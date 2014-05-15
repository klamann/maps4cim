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
package de.nx42.maps4cim.gui.window;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import de.nx42.maps4cim.gui.MainWindow;
import de.nx42.maps4cim.gui.util.Components;
import de.nx42.maps4cim.gui.util.Fonts;
import de.nx42.maps4cim.update.Update;
import de.nx42.maps4cim.update.Update.Release;

public class UpdateWindow extends JDialog {

    private static final long serialVersionUID = 7546326133172584845L;
    
    protected static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private Update update;
    private Release release;

    private final JPanel contentPanel = new JPanel();


	public UpdateWindow(Window owner, Update update) {
	    super(owner);

	    this.update = update;
	    this.release = update.getBranch(SettingsWindow.getSelectedBranch());

	    setTitle(String.format("Update %s available", release.version));
		setBounds(250, 200, 550, 480);
		setMinimumSize(new Dimension(250, 150));
		setLocationByPlatform(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		Components.setIconImages(this);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        JEditorPane editorPaneAboutText = new JEditorPane();
        editorPaneAboutText.setContentType("text/html");
        editorPaneAboutText.setEditable(false);
        editorPaneAboutText.setFont(Fonts.select(editorPaneAboutText.getFont(), "Tahoma", "Geneva", "Arial"));
        editorPaneAboutText.setText(String.format("%s<html><br><a href=\"%s\">More Information</a><br><a href=\"%s\">Download</a></html>", release.description, release.infoUrl, release.downloadUrl));
        editorPaneAboutText.addHyperlinkListener(hyperLinkListener);

        JScrollPane scrollPane1 = new JScrollPane(editorPaneAboutText);
        scrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane1.setBorder(null);
        final JScrollBar vScroll = scrollPane1.getVerticalScrollBar();
        vScroll.setValue(vScroll.getMinimum());

        JLabel lblAnUpdateIs = new JLabel(String.format("<html>An Update is available for maps4cim!\r\n<ul>\r\n<li>You are currently running <b>%s</b> (%s)</li>\r\n<li>The latest version of the <b>%s</b> branch is <b>%s</b> (released %s)</li>\r\n</ul>\r\nAbout this update:</html>",
                MainWindow.version, MainWindow.branch, release.branch, release.version, sdf.format(release.releaseDate)));

        JLabel lblWouldYouLike = new JLabel("Would you like to download the latest version now?");
        GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
        gl_contentPanel.setHorizontalGroup(
            gl_contentPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_contentPanel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
                        .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                        .addComponent(lblAnUpdateIs)
                        .addComponent(lblWouldYouLike))
                    .addContainerGap())
        );
        gl_contentPanel.setVerticalGroup(
            gl_contentPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_contentPanel.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lblAnUpdateIs)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addComponent(lblWouldYouLike)
                    .addGap(6))
        );
        contentPanel.setLayout(gl_contentPanel);

        JPanel buttonPane = new JPanel();
        FlowLayout fl_buttonPane = new FlowLayout(FlowLayout.TRAILING);
        buttonPane.setLayout(fl_buttonPane);
        getContentPane().add(buttonPane, BorderLayout.SOUTH);

        JButton btnOk = new JButton("OK");
        Components.setPreferredWidth(btnOk, 70);
        btnOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainWindow.openWeb(release.downloadUrl);
                dispose();
            }
        });
        buttonPane.add(btnOk);
        getRootPane().setDefaultButton(btnOk);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        Components.setPreferredWidth(btnCancel, 70);
        buttonPane.add(btnCancel);


        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                vScroll.setValue(vScroll.getMinimum());
            }
        });
	}

	protected HyperlinkListener hyperLinkListener = new HyperlinkListener() {
        @Override
        public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent evt) {
            if (HyperlinkEvent.EventType.ACTIVATED.equals(evt.getEventType())) {
                MainWindow.openWeb(evt.getURL());
            }
        }
    };
}
