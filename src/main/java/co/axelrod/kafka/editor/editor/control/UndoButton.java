package co.axelrod.kafka.editor.editor.control;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.swing.*;

@Component
@Qualifier("undo")
public class UndoButton extends JButton {
    public UndoButton() {
        super("Undo");
        this.addActionListener(e -> {

        });
    }
}
