package co.axelrod.kafka.editor.kafka;

import lombok.AllArgsConstructor;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Properties;

@Component
@AllArgsConstructor
public class FileManager {
    private KafkaProperties kafkaProperties;

    public void createFile(String fileName) {
        Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());

        try (AdminClient admin = KafkaAdminClient.create(properties)) {
            int partitions = 1;
            short replication = 1;
            admin.createTopics(Arrays.asList(new NewTopic(fileName, partitions, replication)));
        }
    }
}
