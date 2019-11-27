package org.am.consumers.batch;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class BatchConsumerApplication {

    public static void main(String s[]) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(BatchCommitConfig.class);

    }
}
