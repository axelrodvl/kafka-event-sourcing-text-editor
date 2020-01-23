package co.axelrod.kafka.editor.kafka;

import co.axelrod.kafka.editor.model.Symbol;
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
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

@Component
@Slf4j
public class MyConsumer {
    private long offset = 0;

    private KafkaConsumer<Long, String> consumer;

    @Getter
    private ConsumerRecords<Long, String> records;

    private Queue<ConsumerRecord<Long, String>> consumedRecords = new LinkedBlockingQueue<>();

    private Iterator<ConsumerRecord<Long, String>> iterator;

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
        props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList("bad-git"));

        taskExecutor.execute(() -> {
            while (true) {
                records = consumer.poll(Duration.ofMillis(Integer.MAX_VALUE));
                for(ConsumerRecord<Long, String> record : records) {
                    consumedRecords.add(record);
                    Thread.yield();
                }
            }
        });
    }

    @Async
    public boolean hasNextSymbol() {
        return true;
//
//        if (iterator == null) {
//            iterator = consumedRecords.iterator();
//        }
//
//        return iterator.hasNext();
    }

    @Async
    public Symbol getNextSymbol() {
//        if (iterator == null) {
//            iterator = consumedRecords.iterator();
//        }
//
//        ConsumerRecord<Long, String> record = iterator.next();

        if(!consumedRecords.isEmpty()) {
            ConsumerRecord<Long, String> record = consumedRecords.remove();
            return new Symbol(record.key(), record.value().charAt(0));
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
        props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        KafkaConsumer<Long, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList("bad-git"));

        ConsumerRecords<Long, String> records = consumer.poll(100);

        for(ConsumerRecord<Long, String> record : records) {
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