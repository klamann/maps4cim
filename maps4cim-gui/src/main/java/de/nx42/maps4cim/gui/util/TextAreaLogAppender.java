package de.nx42.maps4cim.gui.util;

import java.awt.Color;

import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Priority;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;

public class TextAreaLogAppender extends WriterAppender {

    private final StyledDocument doc;

    public TextAreaLogAppender(JTextPane tp) {
        this.doc = tp.getStyledDocument();
        this.layout = new PatternLayout("%d{HH:mm:ss,SS} - %p: %m%n");
    }

    @Override
    public void append(LoggingEvent loggingEvent) {

        final String message = this.layout.format(loggingEvent);
        final Level level = loggingEvent.getLevel();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                append(message, level);
            }
        });
    }

    protected void append(String msg, Level lvl) {
        Color color = Color.LIGHT_GRAY;
        int level = lvl.toInt();
        if (level <= Priority.DEBUG_INT) {
            color = Color.GRAY;
        } else if (level == Priority.INFO_INT) {
            color = Color.BLACK;
        } else if (level == Priority.WARN_INT) {
            color = new Color(255, 102, 0);
        } else if (level >= Priority.ERROR_INT) {
            color = Color.RED;
        }
        append(msg, color);
    }

    protected void append(String msg, Color c) {
        // Define a keyword attribute
        SimpleAttributeSet keyWord = new SimpleAttributeSet();
        StyleConstants.setForeground(keyWord, c);

        // Add some text
        try {
            doc.insertString(doc.getLength(), msg, keyWord);
        } catch (BadLocationException e) {
            // do not log here, this would probably cause a loop...
            e.printStackTrace();
        }
    }

}