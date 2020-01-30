package co.axelrod.kafka.editor.ui.control;

import co.axelrod.kafka.editor.TextEditor;
import co.axelrod.kafka.editor.ui.text.TextArea;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.swing.*;

@Component
@Qualifier("replay")
public class ReplayButton extends JButton {
    public ReplayButton(TextEditor textEditor, TextArea textArea) {
        super("Replay");
        this.addActionListener(e -> {
            textArea.setText("");
            textEditor.loadFile();
        });
    }
}
