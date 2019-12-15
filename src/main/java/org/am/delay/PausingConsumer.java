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
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;


public class PausingConsumer {

    private static AtomicBoolean partitionPaused = new AtomicBoolean(false);
    private final static Logger LOGGER = LoggerFactory.getLogger(PausingConsumer.class);
    private final KafkaConsumer<Long, String> consumer;
    private final Duration validAfterDuration;
    private final Duration pollIntervalDuration;

    public PausingConsumer(KafkaConsumer<Long, String> consumer, Duration validAfterDuration, Duration pollIntervalDuration) {
        this.consumer = consumer;
        this.validAfterDuration = validAfterDuration;
        this.pollIntervalDuration = pollIntervalDuration;

        // Single partition consumer
        // TODO : how quickly can it jettison messages?
        //Validate.isTrue(consumer.assignment().size() == 1);
    }


    public void consume() {

        while (true) {

            // Resume partitions where messages need to be dequeued
            if (!partitionPaused.get()) {
                if (!consumer.paused().isEmpty()) {
                    LOGGER.info("Resuming partitions... {}", consumer.paused());
                    consumer.resume(consumer.paused());
                }
            }

            ConsumerRecords<Long, String> records = consumer.poll(pollIntervalDuration.toMillis());

            if (records.isEmpty()) {
                //LOGGER.info("No records");
                continue;
            }

            List<Long> offsetList = new ArrayList<>();
            records.forEach(record -> offsetList.add(record.offset()));
            LOGGER.info("Polled records : {}", offsetList);

            for (ConsumerRecord<Long, String> record : records) {


                Instant validAfter = Instant.ofEpochMilli(record.timestamp()).plus(validAfterDuration);
                Instant now = Instant.now();

                LOGGER.info("{} : Queued at {}, To be processing at {}", record.offset(), Instant.ofEpochMilli(record.timestamp()), validAfter);
                if (partitionPaused.get() // This is because a pause() doesn't happen immediately. Noted behaviour
                        || consumer.paused().contains(record.partition())) {
                    LOGGER.info("{} : Partition {} is paused until {}", record.offset(), record.partition(), validAfter);
                    // Assuming that all subsequent messages will also have to wait
                    continue;
                }

                TopicPartition partition = (TopicPartition) consumer.assignment().toArray()[0];

                if(validAfter.compareTo(now) > 0) {

                    long delay = Duration.between(now, validAfter).toMillis();
                    LOGGER.info("{} : Pausing partition, {} for {} ms ", record.offset(), partition, delay);

                    partitionPaused.set(true);

                    // TODO : Understand this
                    // // :-( Observed behaviour : Uncommitted offsets are not returned after resume(until restart of consumer) if seek is not done
                    consumer.seek(partition, record.offset());
                    consumer.pause(Arrays.asList(partition));

                    // set a timer to indicate that the partition has to be resumed once the wait is over
                    TimerTask timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            partitionPaused.set(false);
                            LOGGER.info("Marking for resumption of partition : {}", partition);

                        }
                    };
                    Timer timer = new Timer("timerTask", false);
                    timer.schedule(timerTask, delay);
                } else {
                    LOGGER.info("{} : Delay executed. Pushing message forward for processing...", record.offset());
                    // TODO : Push into another kafka topic
                    consumer.commitSync();
                }
            }
        }
    }
}

