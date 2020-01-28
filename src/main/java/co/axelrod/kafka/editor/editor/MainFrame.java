package co.axelrod.kafka.editor.editor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
@Slf4j
public class MainFrame extends JFrame implements InitializingBean {
    @Autowired
    private JTextArea displayArea;

    @Autowired
    @Qualifier("fileChange")
    private JPanel fileChangePanel;

    @Autowired
    @Qualifier("control")
    private JPanel controlPanel;

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private void createAndShowGUI() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addComponentsToPane();
        this.pack();
        this.setVisible(true);
    }

    private void addComponentsToPane() {
        getContentPane().add(fileChangePanel, BorderLayout.PAGE_START);

        //Uncomment this if you wish to turn off focus
        //traversal.  The focus subsystem consumes
        //focus traversal keys, such as Tab and Shift Tab.
        //If you uncomment the following line of code, this
        //disables focus traversal and the Tab events will
        //become available to the key event listener.
        //typingArea.setFocusTraversalKeysEnabled(false);

        JScrollPane scrollPane = new JScrollPane(displayArea);
        scrollPane.setPreferredSize(new Dimension(375, 125));
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        getContentPane().add(controlPanel, BorderLayout.PAGE_END);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        javax.swing.SwingUtilities.invokeLater(() -> createAndShowGUI());
    }
}