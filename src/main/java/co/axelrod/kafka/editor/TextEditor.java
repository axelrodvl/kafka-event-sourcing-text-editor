package co.axelrod.kafka.editor;

import co.axelrod.kafka.editor.kafka.FileManager;
import co.axelrod.kafka.editor.kafka.KeyConsumer;
import co.axelrod.kafka.editor.kafka.KeyProducer;
import co.axelrod.kafka.editor.kafka.KeyStreamProcessor;
import co.axelrod.kafka.editor.model.Key;
import co.axelrod.kafka.editor.ui.file.FileNameField;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class TextEditor {
    private static final String DEFAULT_FILE_NAME = "t5";

    private String fileName = DEFAULT_FILE_NAME;

    private final KeyConsumer keyConsumer;
    private final KeyProducer keyProducer;
    private final KeyStreamProcessor keyStreamProcessor;
    private final FileManager fileManager;
    private final FileNameField fileNameField;

    @PostConstruct
    public void init() {
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
        fileManager.createFile(fileName);

        keyConsumer.start(fileName);
        keyStreamProcessor.start(fileName);
    }

    public void type(Key key) {
        keyProducer.send(fileName, key);
    }
}
