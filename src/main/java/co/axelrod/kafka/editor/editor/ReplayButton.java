package co.axelrod.kafka.editor.editor;

import co.axelrod.kafka.editor.TextEditor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.swing.*;

@Component
@Qualifier("replay")
public class ReplayButton extends JButton {
    public ReplayButton(TextEditor textEditor, DisplayArea displayArea, TypingArea typingArea) {
        super("Replay");
        this.addActionListener(e -> {
            displayArea.setText("");
            typingArea.setText("");
            typingArea.requestFocusInWindow();
            textEditor.replay();
        });
    }
}
