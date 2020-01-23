package co.axelrod.kafka.editor.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.admin.NewTopic;

import java.util.Arrays;
import java.util.Properties;

public class FileManager {
    public void createFile(String fileName) {
        Properties config = new Properties();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        try (AdminClient admin = KafkaAdminClient.create(config)) {
            int partitions = 1;
            short replication = 1;
            admin.createTopics(Arrays.asList(new NewTopic(fileName, partitions, replication)));
        }
    }
}
