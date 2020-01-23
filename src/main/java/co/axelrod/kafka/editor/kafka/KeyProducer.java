package co.axelrod.kafka.editor.kafka;

import co.axelrod.kafka.editor.model.Key;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Properties;

@Component
public class KeyProducer {
    private Producer<Long, Key> producer;

    @PostConstruct
    public void init() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-application");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.LongSerializer");
        props.put("value.serializer", co.axelrod.kafka.editor.model.serdes.KeyKafkaSerializer.class.getName());

        producer = new KafkaProducer<>(props);
    }

    public void send(String fileName, long timestamp, Key key) {
        producer.send(new ProducerRecord<>("bad-git", timestamp, key));
    }

    @PreDestroy
    public void close() {
        producer.close();
    }
}