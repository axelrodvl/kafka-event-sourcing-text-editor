package co.axelrod.kafka.editor.kafka;

import co.axelrod.kafka.editor.model.Key;
import co.axelrod.kafka.editor.model.serdes.KeyKafkaSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class KeyProducer implements InitializingBean, DisposableBean {
    private final Properties properties = new Properties();

    private Producer<String, Key> producer;

    public KeyProducer(KafkaProperties kafkaProperties) {
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KeyKafkaSerializer.class.getName());
    }

    @Override
    public void afterPropertiesSet() {
        producer = new KafkaProducer<>(properties);
    }

    public void send(String fileName, Key key) {
        producer.send(new ProducerRecord<>(fileName, "key", key));
    }

    @Override
    public void destroy() {
        producer.close();
    }
}