package co.axelrod.kafka.editor.editor.control;

import co.axelrod.kafka.editor.TextEditor;
import co.axelrod.kafka.editor.editor.text.DisplayArea;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.swing.*;

@Component
@Qualifier("replay")
public class ReplayButton extends JButton {
    public ReplayButton(TextEditor textEditor, DisplayArea displayArea) {
        super("Replay");
        this.addActionListener(e -> {
            displayArea.setText("");
            displayArea.requestFocusInWindow();
            textEditor.replay();
        });
    }
}
