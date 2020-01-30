package co.axelrod.kafka.editor.ui.state;

import co.axelrod.kafka.editor.kafka.KeyStreamProcessor;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.swing.*;

@Component
@Qualifier("wordsCount")
public class WordsCountLabel extends JLabel implements Runnable, DisposableBean {
    private volatile boolean running = true;

    private static final String LABEL = "Total words: ";

    private final KeyStreamProcessor keyStreamProcessor;

    public WordsCountLabel(KeyStreamProcessor keyStreamProcessor) {
        super(LABEL + 0);
        this.keyStreamProcessor = keyStreamProcessor;
        new Thread(this).start();
    }

    @Override
    public void run() {
        while (running) {
            setText(LABEL + keyStreamProcessor.getWordsCount());
        }
    }

    @Override
    public void destroy() {
        running = false;
    }
}