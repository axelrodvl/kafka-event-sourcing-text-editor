package co.axelrod.kafka.editor.editor.state;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.swing.*;

@Component
@Qualifier("symbolsCount")
public class SymbolsCountLabel extends JLabel {
    public SymbolsCountLabel() {
        super("Symbols");
    }
}
