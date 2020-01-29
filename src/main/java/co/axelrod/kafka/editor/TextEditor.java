package co.axelrod.kafka.editor;

import co.axelrod.kafka.editor.editor.MainFrame;
import co.axelrod.kafka.editor.editor.file.FileNameField;
import co.axelrod.kafka.editor.editor.text.TextArea;
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
    private static final String DEFAULT_FILE_NAME = "file10";

    // We store file in Kafka topic
    private String fileName;

    @Autowired
    private MainFrame mainFrame;

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
        loadFile();
    }

    public void changeFileName() {
        this.fileName = fileNameField.getText();
        loadFile();
    }

    public void loadFile() {
        keyConsumer.destroy();
        keyStreamProcessor.destroy();

        fileNameField.setText(fileName);
        mainFrame.updateTitle(fileName);
        fileManager.createFile(fileName);

        keyConsumer.start(fileName);
        keyStreamProcessor.start(fileName);
    }

    public void type(Key key) {
        keyProducer.send(fileName, key);
    }
}
