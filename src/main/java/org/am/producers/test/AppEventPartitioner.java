package org.am.producers.test;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class AppEventPartitioner implements Partitioner {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppEventPartitioner.class);

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        //LOGGER.info("Key is : " + key);
        Long id = (Long) key;
        return Math.abs(id.hashCode()) % cluster.availablePartitionsForTopic(topic).size();
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}
