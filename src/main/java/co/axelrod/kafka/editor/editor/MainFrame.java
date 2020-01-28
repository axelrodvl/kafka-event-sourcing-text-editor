package co.axelrod.kafka.editor.editor;

import co.axelrod.kafka.editor.editor.text.TextScrollPane;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
@Slf4j
public class MainFrame extends JFrame implements InitializingBean {
    @Value("${spring.application.name}")
    private String title;

    @Autowired
    private TextScrollPane textScrollPane;

    @Autowired
    @Qualifier("fileChange")
    private JPanel fileChangePanel;

    @Autowired
    @Qualifier("control")
    private JPanel controlPanel;

    @Autowired
    @Qualifier("state")
    private JPanel statePanel;

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private void createAndShowGUI() {
        this.setTitle(title);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addComponentsToPane();
        this.pack();
        this.setVisible(true);
    }

    private static void addPanel(JComponent panel, Container container) {
        panel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        container.add(panel);
    }

    private void addComponentsToPane() {
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
        addPanel(fileChangePanel, getContentPane());
        addPanel(textScrollPane, getContentPane());
        addPanel(controlPanel, getContentPane());
        addPanel(statePanel, getContentPane());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        javax.swing.SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    public void updateTitle(String newTitle) {
        this.setTitle(title + " - " + newTitle);
    }
}