package co.axelrod.kafka.editor.editor.file;

import co.axelrod.kafka.editor.TextEditor;
import co.axelrod.kafka.editor.editor.text.TextArea;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.swing.*;

@Component
@Qualifier("changeFile")
public class ChangeFileButton extends JButton {
    public ChangeFileButton(TextEditor textEditor, TextArea textArea) {
        super("Change file");
        this.addActionListener(e -> {
            textEditor.changeFileName();
            textArea.setText("");
            textArea.requestFocusInWindow();
        });
    }
}
