package co.axelrod.kafka.editor.swing;

import co.axelrod.kafka.editor.kafka.MyConsumer;
import co.axelrod.kafka.editor.kafka.Sender;
import co.axelrod.kafka.editor.model.Symbol;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

@Component
@Slf4j
public class TextEditor extends JFrame implements KeyListener {
    Long timestamp = null;

    @Autowired
    private Sender sender;

    @Autowired
    private MyConsumer consumer;

    @Autowired
    private TaskExecutor taskExecutor;

    JTextArea displayArea;
    JTextField typingArea;
    static final String newline = System.getProperty("line.separator");

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

        JButton button = new JButton("Replay");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Clear the text components.
                displayArea.setText("");
                typingArea.setText("");

                //Return the focus to the typing area.
                typingArea.requestFocusInWindow();

//                for (ConsumerRecord<Long, String> record : ImmediateConsumer.getAllRecordsFromTopic()) {
//                    displayInfo(record.value());
////                    System.out.printf("topic = %s, partition = %s, offset = %d, customer = %s, country = %s\n",
////                    record.topic(), record.partition(), record.offset(),
////                    record.key(), record.value());
//                }
//                ImmediateConsumer.getAllRecordsFromTopic().forEach((record) -> displayInfo(record.value()));

                timestamp = null;
                consumer.getAllRecordsFromTopic();
            }
        });

        JButton undo = new JButton("Undo");
        undo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        JButton redo = new JButton("Redo");
        redo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        typingArea = new JTextField(20);
        typingArea.addKeyListener(this);

        //Uncomment this if you wish to turn off focus
        //traversal.  The focus subsystem consumes
        //focus traversal keys, such as Tab and Shift Tab.
        //If you uncomment the following line of code, this
        //disables focus traversal and the Tab events will
        //become available to the key event listener.
        //typingArea.setFocusTraversalKeysEnabled(false);

        displayArea = new JTextArea();
        displayArea.setEditable(true);
        JScrollPane scrollPane = new JScrollPane(displayArea);
        scrollPane.setPreferredSize(new Dimension(375, 125));

        getContentPane().add(typingArea, BorderLayout.PAGE_START);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(button, BorderLayout.PAGE_END);
        //getContentPane().add(undo, BorderLayout.AFTER_LAST_LINE);
        //getContentPane().add(redo, BorderLayout.PAGE_END);
    }

    public TextEditor() {
        /* Use an appropriate Look and Feel */
        try {
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
            //UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
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
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    @PostConstruct
    public void init() {
        taskExecutor.execute(() -> {
            while (true) {
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }

                if(!consumer.hasNextSymbol()) {
                    continue;
                }

                Symbol symbol = consumer.getNextSymbol();

                if(symbol == null) {
                    continue;
                }

                if(timestamp == null) {
                    timestamp = Math.abs(symbol.timestamp);
                    displayInfo(String.valueOf(symbol.symbol));
                    continue;
                }

                if(symbol.symbol != '\n' && Math.abs(symbol.timestamp - timestamp) < 3000) {
                    try {
                        log.info("Printing next symbol " + symbol.symbol + " after " + (symbol.timestamp - timestamp) + " ms");
                        Thread.sleep(Math.abs(symbol.timestamp - timestamp));
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }

                timestamp = Math.abs(symbol.timestamp);

                //displayInfo(e, "KEY TYPED: ");
                displayInfo(String.valueOf(symbol.symbol));
            }
        });
    }

    /**
     * Handle the key typed event from the text field.
     */
    public void keyTyped(KeyEvent e) {
        //byte[] arr = serializeKeyEvent(e);
        sender.send("", getTimestamp(), e.getKeyChar());
        Thread.yield();
    }

    private byte[] serializeKeyEvent(KeyEvent keyEvent) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(keyEvent);
            out.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                // Ignoring
            }
        }
        throw new RuntimeException("Unable to serialize key event");
    }

    /**
     * Handle the key pressed event from the text field.
     */
    public void keyPressed(KeyEvent e) {
        //displayInfo(e, "KEY PRESSED: ");
    }

    /**
     * Handle the key released event from the text field.
     */
    public void keyReleased(KeyEvent e) {
        //displayInfo(e, "KEY RELEASED: ");
    }

    /*
     * We have to jump through some hoops to avoid
     * trying to print non-printing characters
     * such as Shift.  (Not only do they not print,
     * but if you put them in a String, the characters
     * afterward won't show up in the text area.)
     */
    private void displayInfo(KeyEvent e, String keyStatus) {

        //You should only rely on the key char if the event
        //is a key typed event.
        int id = e.getID();
        String keyString;
        if (id == KeyEvent.KEY_TYPED) {
            char c = e.getKeyChar();
            keyString = "key character = '" + c + "'";
        } else {
            int keyCode = e.getKeyCode();
            keyString = "key code = " + keyCode
                    + " ("
                    + KeyEvent.getKeyText(keyCode)
                    + ")";
        }

        int modifiersEx = e.getModifiersEx();
        String modString = "extended modifiers = " + modifiersEx;
        String tmpString = KeyEvent.getModifiersExText(modifiersEx);
        if (tmpString.length() > 0) {
            modString += " (" + tmpString + ")";
        } else {
            modString += " (no extended modifiers)";
        }

        String actionString = "action key? ";
        if (e.isActionKey()) {
            actionString += "YES";
        } else {
            actionString += "NO";
        }

        String locationString = "key location: ";
        int location = e.getKeyLocation();
        if (location == KeyEvent.KEY_LOCATION_STANDARD) {
            locationString += "standard";
        } else if (location == KeyEvent.KEY_LOCATION_LEFT) {
            locationString += "left";
        } else if (location == KeyEvent.KEY_LOCATION_RIGHT) {
            locationString += "right";
        } else if (location == KeyEvent.KEY_LOCATION_NUMPAD) {
            locationString += "numpad";
        } else { // (location == KeyEvent.KEY_LOCATION_UNKNOWN)
            locationString += "unknown";
        }

//        displayArea.append(keyStatus + newline
//                + "    " + keyString + newline
//                + "    " + modString + newline
//                + "    " + actionString + newline
//                + "    " + locationString + newline);
//        displayArea.setCaretPosition(displayArea.getDocument().getLength());

        displayArea.append(String.valueOf(e.getKeyChar()));
        displayArea.setCaretPosition(displayArea.getDocument().getLength());
    }

    private void displayInfo(String key) {
        if("\b".equals(key)) {
            Document doc = displayArea.getDocument();
            try {
                if(doc.getLength() > 1) {
                    doc.remove(doc.getLength() - 1, 1);
                }
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        } else {
            displayArea.append(key);
            displayArea.setCaretPosition(displayArea.getDocument().getLength());
        }
    }

        private static long getTimestamp() {
            return System.currentTimeMillis();
}
}