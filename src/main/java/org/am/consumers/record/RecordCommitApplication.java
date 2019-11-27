package org.am.consumers.record;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class RecordCommitApplication {

    public static void main(String s[]) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(RecordCommitConfig.class);
    }
}
