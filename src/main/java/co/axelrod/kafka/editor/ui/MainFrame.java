package co.axelrod.kafka.editor.ui;

import co.axelrod.kafka.editor.ui.text.TextScrollPane;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
@Slf4j
public class MainFrame extends JFrame implements InitializingBean {
    @Value("${spring.application.name}")
    private String titlePrefix;

    private final TextScrollPane textScrollPane;
    private final JPanel fileChangePanel;
    private final JPanel controlPanel;
    private final JPanel statePanel;

    public MainFrame(TextScrollPane textScrollPane,
                     @Qualifier("fileChange") JPanel fileChangePanel,
                     @Qualifier("control") JPanel controlPanel,
                     @Qualifier("state") JPanel statePanel
    ) {
        this.textScrollPane = textScrollPane;
        this.fileChangePanel = fileChangePanel;
        this.controlPanel = controlPanel;
        this.statePanel = statePanel;
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private void createAndShowGUI() {
        this.setTitle(titlePrefix);
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
        SwingUtilities.invokeLater(this::createAndShowGUI);
    }

    public void updateTitle(String newTitle) {
        this.setTitle(titlePrefix + " - " + newTitle);
    }
}