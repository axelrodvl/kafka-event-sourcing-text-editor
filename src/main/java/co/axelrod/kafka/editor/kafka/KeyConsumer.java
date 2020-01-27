package co.axelrod.kafka.editor.kafka;

import co.axelrod.kafka.editor.model.Key;
import co.axelrod.kafka.editor.model.serdes.KeyKafkaDeserializer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
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
    private KafkaConsumer<String, Key> consumer;

    @Getter
    private ConsumerRecords<String, Key> records;

    private Queue<ConsumerRecord<String, Key>> consumedRecords = new LinkedBlockingQueue<>();

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private KafkaProperties kafkaProperties;

    @PostConstruct
    public void init() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test2");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KeyKafkaDeserializer.class.getName());

        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList("bad-git"));

        taskExecutor.execute(() -> {
            while (true) {
                records = consumer.poll(Duration.ofMillis(Integer.MAX_VALUE));
                for (ConsumerRecord<String, Key> record : records) {
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
            ConsumerRecord<String, Key> record = consumedRecords.remove();
            return record.value();
        } else {
            return null;
        }
    }

    public void getAllRecordsFromTopic() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KeyKafkaDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        KafkaConsumer<String, Key> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList("bad-git"));

        ConsumerRecords<String, Key> records = consumer.poll(100);

        for (ConsumerRecord<String, Key> record : records) {
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