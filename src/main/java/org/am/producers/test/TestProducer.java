package org.am.producers.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

public class TestProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestProducer.class);

    @Autowired
    private KafkaTemplate<Long, AppEvent> kafkaTemplate;

    public void sendMessage(final AppEvent appEvent, final String topicName) {

        ListenableFuture<SendResult<Long, AppEvent>> future = kafkaTemplate.send(topicName, appEvent.getDeviceId(), appEvent);
        LOGGER.info("Sending message to {} : {}", topicName,  appEvent);

        // Asynchronous so that producer is not slowed down
        future.addCallback(new ListenableFutureCallback<SendResult<Long, AppEvent>>() {

            @Override
            public void onSuccess(SendResult<Long, AppEvent> result) {
                LOGGER.info("Sent message : {}", result);
            }

            @Override
            public void onFailure(Throwable ex) {
                LOGGER.warn("Unable to send message=["
                        + appEvent + "] due to : " + ex.getMessage());
            }
        });
    }
}