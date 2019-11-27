package org.am.consumers.pause;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class PausingConsumerApplication {

    // List of partitions to which this consumer instance listens
    // TODO : Need to dynamically determine partition assignment
    private static AtomicBoolean partitionPaused = new AtomicBoolean(false);
    private final static Logger LOGGER = LoggerFactory.getLogger(PausingConsumerApplication.class);

    public static void main(String s[]) {

        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "pausing-consumers");
        props.put("enable.auto.commit", "false");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.LongDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.LongDeserializer");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
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

            ConsumerRecords<Long, Long> records = consumer.poll(250);

            if (records.isEmpty()) {
                LOGGER.info("No records");
                continue;
            }

            LOGGER.info("Polled records : {}", records);
            records.forEach(record -> LOGGER.info(String.valueOf(record.offset())));
            for (ConsumerRecord<Long, Long> record : records) {

                LOGGER.info("Processing {}", record);
                if (consumer.paused().contains(record.partition())) {
                    LOGGER.info("Partition {} is paused. Not processing {}", record.partition(), record);
                    continue;
                }

                TopicPartition partition = new TopicPartition("multi-partition-test-topic", record.partition());

                Instant validAfter = Instant.ofEpochMilli(record.value());
                Instant now = Instant.now();
                if(validAfter.compareTo(now) > 0) {
                    long delay = Duration.between(now, validAfter).toMillis();
                    LOGGER.info("Pausing partition, {} for {} ms", partition, delay);
                    consumer.pause(Arrays.asList(partition));
                    partitionPaused.set(true);

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
                    LOGGER.info("Delay executed. Pushing this out, {}", record);
                    // TODO : Pump into another kafka topic
                    consumer.commitSync();
                }
            }
        }
    }
}

