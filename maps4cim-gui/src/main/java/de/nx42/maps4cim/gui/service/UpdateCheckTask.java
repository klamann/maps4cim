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
package de.nx42.maps4cim.gui.service;

import java.awt.Window;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nx42.maps4cim.gui.MainWindow;
import de.nx42.maps4cim.gui.util.event.Event;
import de.nx42.maps4cim.gui.util.event.Observer;
import de.nx42.maps4cim.gui.window.SettingsWindow;
import de.nx42.maps4cim.gui.window.UpdateLoadingWindow;
import de.nx42.maps4cim.gui.window.UpdateWindow;
import de.nx42.maps4cim.update.Update;
import de.nx42.maps4cim.update.UpdateChecker;


public class UpdateCheckTask extends BackgroundTask {

    private static final Logger log = LoggerFactory.getLogger(UpdateCheckTask.class);
    
    protected static final String regKeyUpdate = "update";
    protected static final String regKeyUpdateBranch = regKeyUpdate + ".branch";
    
    // logic
    protected Update update;
    protected UpdateChecker updateChecker;
    
    // gui
    protected UpdateWindow updateWindow;
    protected UpdateLoadingWindow updateLoadingWindow;
    protected Event cancelEvent;
    
    // settings
    protected boolean displayProgress = false;
    protected Window owner;
    
    // update worker thread
    protected FutureTask<UpdateChecker> worker;
    
    
    public UpdateCheckTask() {
        this(false, null);
    }
    
    public UpdateCheckTask(boolean displayProgress, Window owner) {
        // run every 7 days and use reg key prefix "update" by default
        super("maps4cim update check", regKeyUpdate, true, 7);
        this.displayProgress = displayProgress;
        this.owner = owner;
    }


    @Override
    public void execute() {
        
        // launch update check
        this.worker = new FutureTask<UpdateChecker>(callableUpdate);
        Thread t = new Thread(worker, "HTTPUpdate");
        t.start();
        
        // do other stuff
        if(displayProgress) {
            cancelEvent = new Event();
            cancelEvent.addObserver(cancelEventObserver);
            updateLoadingWindow = new UpdateLoadingWindow(owner);
            updateLoadingWindow.registerCancelEvent(cancelEvent);
            updateLoadingWindow.setVisible(true);
            updateLoadingWindow.setWorking(true);
        }
        
        // get update
        try {
            
            this.updateChecker = worker.get();
            this.update = updateChecker.getUpdate();
            
            // close progress window
            if(displayProgress) {
                updateLoadingWindow.setWorking(false);
                updateLoadingWindow.dispose();
            }
            
            // display result
            if(updateChecker.isUpdateAvailable()) {
                // always display available updates
                UpdateWindow uw = new UpdateWindow(owner, update);
                uw.setVisible(true);
            } else if(displayProgress) {
                // display failure, if visibility requested
                JOptionPane.showMessageDialog(owner,
                        String.format("Great, you are running %s, which is the latest %s version of maps4cim.", MainWindow.version, MainWindow.branch),
                        "Everything up to date", JOptionPane.INFORMATION_MESSAGE);
            }
            // do nothing, if no update is available, or the update failed and
            // interaction was not explicitly requested
            
        } catch(ExecutionException e) {
            log.error("Could not check for updates", e);
            JOptionPane.showMessageDialog(owner, "<html><center>Sorry, could not check for updates.<br>Check your firewall/network connection or review the log for details (accessible through settings)</center></html>",
                    "Update check failed", JOptionPane.ERROR_MESSAGE);
        } catch (InterruptedException e) {
            log.warn("Update was cancelled by external request", e);
        } finally {
            if(updateLoadingWindow != null) {
                updateLoadingWindow.dispose();
            }
        }
        
    }
    
    
    /**
     * the actual update check has to run in a separate thread, of course...
     */
    protected Callable<UpdateChecker> callableUpdate = new Callable<UpdateChecker>() {
        @Override
        public UpdateChecker call() {
            return new UpdateChecker(MainWindow.version, SettingsWindow.getSelectedBranch());
        }
    };
    
    protected Observer cancelEventObserver = new Observer() {
        @Override
        public void update() {
            if(!(worker.isDone() || worker.isCancelled())) {
                worker.cancel(true);
            }
        }
    };

}