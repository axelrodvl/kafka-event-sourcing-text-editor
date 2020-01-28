package co.axelrod.kafka.editor.editor;

import co.axelrod.kafka.editor.TextEditor;
import co.axelrod.kafka.editor.model.Key;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

@Component
public class TypingArea extends JTextField {
    public TypingArea(TextEditor textEditor) {
        super(20);
        this.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {
                textEditor.type(new Key(e));
            }
            public void keyPressed(KeyEvent e) {
                // Doing nothing
            }
            public void keyReleased(KeyEvent e) {
                // Doing nothing
            }
        });
    }
}
