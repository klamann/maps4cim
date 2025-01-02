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
package de.nx42.maps4cim.update;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nx42.maps4cim.MapGenerator;
import de.nx42.maps4cim.map.Cache;
import de.nx42.maps4cim.update.Update.Branch;
import de.nx42.maps4cim.util.Network;
import de.nx42.maps4cim.util.Serializer;

public class UpdateChecker {

    private static final Logger log = LoggerFactory.getLogger(UpdateChecker.class);

    protected static final String[] updateURLs = new String[] {
        "https://raw.githubusercontent.com/klamann/maps4cim/master/update.xml",
    };
    protected static final String xmlFileName = "update.xml";

    protected Update update;
    protected ProgramVersion version;
    protected Branch branch;

    public UpdateChecker() {
        this(MapGenerator.version, MapGenerator.branch);
    }

    public UpdateChecker(ProgramVersion version, Branch branch) {
        this.version = version;
        this.branch = branch;
        this.update = getUpdateXml();
    }

    protected static Update getUpdateXml() {
        Exception lastException = null;

        for (String s : updateURLs) {
            try {
                URL url = new URL(s);
                File xml = Cache.temporaray(xmlFileName);
                Network.downloadToFile(url, xml);

                try {
                    return Serializer.deserialize(Update.class, xml);
                } catch (Exception e) {
                    lastException = e;
                    log.warn("Error while parsing update.xml", e);
                }
            } catch (IOException e) {
                lastException = e;
                log.warn(String.format("Could not retrieve file %s from update server.", s), e);
            }
        }

        throw new RuntimeException("Update check failed", lastException);
    }

    /**
     * Checks if an update is available
     * @return true, if an update is available
     * @throws NullPointerException if the specified branch does not exist
     */
    public boolean isUpdateAvailable() throws NullPointerException {
        return version.compareTo(new ProgramVersion(update.getBranch(branch).version)) < 0;
    }

    /**
     * Checks if an update is available
     * @param currentVersion he current version to compare the remote one to
     * @param branch the branch to use (for options, see {@link Branch}
     * @return true, if an update is available
     * @throws NullPointerException if the specified branch does not exist
     */
    public boolean isUpdateAvailable(String currentVersion, String branch) throws NullPointerException {
        return ProgramVersion.compareVersions(currentVersion, update.getBranch(branch).version) < 0;
    }

    /**
     * Checks if an update is available
     * @param currentVersion he current version to compare the remote one to
     * @param branch the branch to use (for options, see {@link Branch}
     * @return true, if an update is available
     * @throws NullPointerException if the specified branch does not exist
     */
    public boolean isUpdateAvailable(ProgramVersion currentVersion, Branch branch) throws NullPointerException {
        return currentVersion.compareTo(new ProgramVersion(update.getBranch(branch).version)) < 0;
    }

    /**
     * @return the update
     */
    public Update getUpdate() {
        return update;
    }

}
