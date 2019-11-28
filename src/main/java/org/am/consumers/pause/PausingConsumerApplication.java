package org.am.consumers.pause;

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
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.ISOLATION_LEVEL_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;

public class PausingConsumerApplication {

    // List of partitions to which this consumer instance listens
    // TODO : Need to dynamically determine partition assignment
    private static AtomicBoolean partitionPaused = new AtomicBoolean(false);
    private final static Logger LOGGER = LoggerFactory.getLogger(PausingConsumerApplication.class);

    public static void main(String s[]) {

        Properties props = new Properties();
        props.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(GROUP_ID_CONFIG, "pausing-consumers");
        props.put(ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ISOLATION_LEVEL_CONFIG, "read_committed");
        props.put(KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.LongDeserializer");
        props.put(VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.LongDeserializer"); // TODO : custom serializer needed for custom message
        KafkaConsumer<Long, Long> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList(TestProducerApplication.TOPIC));
        while (true) {

            // Resume partitions where messages need to be dequeued
            if (!partitionPaused.get()) {
                if (!consumer.paused().isEmpty()) {
                    LOGGER.info("Resuming partitions... {}", consumer.paused());
                    consumer.resume(consumer.paused());
                }
            }

            ConsumerRecords<Long, Long> records = consumer.poll(1000);

            if (records.isEmpty()) {
                //LOGGER.info("No records");
                continue;
            }

            List<Long> offsetList = new ArrayList<>();
            records.forEach(record -> offsetList.add(record.offset()));
            LOGGER.info("Polled records : {}", offsetList);

            for (ConsumerRecord<Long, Long> record : records) {

                Instant validAfter = Instant.ofEpochMilli(record.value());
                Instant now = Instant.now();

                LOGGER.info("{} : To be processing at : {}", record.offset(), validAfter);
                if (partitionPaused.get() // This is because a pause() doesn't happen immediately. Noted behaviour
                        || consumer.paused().contains(record.partition())) {
                    LOGGER.info("{} : Partition {} is paused until {}", record.offset(), record.partition(), validAfter);
                    // Assuming that all subsequent messages will also have to wait
                    continue;
                }

                TopicPartition partition = new TopicPartition("multi-partition-test-topic", record.partition());

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
                    LOGGER.info("{} : Delay executed. Committing...", record.offset());
                    // TODO : Push into another kafka topic
                    consumer.commitSync();
                }
            }
        }
    }
}

