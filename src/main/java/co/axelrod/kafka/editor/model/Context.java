package co.axelrod.kafka.editor.model;

import co.axelrod.kafka.editor.kafka.FileManager;
import co.axelrod.kafka.editor.kafka.KeyConsumer;
import lombok.Getter;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Getter
@Component
public class Context {
    private static final String DEFAULT_FILE_NAME = "bad-git";

    private FileManager fileManager;
    private ApplicationContext applicationContext;

    private KeyConsumer keyConsumer;

    // We store file in Kafka topic
    private String fileName;

    public Context(FileManager fileManager, ApplicationContext applicationContext) {
        this.fileManager = fileManager;
        this.applicationContext = applicationContext;
        this.fileName = DEFAULT_FILE_NAME;
        keyConsumer = applicationContext.getBean(KeyConsumer.class, this);
    }

    public void changeFileName(String fileName) {
        keyConsumer.destroy();
        this.fileName = fileName;
        fileManager.createFile(fileName);
        keyConsumer = applicationContext.getBean(KeyConsumer.class, this);
    }
}
