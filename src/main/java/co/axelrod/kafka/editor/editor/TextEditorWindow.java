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
public class TextEditorWindow extends JFrame implements InitializingBean {
    @Autowired
    public JTextArea displayArea;

    @Autowired
    public JTextField typingArea;

    @Autowired
    @Qualifier("replay")
    public JButton replayButton;

    @Autowired
    @Qualifier("changeFile")
    public JButton changeFileButton;

    public JLabel symbols;

    public JTextField fileNameField;

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private void createAndShowGUI() {
        //Create and set up the window.
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Set up the content pane.
        this.addComponentsToPane();

        //Display the window.
        this.pack();
        this.setVisible(true);
    }

    private void addComponentsToPane() {
        JTextField fileNameField = new JTextField(20);

        FlowLayout topLayout = new FlowLayout();
        topLayout.setAlignment(FlowLayout.TRAILING);
        final JPanel topPanel = new JPanel();
        topPanel.setLayout(topLayout);
        topPanel.add(fileNameField);
        topPanel.add(changeFileButton);
        topPanel.add(typingArea);
        topPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        getContentPane().add(topPanel, BorderLayout.PAGE_START);

        //Uncomment this if you wish to turn off focus
        //traversal.  The focus subsystem consumes
        //focus traversal keys, such as Tab and Shift Tab.
        //If you uncomment the following line of code, this
        //disables focus traversal and the Tab events will
        //become available to the key event listener.
        //typingArea.setFocusTraversalKeysEnabled(false);

        symbols = new JLabel();
        symbols.setText("Wow!");


        JScrollPane scrollPane = new JScrollPane(displayArea);
        scrollPane.setPreferredSize(new Dimension(375, 125));
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        FlowLayout borderLayout = new FlowLayout();
        borderLayout.setAlignment(FlowLayout.TRAILING);
        final JPanel borderPanel = new JPanel();
        borderPanel.setLayout(borderLayout);
        borderPanel.add(symbols);
        borderPanel.add(undoButton());
        borderPanel.add(redoButton());
        borderPanel.add(replayButton);
        borderPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        getContentPane().add(borderPanel, BorderLayout.PAGE_END);
    }

    public JButton undoButton() {
        JButton undo = new JButton("Undo");
        undo.addActionListener(e -> {

        });
        return undo;
    }

    public JButton redoButton() {
        JButton redo = new JButton("Redo");
        redo.addActionListener(e -> {

        });
        return redo;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        /* Use an appropriate Look and Feel */
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        /* Turn off metal's use of bold fonts */
        UIManager.put("swing.boldMetal", Boolean.FALSE);

        //Schedule a job for event dispatch thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(() -> createAndShowGUI());
    }
}