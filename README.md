# Event sourcing based text editor, powered by Kafka

## Demo for Kafka API 

Event sourcing persists the state of a business entity such an Order or a Customer as a sequence of state-changing events.

In our case, we persist key presses as records in Kafka, which allows "replaying" of user commands, and by memory limited only undo/redo operations.

bin/kafka-topics.sh --create \                                                  
--bootstrap-server localhost:9092 \
--replication-factor 1 \
--partitions 1 \
--topic bad-git
    
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic bad-git --from-beginning

delete.topic.enable=true

bin/kafka-configs.sh --zookeeper localhost:2181 --entity-type topics --alter --entity-name bad-git --add-config retention.ms=1000
bin/kafka-topics.sh --zookeeper localhost:2181 --delete --topic bad-git
bin/kafka-topics.sh --bootstrap-server localhost:9092 --describe | grep bad-git

bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic bad-git