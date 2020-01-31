package co.axelrod.kafka.editor.ui.text;

import co.axelrod.kafka.editor.kafka.KeyConsumer;
import co.axelrod.kafka.editor.model.Key;
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
            Key key = keyConsumer.getNextSymbol();

            if (key == null) {
                continue;
            }

            if (!textArea.isFocusOwner()) {
                textArea.display(String.valueOf(key.getKeyChar()));
            }
        }
    }

    @Override
    public void destroy() {
        running = false;
    }
}
