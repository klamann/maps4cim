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

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "maps4cim-update")
public class Update {

    // root elements

    @XmlElementWrapper(name = "releases")
    @XmlElement(name = "release")
    public List<Release> releases;

    @XmlElementWrapper(name = "notifications")
    @XmlElement(name = "notification")
    public List<Notification> notifications;

    @XmlElement(name = "lastUpdate")
    public Date lastUpdate;

    // tree contents

    @XmlRootElement(name = "release")
    public static class Release {

        @XmlAttribute(name = "branch")
        public String branch;
        @XmlAttribute(name = "version")
        public String version;
        @XmlAttribute(name = "release")
        public Date releaseDate;

        @XmlElement(name = "description")
        public String description;
        @XmlElement(name = "info-url")
        public String infoUrl;
        @XmlElement(name = "download-url")
        public String downloadUrl;

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "Release [branch=" + branch + ", version=" + version
                    + ", releaseDate=" + releaseDate + ", description="
                    + description + ", infoUrl=" + infoUrl + ", downloadUrl="
                    + downloadUrl + "]";
        }

    }

    @XmlRootElement(name = "notification")
    public static class Notification {

        @XmlAttribute(name = "id")
        public Integer id;
        @XmlAttribute(name = "date")
        public Date date;
        @XmlAttribute(name = "min-version")
        public String minVersion;
        @XmlAttribute(name = "max-version")
        public String maxVersion;

        @XmlElement(name = "heading")
        public String heading;
        @XmlElement(name = "content")
        public String content;

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "Notification [id=" + id + ", date=" + date
                    + ", minVersion=" + minVersion + ", maxVersion="
                    + maxVersion + ", heading=" + heading + ", content="
                    + content + "]";
        }

    }

    // helper functions

    public enum Branch {
        stable,
        testing;
    }

    public Release getStable() {
        return getBranch(Branch.stable);
    }

    public Release getTesting() {
        return getBranch(Branch.testing);
    }

    public Release getBranch(Branch branch) {
        return getBranch(branch.name());
    }

    public Release getBranch(String branch) {
        for (Release r : releases) {
            if(r.branch.equals(branch)) {
                return r;
            }
        }
        return null;
    }

    // object overrides

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Update [releases=" + releases + ", notifications="
                + notifications + ", lastUpdate=" + lastUpdate + "]";
    }

}