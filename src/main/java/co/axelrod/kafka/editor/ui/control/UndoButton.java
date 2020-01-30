package co.axelrod.kafka.editor.ui.control;

import co.axelrod.kafka.editor.kafka.KeyConsumer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.swing.*;

@Component
@Qualifier("undo")
public class UndoButton extends JButton {
    public UndoButton(KeyConsumer keyConsumer) {
        super("Undo");
        this.addActionListener(e -> keyConsumer.undo());
    }
}
