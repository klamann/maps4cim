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
package de.nx42.maps4cim.util.math;



/**
 * A collection of static math functions that are not provided in the java
 * standard library.
 * 
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
public class MathExt {

    /**
     * default precision to use when comparing decimals
     * (decimals that are equal to this precision are considered to be equal)
     */
    public final static double defaultPrecision = 0.0000000001;

    /**
     * Compares two double values and checks for equality, using the default
     * precision (see {@link MathExt#defaultPrecision}). Provides more security
     * when comparing primitive decimals in java, when only limited precision
     * is required.
     * 
     * Use {@link MathExt#equalsDouble(double, double, double)} to use a custom
     * precision.
     * 
     * @param a the first value
     * @param b the second value
     * @return true, iff a and b are equal up to the point of
     * {@link MathExt#defaultPrecision}
     */
    public static boolean equalsDouble(double a, double b) {
        return Math.abs(a - b) < defaultPrecision;
    }

    /**
     * Compares two double values and checks for equality, using the defined
     * precision value. Provides more security when comparing primitive decimals
     * in java, when only limited precision is required.
     * 
     * @param a the first value
     * @param b the second value
     * @param precision the precision to use when comparing a and b
     * @return true, iff a and b are equal with respect to the user defined
     * precision
     */
    public static boolean equalsDouble(double a, double b, double precision) {
        return Math.abs(a - b) < precision;
    }

    /**
     * Parses a String as double, removing all characters except for digits and
     * decimal points. If there is a number in there, you will get it...
     * @param s the String to parese
     * @return the number contained in the String
     */
    public static double parseDoubleAggressive(String s) {
        String clean = s.replaceAll("[^x0-9|^\\.]", "").trim();
        return Double.parseDouble(clean);
    }

    /**
     * Parses any amount of numbers, separated by an arbitrary String, using
     * {@link MathExt#parseDoubleAggressive(String)} and returns each value in
     * a new double-array
     * @param s the String to parse
     * @param separator the sequence that separates each number
     * @return a double-array containing all parsed numbers
     * @throws NumberFormatException if at least one of the numbers cannot be
     * parsed as double
     */
    public static double[] parseDoubleValues(String s, String separator) throws NumberFormatException {
        String[] values = s.split(separator);

        double[] result = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = parseDoubleAggressive(values[i]);
        }
        return result;
    }

    /**
     * Rounds a double value using two significant digits and returns it as
     * String
     * @param a the double to round
     * @return the String representation of the rounded value
     */
    public static String roundf(double a) {
        return String.valueOf(Math.round(a * 100) / 100d);
    }

    /**
     * Rounds a double value using the specified amount of significant digits
     * and returns it as String
     * @param a the double to round
     * @param sigDigits the number of significant digits to use
     * @return the String representation of the rounded value
     */
    public static String roundf(double a, int sigDigits) {
        double mul = Math.pow(10, sigDigits);
        return String.valueOf(Math.round(a * mul) / mul);
    }

}
