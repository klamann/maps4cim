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
package de.nx42.maps4cim.util.gis;

import de.nx42.maps4cim.config.bounds.CenterDef.Unit;

/**
 *
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
public enum UnitOfLength {

    DEGREE {
        @Override
        public double toKilometer(double input) {
            return Geo.degreeOfLatitudeLength * input;
        }
        @Override
        public double fromKilometer(double input) {
            return input / Geo.degreeOfLatitudeLength;
        }
        @Override
        public double toKilometer(double input, double atLatitude) {
            return Geo.degreeOfLongitudeLength(atLatitude) * input;
        }
        @Override
        public double fromKilometer(double input, double atLatitude) {
            return input / Geo.degreeOfLongitudeLength(atLatitude);
        }
    },
    METER {
        @Override
        public double toKilometer(double input) {
            return input / 1000;
        }
        @Override
        public double fromKilometer(double input) {
            return input * 1000;
        }
        @Override
        public double toKilometer(double input, double atLatitude) {
            return toKilometer(input);
        }
        @Override
        public double fromKilometer(double input, double atLatitude) {
            return fromKilometer(input);
        }
    },
    KILOMETER {
        @Override
        public double toKilometer(double input) {
            return input;
        }
        @Override
        public double fromKilometer(double input) {
            return input;
        }
        @Override
        public double toKilometer(double input, double atLatitude) {
            return input;
        }
        @Override
        public double fromKilometer(double input, double atLatitude) {
            return input;
        }
    };


    public abstract double toKilometer(double input);

    public abstract double fromKilometer(double input);

    public abstract double toKilometer(double input, double atLatitude);

    public abstract double fromKilometer(double input, double atLatitude);

    public double convert(double input, UnitOfLength output) {
        return output.fromKilometer(this.toKilometer(input));
    }

    public double convert(double input, UnitOfLength output, double atLatitude) {
        return output.fromKilometer(this.toKilometer(input, atLatitude), atLatitude);
    }

    public static UnitOfLength fromUnit(Unit unit) {
        switch (unit) {
            case DEG: return DEGREE;
            case KM:  return KILOMETER;
            case M:   return METER;
            default:  return null;
        }
    }

}
