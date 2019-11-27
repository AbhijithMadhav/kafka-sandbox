package org.am.consumers.batch;

import org.am.producers.test.AppEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BatchConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchConsumer.class);

    private static AtomicInteger count =  new AtomicInteger(0);
    @KafkaListener(topics = "multi-partition-test-topic", containerFactory = "kafkaListenerContainerFactory")
    public void listen(List<ConsumerRecord<Long, AppEvent>> consumerRecords) {
        LOGGER.info("Received {} message/s in group batch-consumers", consumerRecords.size());

        throw new RuntimeException();
        /*consumerRecords.forEach(consumerRecord -> {
            count.incrementAndGet();
            LOGGER.info("Processing {} message at offset {}", count, consumerRecord.offset());
            if (count.get() >= 3) {
                throw new RuntimeException();
            }
        });*/
    }
}
