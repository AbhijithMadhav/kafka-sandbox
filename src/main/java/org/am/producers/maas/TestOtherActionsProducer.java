package org.am.producers.maas;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

/**
 * Test producer
 */
public class TestOtherActionsProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestOtherActionsProducer.class);
    private KafkaTemplate<Long, AppsEventModel> kafkaTemplate;

    public TestOtherActionsProducer(KafkaTemplate<Long, AppsEventModel> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(final AppsEventModel appsEventModel, final String topicName) {

        ListenableFuture<SendResult<Long, AppsEventModel>> future = kafkaTemplate.send(topicName, appsEventModel.getDeviceId(), appsEventModel);

        // Asynchronous so that producer is not slowed down
        future.addCallback(new ListenableFutureCallback<SendResult<Long, AppsEventModel>>() {

            @Override
            public void onSuccess(SendResult<Long, AppsEventModel> result) {
                LOGGER.debug("Sent message=[{}] with offset=[{}]", appsEventModel, result.getRecordMetadata().offset());
            }

            @Override
            public void onFailure(Throwable ex) {
                LOGGER.error("Unable to send message=[{}]", appsEventModel, ex.getMessage());
            }
        });
    }
}
