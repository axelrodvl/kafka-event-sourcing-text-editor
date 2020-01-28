package co.axelrod.kafka.editor.editor.file;

import co.axelrod.kafka.editor.TextEditor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.swing.*;

@Component
@Qualifier("changeFile")
public class ChangeFileButton extends JButton {
    public ChangeFileButton(TextEditor textEditor) {
        super("Change file");
        this.addActionListener(e -> {
            textEditor.changeFileName();
        });
    }
}
