package org.am.delay;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.kafka.annotation.EnableKafka;

import java.time.Duration;
import java.util.Properties;

import static org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.ISOLATION_LEVEL_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;

@EnableKafka
@PropertySource(value = {"classpath:/delay/consumer.properties"})
@Configuration
class ConsumerConfig {

    @Bean
    public PausingConsumer pausingConsumer() {
        Properties consumerProps = new Properties();
        consumerProps.put(BOOTSTRAP_SERVERS_CONFIG, String.valueOf(getProperty("kafka-bootstrap-servers")));
        consumerProps.put(GROUP_ID_CONFIG, "pausing-consumers");
        consumerProps.put(ENABLE_AUTO_COMMIT_CONFIG, "false");
        consumerProps.put(ISOLATION_LEVEL_CONFIG, "read_committed");
        consumerProps.put(KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.LongDeserializer");
        consumerProps.put(VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer"); // TODO : custom serializer needed for custom message
        return new PausingConsumer(consumerProps, Duration.ofSeconds(Long.valueOf(String.valueOf(getProperty("valid-after-duration-in-secs")))), Duration.ofMillis(Long.valueOf(String.valueOf(getProperty("polling-interval-in-millis")))));
    }

    @Bean
    public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public org.springframework.core.env.PropertySource propertySource() {
        org.springframework.core.env.PropertySource propertySource = propertySourcesPlaceholderConfigurer().getAppliedPropertySources().get("environmentProperties");
        if (propertySource == null) {
            throw new RuntimeException("property source not found : environmentProperties");
        }
        return propertySource;
    }

    private Object getProperty(String propertyName) {
        if (!propertySource().containsProperty(propertyName))
            throw new RuntimeException("property not found : " + propertyName);
        return propertySource().getProperty(propertyName);
    }

}
