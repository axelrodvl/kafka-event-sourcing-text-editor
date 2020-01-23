package co.axelrod.kafka.editor.model;

import lombok.Data;

import java.awt.event.KeyEvent;
import java.io.Serializable;

@Data
public class Key implements Serializable {
    private int modifiersEx;
    private char keyChar;
    private int extendedKeyCode;
    private int keyCode;
    private int keyLocation;
    private long timestamp;

    public Key(KeyEvent keyEvent) {
        this.modifiersEx = keyEvent.getModifiersEx();
        this.keyChar = keyEvent.getKeyChar();
        this.extendedKeyCode = keyEvent.getExtendedKeyCode();
        this.keyCode = keyEvent.getKeyCode();
        this.keyLocation = keyEvent.getKeyLocation();
        this.timestamp = System.currentTimeMillis();
    }
}