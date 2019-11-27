package org.am.producers.maas;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

import java.util.Map;


/**
 * Custom partitioner to ensure that messages are distributed uniformly amongst the partitions.
 * This is just so that it is easier to verify the offset advance when the consumer consumes the messages
 * The default partitioner does not do a deterministic uniform distribution of messages amongst the partitions
 */
public class DeviceIdPartitioner implements Partitioner {
    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        Long deviceId = (Long) key;
        return (int) (deviceId % cluster.partitionCountForTopic(topic));
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}
