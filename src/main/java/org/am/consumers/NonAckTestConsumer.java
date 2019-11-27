package org.am.consumers;

import org.am.producers.test.AppEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;

import java.util.concurrent.atomic.AtomicInteger;

public class NonAckTestConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(NonAckTestConsumer.class);

    public static AtomicInteger count = new AtomicInteger(0);
    @KafkaListener(topics = "multi-partition-test-topic", containerFactory = "kafkaListenerContainerFactory")
    public void listen(ConsumerRecord<Long, AppEvent> consumerRecord) {
        count.incrementAndGet();
        LOGGER.info("Received message with offset : {}, count : {}", consumerRecord.offset(), count);
        if (count.get() == 3) {
            LOGGER.info("Erroring out");
            throw new RuntimeException();
        }
    }
}
