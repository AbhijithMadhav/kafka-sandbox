package org.am.consumers.selective_commit;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TestConsumerApplication {

    public static void main(String s[]) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(TestConsumerKafkaConfig.class);
    }
}
