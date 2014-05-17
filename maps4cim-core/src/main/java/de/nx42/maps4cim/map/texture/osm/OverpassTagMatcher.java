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
package de.nx42.maps4cim.map.texture.osm;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openstreetmap.osmosis.core.domain.v0_6.Entity;
import org.openstreetmap.osmosis.core.domain.v0_6.EntityType;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;

import de.nx42.maps4cim.config.texture.osm.EntityDef;
import de.nx42.maps4cim.config.texture.osm.NodeDef;
import de.nx42.maps4cim.config.texture.osm.PolygonDef;

public class OverpassTagMatcher {

    protected EntityType type;
    protected String key;
    protected Value value;


    public OverpassTagMatcher(EntityDef def) {
        this.type = inferType(def);
        this.key = def.key;
        this.value = Value.of(def);
    }

    public OverpassTagMatcher(EntityType type, String key, String value, boolean regexValue) {
    	this.type = type;
        this.key = key;
        this.value = Value.of(value, regexValue);
    }


    public String getKey() {
        return key;
    }

    public String getValue() {
    	return value.getValue();
    }

    public boolean matches(Entity entity) {
        // 1. match type
        if(entity.getType() != type) {
            return false;	// break immediately if type is wrong
        }

        // 2. match tags
        Collection<Tag> tags = entity.getTags();
        for (Tag tag : tags) {
        	// match key (exact)
            if(key.equals(tag.getKey())) {
            	// match value (any, exact or regex)
            	if(value.matches(tag.getValue())) {
            		// 3.1: if any key-value pair matches, return true
            		return true;
            	}
            }
        }

        // 3.2: if nothing matches, return false
        return false;
    }

    protected EntityType inferType(EntityDef def) {
        if(def instanceof NodeDef) {
            return EntityType.Node;
        } else if(def instanceof PolygonDef) {  // WayDef is subclass of PolygonDef
            return EntityType.Way;
        } else {
            throw new RuntimeException("Unexpected input type. Must be one of Node, Polygon or Way.");
        }
    }



    protected abstract static class Value {

        public abstract boolean matches(String s);
        public abstract String getValue();

        public static Value of(EntityDef def) {
            if(def.allowsAnyValue()) {
                return new Any();
            } else if(def.hasExactValue()) {
                return new Exact(def.value);
            } else {
                return new Regex(Pattern.compile(def.rvalue).matcher(""));
            }
        }

        public static Value of(String value, boolean regex) {
            if(value == null || value.trim().isEmpty() || value.trim().equals("*")) {
                return new Any();
            } else if(regex) {
                return new Regex(Pattern.compile(value).matcher(""));
            } else {
                return new Exact(value);
            }
        }
        
        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            return super.hashCode();
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            return this.getClass() == obj.getClass();
        }

        
        protected static class Any extends Value {
            
            @Override
            public boolean matches(String s) {
                return true;
            }
            @Override
            public String getValue() {
                return null;
            }
        }

        protected static class Exact extends Value {

            protected final String exactMatch;

            public Exact(String exactMatch) {
                this.exactMatch = exactMatch;
            }

            @Override
            public boolean matches(String s) {
                return exactMatch.equals(s);
            }
            
            @Override
            public String getValue() {
                return exactMatch;
            }

            /* (non-Javadoc)
             * @see java.lang.Object#hashCode()
             */
            @Override
            public int hashCode() {
                return (exactMatch == null) ? 0 : exactMatch.hashCode();
            }

            /* (non-Javadoc)
             * @see java.lang.Object#equals(java.lang.Object)
             */
            @Override
            public boolean equals(Object obj) {
                if (this == obj)
                    return true;
                if (!super.equals(obj))
                    return false;
                if (getClass() != obj.getClass())
                    return false;
                Exact other = (Exact) obj;
                if (exactMatch == null) {
                    if (other.exactMatch != null)
                        return false;
                } else if (!exactMatch.equals(other.exactMatch))
                    return false;
                return true;
            }

        }

        protected static class Regex extends Value {
            
            protected final Matcher regexMatch;

            public Regex(Matcher regexMatch) {
                this.regexMatch = regexMatch;
            }

            @Override
            public boolean matches(String s) {
                return regexMatch.reset(s).find();
            }
            
            @Override
            public String getValue() {
                return regexMatch.pattern().pattern();
            }

            /* (non-Javadoc)
             * @see java.lang.Object#hashCode()
             */
            @Override
            public int hashCode() {
                return (regexMatch == null) ? 0 : regexMatch.pattern().pattern().hashCode();
            }

            /* (non-Javadoc)
             * @see java.lang.Object#equals(java.lang.Object)
             */
            @Override
            public boolean equals(Object obj) {
                if (this == obj)
                    return true;
                if (!super.equals(obj))
                    return false;
                if (getClass() != obj.getClass())
                    return false;
                Regex other = (Regex) obj;
                if (regexMatch == null) {
                    if (other.regexMatch != null)
                        return false;
                } else if (other.regexMatch == null) {
                    return false;
                } else if (!regexMatch.pattern().pattern().equals(
                        other.regexMatch.pattern().pattern())) {
                    return false;
                }
                return true;
            }

        }
    }

}
