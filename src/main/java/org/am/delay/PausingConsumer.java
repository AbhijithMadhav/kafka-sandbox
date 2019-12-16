package org.am.delay;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;


public class PausingConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PausingConsumer.class);

    private final Properties consumerProperties;
    private final Duration validAfterDuration;
    private final Duration pollIntervalDuration;

    public PausingConsumer(Properties consumerProperties, Duration validAfterDuration, Duration pollIntervalDuration) {
        this.consumerProperties = consumerProperties;
        this.validAfterDuration = validAfterDuration;
        this.pollIntervalDuration = pollIntervalDuration;
    }


     void startPolling(final KafkaConsumer<Long, String> consumer, TopicPartition topicPartition) {

         AtomicBoolean partitionPaused = new AtomicBoolean(false);

         while (true) {

            // Resume partitions where messages need to be dequeued
            if (!partitionPaused.get() && !consumer.paused().isEmpty()) {
                LOGGER.info("Resuming partitions... {}", consumer.paused());
                consumer.resume(consumer.paused());
            }

            ConsumerRecords<Long, String> records = consumer.poll(pollIntervalDuration.toMillis());

            if (records.isEmpty())
                continue;

            List<Long> offsetList = new ArrayList<>();
            records.forEach(record -> offsetList.add(record.offset()));
            LOGGER.info("Polled records : {}", offsetList);

            for (ConsumerRecord<Long, String> record : records) {

                Instant validAfter = Instant.ofEpochMilli(record.timestamp()).plus(validAfterDuration);
                Instant now = Instant.now();

                LOGGER.info("{} : Queued at {}, To be processing at {}", record.offset(), Instant.ofEpochMilli(record.timestamp()), validAfter);
                if (partitionPaused.get() // This is because a pause() doesn't happen immediately. Noted behaviour
                        || consumer.paused().contains(topicPartition)) {
                    LOGGER.info("{} : Partition {} is paused until {}", record.offset(), record.partition(), validAfter);
                    // Assuming that all subsequent messages will also have to wait
                    continue;
                }

                if(validAfter.compareTo(now) > 0) {

                    long delay = Duration.between(now, validAfter).toMillis();
                    LOGGER.info("{} : Pausing partition, {} for {} ms ", record.offset(), topicPartition, delay);

                    partitionPaused.set(true);

                    // TODO : Understand this
                    // // :-( Observed behaviour : Uncommitted offsets are not returned after resume(until restart of consumer) if seek is not done
                    consumer.seek(topicPartition, record.offset());
                    consumer.pause(Arrays.asList(topicPartition));

                    // set a timer to indicate that the partition has to be resumed once the wait is over
                    TimerTask timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            partitionPaused.set(false);
                            LOGGER.info("Marking for resumption of partition : {}", topicPartition);

                        }
                    };
                    Timer timer = new Timer("timerTask", false);
                    timer.schedule(timerTask, delay);

                } else {
                    LOGGER.info("{} : Delay executed. Pushing message forward for processing...", record.offset());

                    // The message can be pushed into another kafka topic here

                    consumer.commitSync();
                }
            }
        }
    }

    public void consume(final TopicPartition topicPartition) {

        try (KafkaConsumer<Long, String> consumer = new KafkaConsumer<>(consumerProperties)) {

            // Manual assignment
            consumer.assign(Collections.singleton(topicPartition));

            startPolling(consumer, topicPartition);
        }
    }
}

