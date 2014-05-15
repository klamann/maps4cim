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

import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import de.nx42.maps4cim.gui.util.Components;
import de.nx42.maps4cim.gui.util.event.Event;
import de.nx42.maps4cim.update.Update;

public class UpdateLoadingWindow extends JDialog {

    private static final long serialVersionUID = 7546326133172584845L;

    protected Event cancelEvent;
    protected JProgressBar progressBar;
    
    protected Update update;

	public UpdateLoadingWindow(Window owner) {
	    super(owner);

	    setTitle("Check for Updates");
		setBounds(250, 200, 350, 130);
		setMinimumSize(new Dimension(120, 70));
	    setResizable(false);
		setLocationByPlatform(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		Components.setIconImages(this);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelUpdate();
                setVisible(false);
                dispose();
            }
        });
        
        progressBar = new JProgressBar();
        
        JLabel lblSearchingForUpdates = new JLabel("Searching for updates...");
        
        JButton btnRunInBackground = new JButton("Run in Background");
        btnRunInBackground.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        });
        
        GroupLayout groupLayout = new GroupLayout(getContentPane());
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.TRAILING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addComponent(lblSearchingForUpdates)
                        .addComponent(progressBar, GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
                        .addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
                            .addComponent(btnRunInBackground)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(btnCancel)))
                    .addContainerGap())
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lblSearchingForUpdates)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(progressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                    .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(btnCancel)
                        .addComponent(btnRunInBackground))
                    .addContainerGap())
        );
        getContentPane().setLayout(groupLayout);
	}

	public Update getUpdate() {
	    return update;
	}
	
	public void registerCancelEvent(Event e) {
	    this.cancelEvent = e;
	}
	
	public void setWorking(final boolean working) {
	    SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                progressBar.setIndeterminate(working);
            }
        });
	}
	
	protected void cancelUpdate() {
	    if(cancelEvent != null) {
	        cancelEvent.fire();
	    }
	}
}
