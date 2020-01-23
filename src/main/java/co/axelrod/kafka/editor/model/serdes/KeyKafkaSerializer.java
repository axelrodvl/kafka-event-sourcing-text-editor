package co.axelrod.kafka.editor.model.serdes;

import co.axelrod.kafka.editor.model.Key;
import org.apache.kafka.common.serialization.Serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class KeyKafkaSerializer implements Serializer<Key> {
    @Override
    public byte[] serialize(String topic, Key data) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(data);
            out.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                throw new RuntimeException("Unable to close ByteArray");
            }
        }
        throw new RuntimeException("Unable to serialize key event");
    }
}
