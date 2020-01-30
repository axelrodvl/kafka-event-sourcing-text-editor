package co.axelrod.kafka.editor.ui.control;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.swing.*;

@Component
@Qualifier("redo")
public class RedoButton extends JButton {
    public RedoButton() {
        super("Redo");
        this.addActionListener(e -> {

        });
    }
}
