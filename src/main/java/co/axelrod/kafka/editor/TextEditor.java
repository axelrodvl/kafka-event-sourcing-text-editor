package co.axelrod.kafka.editor;

import co.axelrod.kafka.editor.editor.TextAreaUpdater;
import co.axelrod.kafka.editor.kafka.FileManager;
import co.axelrod.kafka.editor.kafka.KeyConsumer;
import co.axelrod.kafka.editor.kafka.KeyProducer;
import co.axelrod.kafka.editor.kafka.KeyStreamProcessor;
import co.axelrod.kafka.editor.model.Key;
import co.axelrod.kafka.editor.editor.TextEditorWindow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;

@Component
public class TextEditor {
    private static final String DEFAULT_FILE_NAME = "bad-git";

    @Autowired
    private TextEditorWindow window;

    private JTextArea displayArea;
    private JTextField typingArea;

    @Autowired
    private KeyConsumer keyConsumer;

    @Autowired
    private KeyProducer keyProducer;

    @Autowired
    private KeyStreamProcessor keyStreamProcessor;

    @Autowired
    private FileManager fileManager;

    @PostConstruct
    public void init() {
        this.fileName = DEFAULT_FILE_NAME;
        //window.init();
        keyConsumer.start(fileName);

    }

    // We store file in Kafka topic
    private String fileName;

    public void changeFileName() {
        keyConsumer.destroy();
        this.fileName = window.fileNameField.getText();
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
