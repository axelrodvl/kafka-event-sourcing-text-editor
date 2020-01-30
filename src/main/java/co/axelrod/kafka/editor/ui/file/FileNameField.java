package co.axelrod.kafka.editor.ui.file;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.swing.*;

@Component
@Qualifier("fileName")
public class FileNameField extends JTextField {
    public FileNameField() {
        super(20);
    }
}
