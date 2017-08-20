/**
 * maps4cim - a real world map generator for CiM 2
 * Copyright 2013 - 2014 Sebastian Straub
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.nx42.maps4cim.gui.util;

import java.awt.Color;

import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.Context;

import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import org.slf4j.LoggerFactory;


public class TextAreaLogAppender extends AppenderBase<ILoggingEvent> {

    private static final String pattern = "%d{HH:mm:ss.SSS} [%level]: %msg%nopex%n";
    private final TextAreaEncoder encoder = new TextAreaEncoder(pattern);
    private final StyledDocument doc;

    public TextAreaLogAppender(JTextPane tp) {
        this.doc = tp.getStyledDocument();
        this.started = true;
    }

    @Override
    protected void append(final ILoggingEvent eventObject) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                encoder.doEncode(eventObject);
            }
        });
    }

    protected void append(String msg, Level lvl) {
        Color color = Color.LIGHT_GRAY;
        int level = lvl.toInt();

        if (level <= Level.DEBUG_INT) {
            color = Color.GRAY;
        } else if (level == Level.INFO_INT) {
            color = Color.BLACK;
        } else if (level == Level.WARN_INT) {
            color = new Color(255, 102, 0);
        } else if (level >= Level.ERROR_INT) {
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
            // no logging here ON PURPOSE to avoid log exception loops!
            e.printStackTrace();
        }
    }

    class TextAreaEncoder extends PatternLayoutEncoder {

        public TextAreaEncoder(String pattern) {
            super();
            setContext((Context) LoggerFactory.getILoggerFactory());
            setPattern(pattern);
            start();
        }

        /* (non-Javadoc)
         * @see ch.qos.logback.core.encoder.LayoutWrappingEncoder#doEncode(java.lang.Object)
         */
        public void doEncode(ILoggingEvent event) {
            String txt = layout.doLayout(event);
            append(txt, event.getLevel());
        }
    }

}