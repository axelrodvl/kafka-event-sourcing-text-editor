package co.axelrod.kafka.editor.kafka;

import co.axelrod.kafka.editor.model.Key;
import co.axelrod.kafka.editor.model.serdes.KeyKafkaDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

@Component
@Slf4j
public class KeyConsumer implements DisposableBean {
    private final Properties properties = new Properties();

    private KafkaConsumer<String, Key> consumer;
    private final Queue<ConsumerRecord<String, Key>> consumedRecords = new LinkedBlockingQueue<>();

    private ConsumerTask consumerTask;
    private Thread consumerThread;

    public KeyConsumer(KafkaProperties kafkaProperties) {
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        properties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KeyKafkaDeserializer.class.getName());
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    }

    public void start(String fileName) {
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());

        consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Arrays.asList(fileName));

        consumerTask = new ConsumerTask();
        consumerThread = new Thread(consumerTask);
        consumerThread.start();
    }

    private class ConsumerTask implements Runnable {
        private volatile boolean running = true;

        @Override
        public void run() {
            while (running) {
                consumer.poll(Duration.ofMillis(100)).forEach(consumedRecords::add);
            }
            consumer.commitSync();
            consumer.close();
        }

        public void destroy() {
            running = false;
        }
    }

    public Optional<Key> getNextSymbol() {
        return Optional.of(consumedRecords.remove().value());
    }

    @Override
    public void destroy() {
        if (consumerTask != null) {
            consumerTask.destroy();
            try {
                consumerThread.join();
            } catch (InterruptedException e) {
                log.error(e.getMessage());
                Thread.currentThread().interrupt();
            }
            consumedRecords.clear();
        }
    }

    public void undo() {
        consumedRecords.remove(); // TODO
    }
}