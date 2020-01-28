package co.axelrod.kafka.editor.editor.text;

import co.axelrod.kafka.editor.kafka.KeyConsumer;
import co.axelrod.kafka.editor.model.Key;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TextAreaUpdater {
    public TextAreaUpdater(KeyConsumer keyConsumer, TextArea textArea) {
        new Thread(() -> {
            while (true) {
                Key key = keyConsumer.getNextSymbol();
                if (key == null) {
                    continue;
                }
                if (!textArea.isFocusOwner()) {
                    textArea.display(String.valueOf(key.getKeyChar()));
                }
            }
        }).start();
    }
}
