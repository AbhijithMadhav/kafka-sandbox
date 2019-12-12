package org.am.consumers.pause;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ConsumerConfig {

    @Bean
    public PausingConsumer pausingConsumer() {
        return new PausingConsumer();
    }
}
