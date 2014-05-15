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

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackgroundTaskScheduler {

    private static final Logger log = LoggerFactory.getLogger(BackgroundTaskScheduler.class);
    
    /** initial time to wait (seconds) before launching tasks */
    protected double initialDelay = 5;
    /** time (seconds) to wait between tasks */
    protected double taskDelay = 3;
    /** the ordered list of tasks to execute */
    protected List<BackgroundTask> tasks;
    
    
    public BackgroundTaskScheduler(double initialDelay, double taskDelay,
            List<BackgroundTask> tasks) {
        this.initialDelay = initialDelay;
        this.taskDelay = taskDelay;
        this.tasks = tasks;
    }
    
    public BackgroundTaskScheduler(double initialDelay, double taskDelay,
            BackgroundTask... tasks) {
        this(initialDelay, taskDelay, Arrays.asList(tasks));
    }
    
    public BackgroundTaskScheduler(BackgroundTask... tasks) {
        this.tasks = Arrays.asList(tasks);
    }
    
    /**
     * Launches the scheduler in a new Thread. Fire and forget!
     */
    public void launch() {
        Thread t = new Thread(scheduler, "BgSched");
        t.start();
    }
    
    /**
     * Executes all tasks in their respective order, only one Thread is used.
     */
    protected void executeSingleThreaded() {
        for (BackgroundTask task : tasks) {
            if(task.isEnabled() && task.isPending()) {
                // executes the task on this thread
                task.run();
                // sleep after the task is finished
                if(taskDelay > 0) {
                    try {
                        Thread.sleep((long) (taskDelay * 1000));
                    } catch (InterruptedException e) {
                        log.warn("Could not sleep", e);
                    }
                }
            }
        }
    }
    
    /**
     * The scheduler requires it's own thread
     */
    protected Runnable scheduler = new Runnable() {
        @Override
        public void run() {
            // sleep initially
            if(initialDelay > 0) {
                try {
                    Thread.sleep((long) (initialDelay * 1000));
                } catch (InterruptedException e) {
                    log.warn("Could not sleep", e);
                }
            }
            // run each task
            executeSingleThreaded();
        }
    };
    
}
