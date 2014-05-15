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

import de.nx42.maps4cim.map.Cache.Janitor;

public class CacheJanitorTask extends BackgroundTask {
    
    protected static final String regKeyJanitor = "janitor";
    
    public CacheJanitorTask() {
        // default setup (run once a week, use specified preference keys)
        super("cache janitor", regKeyJanitor, true, 7);
    }
    
    public CacheJanitorTask(String name, String regKeyPrefix,
            boolean defaultEnabled, double defaultInterval) {
        super(name, regKeyPrefix, defaultEnabled, defaultInterval);
    }

    @Override
    public void execute() throws Exception {
        Janitor janitor = new Janitor();
        janitor.wipeCache();
    }

}