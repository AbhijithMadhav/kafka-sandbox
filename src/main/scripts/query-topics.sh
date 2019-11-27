#!/bin/bash
BASE_DIR=/Users/amadhav/installations/kafka_2.12-2.2.0
${BASE_DIR}/bin/kafka-topics.sh --zookeeper localhost:2181 --topic multi-partition-test-topic --describe