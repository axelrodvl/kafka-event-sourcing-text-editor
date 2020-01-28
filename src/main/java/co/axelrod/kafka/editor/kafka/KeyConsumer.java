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
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

@Component
@Slf4j
public class KeyConsumer implements DisposableBean {
    private KafkaConsumer<String, Key> consumer;

    @Getter
    private ConsumerRecords<String, Key> records;

    private Queue<ConsumerRecord<String, Key>> consumedRecords = new LinkedBlockingQueue<>();

    @Autowired
    private KafkaProperties kafkaProperties;

    private ConsumerTask consumerTask;

    public void start(String fileName) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KeyKafkaDeserializer.class.getName());

        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList(fileName));

        consumerTask = new ConsumerTask();
        new Thread(consumerTask).start();
    }

    private class ConsumerTask implements Runnable {
        public Boolean running = true;

        @Override
        public void run() {
            while (running) {
                records = consumer.poll(Duration.ofMillis(Integer.MAX_VALUE));
                for (ConsumerRecord<String, Key> record : records) {
                    consumedRecords.add(record);
                    if (!running) {
                        break;
                    }
                    Thread.yield();
                }
            }
            consumer.close();
            consumedRecords.clear();
        }
    }

    public Key getNextSymbol() {
        if (!consumedRecords.isEmpty()) {
            ConsumerRecord<String, Key> record = consumedRecords.remove();
            return record.value();
        } else {
            return null;
        }
    }

    public void getAllRecordsFromTopic(String fileName) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KeyKafkaDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        KafkaConsumer<String, Key> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList(fileName));

        ConsumerRecords<String, Key> records = consumer.poll(100);

        for (ConsumerRecord<String, Key> record : records) {
            consumedRecords.add(record);
            Thread.yield();
        }

        consumer.close();
    }

    @Override
    public void destroy() {
        consumerTask.running = false;
    }

    public void undo() {
        consumedRecords.remove(); // TODO
    }
}