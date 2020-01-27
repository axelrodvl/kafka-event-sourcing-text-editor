package co.axelrod.kafka.editor.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Properties;

public class FileManager {
    @Autowired
    private KafkaProperties kafkaProperties;

    public void createFile(String fileName) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, kafkaProperties.getApplicationId());
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());

        try (AdminClient admin = KafkaAdminClient.create(props)) {
            int partitions = 1;
            short replication = 1;
            admin.createTopics(Arrays.asList(new NewTopic(fileName, partitions, replication)));
        }
    }
}
