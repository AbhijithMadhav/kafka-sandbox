package org.am.consumers.slow_consumer;

import org.am.producers.test.AppEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;

public class TestConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestConsumer.class);

    @KafkaListener(topics = "multi-partition-test-topic", containerFactory = "kafkaListenerContainerFactory", errorHandler = "testConsumerErrorhandler")
    public void listen(ConsumerRecord<Long, AppEvent> consumerRecord) throws InterruptedException {
        LOGGER.info("Received message : {}. Now sleeping for 10000 secs", consumerRecord);
        Thread.sleep(10000);
        LOGGER.info("Done");
    }
}
