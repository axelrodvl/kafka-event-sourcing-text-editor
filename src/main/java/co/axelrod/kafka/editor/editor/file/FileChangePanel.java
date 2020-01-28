package co.axelrod.kafka.editor.editor.file;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
@Qualifier("fileChange")
public class FileChangePanel extends JPanel {
    public FileChangePanel(
            @Qualifier("changeFile") JButton changeFileButton,
            @Qualifier("fileName") JTextField fileNameField) {
        super();
        FlowLayout topLayout = new FlowLayout();
        topLayout.setAlignment(FlowLayout.TRAILING);

        JLabel fileNameLabel = new JLabel("File name");

        this.setLayout(topLayout);
        this.add(fileNameLabel);
        this.add(fileNameField);
        this.add(changeFileButton);
        this.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
    }
}
