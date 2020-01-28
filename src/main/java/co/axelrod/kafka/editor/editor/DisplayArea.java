package co.axelrod.kafka.editor.editor;

import co.axelrod.kafka.editor.TextEditor;
import co.axelrod.kafka.editor.model.Key;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

@Component
public class DisplayArea extends JTextArea {
    public DisplayArea(TextEditor textEditor) {
        super();
        this.setEditable(true);
        this.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                textEditor.type(new Key(e));
            }

            @Override
            public void keyPressed(KeyEvent e) {
                // Doing nothing
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // Doing nothing
            }
        });
    }

    public void display(String key) {
        if ("\b".equals(key)) {
            Document doc = this.getDocument();
            try {
                if (doc.getLength() > 1) {
                    doc.remove(doc.getLength() - 1, 1);
                }
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        } else {
            this.append(key);
            this.setCaretPosition(this.getDocument().getLength());
        }
    }
}