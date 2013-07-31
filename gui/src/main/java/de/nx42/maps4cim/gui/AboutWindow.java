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
package de.nx42.maps4cim.gui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AboutWindow extends JDialog {

    private static final Logger log = LoggerFactory.getLogger(AboutWindow.class);
	private static final long serialVersionUID = 5583448871617188269L;

	private final JPanel contentPanel = new JPanel();

	/**
	 * Create the dialog.
	 */
	public AboutWindow() {
		setTitle("About maps4cim");
		setBounds(200, 200, 540, 340);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		JLabel lblTitle = new JLabel("maps4cim");
		lblTitle.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);

		JEditorPane editorPaneAboutText = new JEditorPane();
		editorPaneAboutText.setContentType("text/html");
		editorPaneAboutText.setEditable(false);
		editorPaneAboutText.setText("<html>\r\n<p>maps4cim, version 0.9 (2013-06-24)<br />\r\ndeveloped by Sebastian Straub &lt;<a href=\"mailto:sebastian-straub@gmx.net\">sebastian-straub@gmx.net</a>&gt;</p>\r\n<p>maps4cim is a real-world map generator for the traffic simulation game <a href=\"http://www.citiesinmotion2.com/\" title=\"Cities in Motion 2\">Cities in Motion 2</a>, which relies on free geospatial data from the <a href=\"http://www2.jpl.nasa.gov/srtm/\">SRTM</a> and <a href=\"http://www.openstreetmap.org/\">OpenStreetMap</a>.</p>\r\n<p>For updates, visit the <a href=\"http://nx42.de/projects/maps4cim/\">project homepage</a>.\r\n<p>Please be aware that this is a beta release. It may contain serious bugs, so use it at your own risk. Watch for updates on the project homepage or in the forums.</p>\r\n</html>");
		editorPaneAboutText.addHyperlinkListener(new javax.swing.event.HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent evt) {
                jEditorPaneAboutTextHyperlinkUpdate(evt);
            }
        });

		JScrollPane scrollPane1 = new JScrollPane(editorPaneAboutText);
        scrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane1.setBorder(null);
        final JScrollBar vScroll = scrollPane1.getVerticalScrollBar();
        vScroll.setValue(vScroll.getMinimum());

		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
		    gl_contentPanel.createParallelGroup(Alignment.LEADING)
		        .addGroup(gl_contentPanel.createSequentialGroup()
		            .addGap(10)
		            .addComponent(lblTitle, GroupLayout.DEFAULT_SIZE, 304, Short.MAX_VALUE)
		            .addContainerGap())
		        .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
		);
		gl_contentPanel.setVerticalGroup(
		    gl_contentPanel.createParallelGroup(Alignment.LEADING)
		        .addGroup(gl_contentPanel.createSequentialGroup()
		            .addContainerGap()
		            .addComponent(lblTitle)
		            .addPreferredGap(ComponentPlacement.UNRELATED)
		            .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE))
		);
		contentPanel.setLayout(gl_contentPanel);

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPane, BorderLayout.SOUTH);

        JButton okButton = new JButton("Close");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { dispose(); }
        });
        okButton.setActionCommand("OK");
        buttonPane.add(okButton);
        getRootPane().setDefaultButton(okButton);


		EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                vScroll.setValue(vScroll.getMinimum());
            }
        });
	}

	private void jEditorPaneAboutTextHyperlinkUpdate(HyperlinkEvent evt) {
        if (HyperlinkEvent.EventType.ACTIVATED.equals(evt.getEventType())) {
            try {
                openWeb(evt.getURL().toURI());
            } catch (URISyntaxException ex) {
                log.error("Could not open URL", ex);
            }
        }
    }

	private static void openWeb(URI uri) {
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(uri);
            } catch (IOException ex) {
                log.error("", ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, String.format("Your java runtime does not seem to support the opening of weblinks.\n"
                    + "You can open the link manually though:\n%s", uri.toString()), "Unable to open weblink", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void openWeb(URL url) {
        try {
            openWeb(url.toURI());
        } catch (URISyntaxException ex) {
            log.error("", ex);
        }
    }

    private static void openWeb(String uri) {
        try {
            openWeb(new URI(uri));
        } catch (URISyntaxException ex) {
            log.error("", ex);
        }
    }

}
