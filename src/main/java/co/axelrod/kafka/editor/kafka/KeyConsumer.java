package co.axelrod.kafka.editor.kafka;

import co.axelrod.kafka.editor.model.Key;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

@Component
@Slf4j
public class KeyConsumer {
    private KafkaConsumer<Long, Key> consumer;

    @Getter
    private ConsumerRecords<Long, Key> records;

    private Queue<ConsumerRecord<Long, Key>> consumedRecords = new LinkedBlockingQueue<>();

    @Autowired
    private TaskExecutor taskExecutor;

    @PostConstruct
    public void init() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-application");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        props.setProperty("bootstrap.servers", "localhost:9092");
        props.setProperty("group.id", "test2");
        props.setProperty("enable.auto.commit", "true");
        props.setProperty("auto.commit.interval.ms", "1000");
        props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.LongDeserializer");
        props.setProperty("value.deserializer", co.axelrod.kafka.editor.model.serdes.KeyKafkaDeserializer.class.getName());

        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList("bad-git"));

        taskExecutor.execute(() -> {
            while (true) {
                records = consumer.poll(Duration.ofMillis(Integer.MAX_VALUE));
                for (ConsumerRecord<Long, Key> record : records) {
                    consumedRecords.add(record);
                    Thread.yield();
                }
            }
        });
    }

    @Async
    public Key getNextSymbol() {
//        if (iterator == null) {
//            iterator = consumedRecords.iterator();
//        }
//
//        ConsumerRecord<Long, String> record = iterator.next();

        if (!consumedRecords.isEmpty()) {
            ConsumerRecord<Long, Key> record = consumedRecords.remove();
            return record.value();
        } else {
            return null;
        }
    }

    public void getAllRecordsFromTopic() {
        Properties props = new Properties();
        //props.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-application");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        props.setProperty("bootstrap.servers", "localhost:9092");
        props.setProperty("group.id", UUID.randomUUID().toString());
        //props.setProperty("enable.auto.commit", "true");
        //props.setProperty("auto.commit.interval.ms", "1000");
        props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.LongDeserializer");
        props.setProperty("value.deserializer", co.axelrod.kafka.editor.model.serdes.KeyKafkaDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        KafkaConsumer<Long, Key> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList("bad-git"));

        ConsumerRecords<Long, Key> records = consumer.poll(100);

        for (ConsumerRecord<Long, Key> record : records) {
            consumedRecords.add(record);
            Thread.yield();
        }

        consumer.close();
    }

    @PreDestroy
    public void close() {
        consumer.close();
    }
}