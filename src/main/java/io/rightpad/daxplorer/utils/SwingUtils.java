package io.rightpad.daxplorer.utils;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.BiConsumer;

public class SwingUtils
{
    public static final Border INVALID_INPUT_BORDER = BorderFactory.createLineBorder(Color.RED, 2);

    public static ActionListener validatedListener(JTextField textField, BiConsumer<JTextField, ActionEvent> eventHandler)
    {
        return validatedListener(textField, e -> eventHandler.accept(textField, e));
    }

    public static ActionListener validatedListener(JTextField textField, ActionListener delegateListener)
    {
        final Border defaultBorder = textField.getBorder();
        return e -> {
            try {
                delegateListener.actionPerformed(e);
                textField.setBorder(defaultBorder);
            } catch(Exception ex) {
                System.err.println(ex);
                textField.setBorder(INVALID_INPUT_BORDER);
            }
        };
    }
}
