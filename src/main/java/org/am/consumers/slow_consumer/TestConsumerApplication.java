package org.am.consumers.slow_consumer;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TestConsumerApplication {

    public static void main(String s[]) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(TestConsumerKafkaConfig.class);
    }
}
