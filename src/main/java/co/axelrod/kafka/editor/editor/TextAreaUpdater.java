package co.axelrod.kafka.editor.editor;

import co.axelrod.kafka.editor.kafka.KeyConsumer;
import co.axelrod.kafka.editor.model.Key;
import org.springframework.stereotype.Component;

@Component
public class TextAreaUpdater {
    public TextAreaUpdater(KeyConsumer keyConsumer, DisplayArea displayArea) {
        new Thread(() -> {
            while (true) {
                Key key = keyConsumer.getNextSymbol();

                if (key == null) {
                    continue;
                }

                displayArea.display(String.valueOf(key.getKeyChar()));
//            textEditorWindow.symbols.setText("String.valueOf(keyStreamProcessor.getKeyCount()");
            }
        }).start();
    }
}
