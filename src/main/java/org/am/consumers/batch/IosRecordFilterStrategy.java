package org.am.consumers.batch;

import org.am.producers.test.AppEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;

public class IosRecordFilterStrategy implements RecordFilterStrategy<Long, AppEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(IosRecordFilterStrategy.class);

    @Override
    public boolean filter(ConsumerRecord<Long, AppEvent> consumerRecord) {
        if (consumerRecord.value().getPlatform().equals(AppEvent.Platform.IOS)) {
            LOGGER.info("Not filtering : {}", consumerRecord);
            return false;
        }
        LOGGER.info("Filtering : {}", consumerRecord);
        return true;
    }
}
