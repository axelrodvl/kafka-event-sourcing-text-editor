package co.axelrod.kafka.editor.model.serdes;

import co.axelrod.kafka.editor.model.Key;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class KeyKafkaDeserializer implements Deserializer<Key> {
    @Override
    public Key deserialize(String topic, byte[] data) {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            ObjectInputStream is = new ObjectInputStream(in);
            return (Key) is.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to deserialize Key");
        }
    }
}
