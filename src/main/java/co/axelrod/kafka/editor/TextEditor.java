package co.axelrod.kafka.editor;

import co.axelrod.kafka.editor.editor.file.FileNameField;
import co.axelrod.kafka.editor.kafka.FileManager;
import co.axelrod.kafka.editor.kafka.KeyConsumer;
import co.axelrod.kafka.editor.kafka.KeyProducer;
import co.axelrod.kafka.editor.kafka.KeyStreamProcessor;
import co.axelrod.kafka.editor.model.Key;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class TextEditor {
    private static final String DEFAULT_FILE_NAME = "bad-git";

    // We store file in Kafka topic
    private String fileName;

    @Autowired
    private KeyConsumer keyConsumer;

    @Autowired
    private KeyProducer keyProducer;

    @Autowired
    private KeyStreamProcessor keyStreamProcessor;

    @Autowired
    private FileManager fileManager;

    @Autowired
    private FileNameField fileNameField;

    @PostConstruct
    public void init() {
        this.fileName = DEFAULT_FILE_NAME;
        keyConsumer.start(fileName);
        fileNameField.setText(fileName);
    }

    public void changeFileName() {
        keyConsumer.destroy();
        this.fileName = fileNameField.getText();
        fileManager.createFile(fileName);
        keyConsumer.start(fileName);
    }

    public void type(Key key) {
        keyProducer.send(fileName, key);
    }

    public void replay() {
        keyConsumer.getAllRecordsFromTopic(fileName);
    }
}
