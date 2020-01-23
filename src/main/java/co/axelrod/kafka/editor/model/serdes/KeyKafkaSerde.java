package co.axelrod.kafka.editor.model.serdes;

import co.axelrod.kafka.editor.model.Key;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

public class KeyKafkaSerde implements Serde<Key> {
    @Override
    public Serializer<Key> serializer() {
        return new KeyKafkaSerializer();
    }

    @Override
    public Deserializer<Key> deserializer() {
        return new KeyKafkaDeserializer();
    }
}
