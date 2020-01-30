package co.axelrod.kafka.editor.ui.control;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
@Qualifier("control")
public class ControlPanel extends JPanel {
    public ControlPanel(
            @Qualifier("undo") UndoButton undoButton,
            @Qualifier("redo") RedoButton redoButton,
            @Qualifier("replay") ReplayButton replayButton) {
        super();
        FlowLayout borderLayout = new FlowLayout();
        borderLayout.setAlignment(FlowLayout.TRAILING);
        this.setLayout(borderLayout);
        this.add(undoButton);
        this.add(redoButton);
        this.add(replayButton);
        this.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
    }
}
