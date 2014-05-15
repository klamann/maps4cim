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
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ResourceBundle;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.xml.bind.JAXBException;

import ch.qos.logback.classic.LoggerContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nx42.maps4cim.MapGenerator;
import de.nx42.maps4cim.ResourceLoader;
import de.nx42.maps4cim.config.Config;
import de.nx42.maps4cim.gui.util.Components;
import de.nx42.maps4cim.gui.util.Fonts;
import de.nx42.maps4cim.gui.util.TextAreaLogAppender;
import de.nx42.maps4cim.util.Serializer;

public class RenderWindow extends JFrame {
    private static final ResourceBundle MESSAGES = ResourceLoader.getMessages();

	private static final Logger log = LoggerFactory.getLogger(RenderWindow.class);
	private static final long serialVersionUID = -1913557466280155872L;

	private JPanel contentPane;

	private JButton btnDone;
	private JButton btnSaveLog;
	private JTextPane logView;
	private JProgressBar progressBar;

	private boolean working = false;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
            public void run() {
				try {
					RenderWindow frame = new RenderWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					log.error("Error while creating RenderWindow", e);
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public RenderWindow() {
		super();
		setTitle(MESSAGES.getString("RenderWindow.this.title")); //$NON-NLS-1$

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setBounds(115, 115, 500, 400);
		setMinimumSize(new Dimension(400, 180));
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		Components.setIconImages(this);

		progressBar = new JProgressBar(0, 100);

		btnDone = new JButton(MESSAGES.getString("RenderWindow.btnDone.text")); //$NON-NLS-1$
		btnDone.setToolTipText(MESSAGES.getString("RenderWindow.btnDone.toolTipText")); //$NON-NLS-1$
		btnDone.addActionListener(btnDoneAction);

		logView = new JTextPane();
		logView.setFont(Fonts.select(logView.getFont(), "Tahoma", "Geneva", "Arial"));
		logView.setEditable(false);

		JScrollPane scrollPane = new JScrollPane(logView);
		scrollPane.setBorder(null);

		btnSaveLog = new JButton(MESSAGES.getString("RenderWindow.btnSaveLog.text")); //$NON-NLS-1$
		btnSaveLog.setToolTipText(MESSAGES.getString("RenderWindow.btnSaveLog.toolTipText")); //$NON-NLS-1$
		btnSaveLog.addActionListener(btnSaveLogAction);

		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addComponent(scrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE)
						.addComponent(progressBar, GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(btnSaveLog)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnDone, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(progressBar, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnDone)
						.addComponent(btnSaveLog))
					.addContainerGap())
		);
		contentPane.setLayout(gl_contentPane);

		addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cancelMapGenerator();
            }
        });
		addLogAppender();
	}

	// action listeners

	protected ActionListener btnDoneAction = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            cancelMapGenerator();
        	if(working) {
        		finishWork(false);
        	} else {
        		dispose();
        	}
        }
    };

    protected ActionListener btnSaveLogAction = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            Document doc = logView.getStyledDocument();
            try {
                String eventlog = doc.getText(0, doc.getLength());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                StringSelection strSel = new StringSelection(eventlog);
                clipboard.setContents(strSel, null);
                // log.debug("the log has been copied to clipboard");
            } catch (BadLocationException e1) {
                log.error("Could not copy log to clipboard", e);
            }

        }
    };

    // application logic

    Thread mapGenerator;
    private Config config = null;

    public void runMapGenerator(final Config conf, final File dest) {
    	config = conf;
    	if(mapGenerator != null) {
    		mapGenerator.interrupt();
    	}

    	mapGenerator = new Thread(new Runnable() {
			@Override
			public void run() {
				boolean success = MapGenerator.execute(conf, dest);
				mapGeneratorFinished(success);
			}
		});
    	mapGenerator.start();
    	startWork();
    }
    
    @SuppressWarnings("deprecation")
    protected void cancelMapGenerator() {
        if(mapGenerator != null && mapGenerator.isAlive()) {
            log.warn("Operation aborted by user request.");
            mapGenerator.interrupt();
            // Show no mercy
            if(mapGenerator.isAlive()) {
                mapGenerator.stop();
            }
            mapGenerator = null;
        }
    }

    protected void mapGeneratorFinished() {
        mapGeneratorFinished(true);
    }

    protected void mapGeneratorFinished(final boolean success) {
    	EventQueue.invokeLater(new Runnable() {
			@Override
            public void run() {
				finishWork(success);
			}
		});
    	autoSaveConfig();
    }

	protected void startWork() {
		working = true;
		progressBar.setValue(0);
		progressBar.setIndeterminate(true);
	}

	protected void finishWork(boolean success) {
		working = false;
		progressBar.setIndeterminate(false);
		progressBar.setValue(success ? 100 : 0);
		btnDone.setText("Done");
	}

	protected void autoSaveConfig() {
		File serialized = new File(ResourceLoader.getAppDir(), "config-last.xml");
		try {
			Serializer.serialize(Config.class, config, serialized);
		} catch (JAXBException e) {
			log.error("Could not auto-save config in appdata", e);
		}
	}

	protected void addLogAppender() {
		TextAreaLogAppender tap = new TextAreaLogAppender(logView);

		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		ch.qos.logback.classic.Logger logbackLogger = lc.getLogger(Logger.ROOT_LOGGER_NAME);
        logbackLogger.addAppender(tap);

//		Logger.getRootLogger().addAppender(tap);
	}

}
