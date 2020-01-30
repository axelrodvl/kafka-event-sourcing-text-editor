package co.axelrod.kafka.editor.model.serdes;

import co.axelrod.kafka.editor.model.Key;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

@Slf4j
public class KeyKafkaDeserializer implements Deserializer<Key> {
    @Override
    public Key deserialize(String topic, byte[] data) {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            ObjectInputStream is = new ObjectInputStream(in);
            return (Key) is.readObject();
        } catch (IOException | ClassNotFoundException e) {
            log.error(e.getMessage());
            throw new RuntimeException("Unable to deserialize Key");
        }
    }
}
