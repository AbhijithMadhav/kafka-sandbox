package org.am.producers.maas;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.stream.IntStream;

/**
 * Integration test to check kafka configuration and whether the consumers can consumer messages
 */
public class KafkaTestProducerIntTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaTestProducerIntTest.class);
    public static void main(String s[]) throws InterruptedException {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(PropertiesConfig.class, TestOtherActionsConfig.class);

        TestOtherActionsProducer producer = applicationContext.getBean(TestOtherActionsProducer.class);
        String topicName = ((AnnotationConfigApplicationContext) applicationContext).getBeanFactory().resolveEmbeddedValue("${kafka.ios.app.dist.other.actions.topic}");

        // Produce two messages per partition
        IntStream.range(0, 32).forEach(i -> producer.sendMessage(new AppsEventModel("guid1", 123456L, (long) i), topicName));

        Thread.sleep(10000);

    }
}
