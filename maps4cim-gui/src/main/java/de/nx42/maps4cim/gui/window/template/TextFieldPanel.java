/**
 * maps4cim - a real world map generator for CiM 2
 * Copyright 2013 Sebastian Straub
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
package de.nx42.maps4cim.gui.window.template;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;

import de.nx42.maps4cim.ResourceLoader;
import de.nx42.maps4cim.gui.MainWindow;
import de.nx42.maps4cim.gui.util.Components;
import de.nx42.maps4cim.gui.util.Fonts;

public class TextFieldPanel extends JDialog {

    private static final long serialVersionUID = 5556229786055202733L;
    protected static final ResourceBundle MESSAGES = ResourceLoader.getMessages();

    protected JEditorPane editorPane;
    protected JPanel buttonPane;

    /**
	 * Create the dialog.
	 */
	public TextFieldPanel(Window owner, String title, String text) {
	    // basic window config
        super(owner, title);
		setBounds(200, 150, 350, 400);
		setMinimumSize(new Dimension(250, 150));
		setLocationByPlatform(true);
		Components.setIconImages(this);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		// layout
		getContentPane().setLayout(new BorderLayout());
		JPanel contentPanel = new JPanel();
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		// text field
		editorPane = new JEditorPane();
		editorPane.setContentType("text/html");
		editorPane.setEditable(false);
		editorPane.setFont(Fonts.select(editorPane.getFont(), "Tahoma", "Geneva", "Arial"));
		editorPane.setText(text);
		editorPane.setCaretPosition(0);
		editorPane.addHyperlinkListener(new javax.swing.event.HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent evt) {
                if (HyperlinkEvent.EventType.ACTIVATED.equals(evt.getEventType())) {
                    MainWindow.openWeb(evt.getURL());
                }
            }
        });

		// scrolling
		JScrollPane scrollPane1 = new JScrollPane(editorPane);
        scrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane1.setBorder(null);
        final JScrollBar vScroll = scrollPane1.getVerticalScrollBar();
        vScroll.setValue(vScroll.getMinimum());
		contentPanel.setLayout(new BorderLayout(0, 0));
		contentPanel.add(scrollPane1);

		// buttons (bottom)
        buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
        getContentPane().add(buttonPane, BorderLayout.SOUTH);

        // close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        buttonPane.add(closeButton);
        getRootPane().setDefaultButton(closeButton);
	}

    /**
     * @return the text field of this window
     */
    public JEditorPane getEditorPane() {
        return editorPane;
    }
    
    protected void setText(String text) {
        editorPane.setText(text);
        editorPane.setCaretPosition(0);
    }

}
