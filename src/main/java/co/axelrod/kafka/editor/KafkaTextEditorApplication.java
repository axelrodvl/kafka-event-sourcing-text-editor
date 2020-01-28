package co.axelrod.kafka.editor;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class KafkaTextEditorApplication {
    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(KafkaTextEditorApplication.class);
        builder.headless(false);
        builder.run(args);
    }
}
