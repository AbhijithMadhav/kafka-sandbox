package org.am.delay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

public class Producer {

    private static final Logger LOGGER = LoggerFactory.getLogger(Producer.class);

    private final KafkaTemplate<Long, String> kafkaTemplate;
    private final String topicName;

    public Producer(KafkaTemplate<Long, String> kafkaTemplate, String topicName) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }

    public void sendMessage(String message) {

        ListenableFuture<SendResult<Long, String>> future = kafkaTemplate.send(topicName, message);
        LOGGER.info("Sending message to {} : {}", topicName, message);

        // Asynchronous so that producer is not slowed down
        future.addCallback(new ListenableFutureCallback<SendResult<Long, String>>() {

            @Override
            public void onSuccess(SendResult<Long, String> result) {
                LOGGER.info("Sent message : {}", result);
            }

            @Override
            public void onFailure(Throwable ex) {
                LOGGER.warn("Unable to send message=["
                        + message + "] due to : " + ex.getMessage());
            }
        });
    }
}