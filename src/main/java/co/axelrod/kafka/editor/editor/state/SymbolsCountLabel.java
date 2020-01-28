package co.axelrod.kafka.editor.editor.state;

import co.axelrod.kafka.editor.kafka.KeyStreamProcessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.swing.*;

@Component
@Qualifier("symbolsCount")
public class SymbolsCountLabel extends JLabel {
    private static final String LABEL = "Total symbols: ";

    public SymbolsCountLabel(KeyStreamProcessor keyStreamProcessor) {
        super(LABEL + 0);
        new Thread(() -> {
            while (true) {
                setText(LABEL + keyStreamProcessor.getKeyCount());
            }
        });
    }
}
