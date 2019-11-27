package org.am.consumers.manual;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ManualCommitApplication {

    public static void main(String s[]) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(ManualCommitConfig.class);
    }
}
