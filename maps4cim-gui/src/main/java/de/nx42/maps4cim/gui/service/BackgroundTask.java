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

import java.util.Date;
import java.util.prefs.Preferences;

import com.google.common.base.Strings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nx42.maps4cim.gui.util.PrefsHelper;
import de.nx42.maps4cim.util.DateUtils;

public abstract class BackgroundTask implements Runnable {
    
    private static final Logger log = LoggerFactory.getLogger(BackgroundTask.class);
    
    /** Preferences-Object to be used by all tasks */
    protected static final Preferences prefs = PrefsHelper.getPrefs();
    /** some date in the past, should cause most tasks to execute initially */
    protected static final long defaultDate = 1388583420000L;
    
    public static final String regKeyPostfixEnabled = "enabled";
    public static final String regKeyPostfixLastRun = "lastrun";
    public static final String regKeyPostfixInterval = "interval";
    
    /** the name of this task */
    protected String name;
    
    protected String regKeyPrefix;
    
    /** Preferences-Key that decides if this task is enabled (boolean) */
    protected String regKeyEnabled;
    /** Preferences-Key that stores date/time of the last run */
    protected String regKeyLastRun;
    /** Preferences-Key that stores the execution interval (in days) */
    protected String regKeyInterval;
    
    protected boolean defaultEnabled;
    protected double defaultInterval;
    
    
    /**
     * Creates a new background task which is supposed to run on a regular
     * basis. Stores information about the last run and the interval in
     * which to invoke the task
     * @param name the name of this task
     * @param regKeyPrefix Preferences-Key that will be the root for subsequent
     * preferences like lastrun, interval, etc.
     * @param defaultEnabled this setting is used, if no preference was found
     * @param defaultInterval this interval is used, if no preference was found
     */
    public BackgroundTask(String name, String regKeyPrefix, boolean defaultEnabled, double defaultInterval) {
        this.name = Strings.isNullOrEmpty(name) ? this.getClass().getCanonicalName() : name;
        this.regKeyPrefix = regKeyPrefix;
        this.defaultEnabled = defaultEnabled;
        this.defaultInterval = defaultInterval;
        expandRegKeys();
    }
    
    /**
     * Creates a new background task which is supposed to run on a regular
     * basis. Stores information about the last run and the interval in
     * which to invoke the task
     * @param name the name of this task
     * @param regKeyPrefix Preferences-Key that will be the root for subsequent
     * preferences like lastrun, interval, etc.
     */
    public BackgroundTask(String name, String regKeyPrefix) {
        this(name, regKeyPrefix, true, 7);
    }
    
    /**
     * Creates a new background task which is supposed to run on a regular
     * basis. Stores information about the last run and the interval in
     * which to invoke the task
     * @param regKeyPrefix Preferences-Key that will be the root for subsequent
     * preferences like lastrun, interval, etc.
     */
    public BackgroundTask(String regKeyPrefix) {
        this(null, regKeyPrefix);
    }

    /**
     * Expands all Preferences-Keys to the final path (combines prefix
     * and postfix)
     */
    private void expandRegKeys() {
        regKeyEnabled = regKeyPrefix + "." + regKeyPostfixEnabled;
        regKeyLastRun = regKeyPrefix + "." + regKeyPostfixLastRun;
        regKeyInterval = regKeyPrefix + "." + regKeyPostfixInterval;
    }
    
    // Getters

    /**
     * @return the name of this task
     */
    public String getName() {
        return name;
    }
    
    /**
     * @return true, if this task is enabled / scheduled for execution
     */
    public boolean isEnabled() {
        return prefs.getBoolean(regKeyEnabled, defaultEnabled);
    }

    /**
     * @return exact date/time when the task has finished the last time
     */
    public Date getLastRun() {
        return new Date(getLastRunMillis());
    }

    /**
     * @return the execution interval (in days)
     */
    public double getInterval() {
        return prefs.getDouble(regKeyInterval, defaultInterval);
    }
    
    /**
     * @return true, iff the task should be executed, i.e. enough time has
     * passed since the last execution
     */
    public boolean isPending() {
        return getLastRun().before(DateUtils.getDaysBeforeToday(getInterval()));
    }

    protected long getLastRunMillis() {
        return prefs.getLong(regKeyLastRun, defaultDate);
    }
    
    // actual operations
    
    /**
     * sets the date/time of the last run to now
     */
    protected void updateLastRun() {
        prefs.putLong(regKeyLastRun, new Date().getTime());
    }
    
    @Override
    public void run() {
        log.debug("Launching {}, last run finished on {}", name, getLastRun());
        try {
            execute();
            updateLastRun();
        } catch (Exception e) {
            log.warn(String.format("Error while executing background task {}", name), e);
        }
        log.debug("{} finished, next run is scheduled in {} days", name, getInterval());
    }
    
    /**
     * This method is called when the BackgroundTask is to be executed.
     * Usually, this method should NOT be called directly! Use the
     * {@link BackgroundTaskScheduler} instead or wrap this class in a new
     * {@link Thread}.
     * 
     * @throws Exception if anything goes wrong
     */
    public abstract void execute() throws Exception;

}
