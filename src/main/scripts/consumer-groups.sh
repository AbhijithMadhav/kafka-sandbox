#!/bin/bash

BASE_DIR=/Users/amadhav/installations/kafka_2.12-2.2.0
${BASE_DIR}/bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092  --describe --group other-group
${BASE_DIR}/bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092  --describe --group autocommit-consumers