package co.axelrod.kafka.editor.ui.text;

import org.springframework.stereotype.Component;

import javax.swing.*;

@Component
public class TextScrollPane extends JScrollPane {
    public TextScrollPane(TextArea textArea) {
        super(textArea);
    }
}
