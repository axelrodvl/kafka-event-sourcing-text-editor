package co.axelrod.kafka.editor.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("kafka")
@Getter
@Setter
public class KafkaProperties {
    private String bootstrapServers;
    private String applicationId;
}
