package co.axelrod.kafka.editor.ui.state;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
@Qualifier("state")
public class StatePanel extends JPanel {
    public StatePanel(
            @Qualifier("symbolsCount") JLabel symbolsCountLabel,
            @Qualifier("wordsCount") JLabel wordsCountLabel) {
        super();
        FlowLayout topLayout = new FlowLayout();
        topLayout.setAlignment(FlowLayout.TRAILING);

        this.setLayout(topLayout);
        this.add(symbolsCountLabel);
        this.add(wordsCountLabel);
        this.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
    }
}
