package co.axelrod.kafka.editor.editor.file;

import co.axelrod.kafka.editor.TextEditor;
import co.axelrod.kafka.editor.editor.text.DisplayArea;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.swing.*;

@Component
@Qualifier("changeFile")
public class ChangeFileButton extends JButton {
    public ChangeFileButton(TextEditor textEditor, DisplayArea displayArea) {
        super("Change file");
        this.addActionListener(e -> {
            textEditor.changeFileName();
            displayArea.setText("");
            displayArea.requestFocusInWindow();
        });
    }
}
