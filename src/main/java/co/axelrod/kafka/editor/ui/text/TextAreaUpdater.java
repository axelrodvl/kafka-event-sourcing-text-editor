package co.axelrod.kafka.editor.ui.text;

import co.axelrod.kafka.editor.kafka.KeyConsumer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

@Component
public class TextAreaUpdater implements Runnable, DisposableBean {
    private volatile boolean running = true;

    private final KeyConsumer keyConsumer;
    private final TextArea textArea;

    public TextAreaUpdater(KeyConsumer keyConsumer, TextArea textArea) {
        this.keyConsumer = keyConsumer;
        this.textArea = textArea;
        new Thread(this).start();
    }

    @Override
    public void run() {
        while (running) {
            keyConsumer.getNextSymbol().ifPresent(key -> {
                if (!textArea.isFocusOwner()) {
                    textArea.display(String.valueOf(key.getKeyChar()));
                }
            });
        }
    }

    @Override
    public void destroy() {
        running = false;
    }
}
