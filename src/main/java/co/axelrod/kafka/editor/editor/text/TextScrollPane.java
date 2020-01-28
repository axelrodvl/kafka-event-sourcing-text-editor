package co.axelrod.kafka.editor.editor.text;

import org.springframework.stereotype.Component;

import javax.swing.*;

@Component
public class TextScrollPane extends JScrollPane {
    public TextScrollPane(DisplayArea displayArea) {
        super(displayArea);
    }
}
