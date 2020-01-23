package co.axelrod.kafka.editor.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Context {
    // We store file in Kafka topic
    private String fileName;
}
