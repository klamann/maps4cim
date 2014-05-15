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
package de.nx42.maps4cim.util.math;

import java.lang.reflect.Array;
import java.util.Arrays;


public class Statistics {

    /** the number of elements in the array */
    protected int samples;
    /** the highest data point in the set */
    protected double max;
    /** the lowest data point in the set */
    protected double min;
    /** a.k.a. average. Sum of all the values divided by the number of values. */
    protected double mean;
    /** The midpoint of the data after being ranked (sorted in ascending order).
        There are as many numbers below the median as above the median. */
    protected double median;
    /** The mean of the highest and lowest values. (Max + Min) / 2 */
    protected double midrange;
    /** The difference between the highest and lowest values. Max - Min */
    protected double range;
    /** lower Quartile, or 25th Percentile:
        25 percent of the population lie below this value */
    protected double lowerQuartile;
    /** upper Quartile, or 75th Percentile:
        75 percent of the population lie below this value */
    protected double upperQuartile;
    /** Interquartile range (also called the midspread or middle fifty),
        is a measure of statistical dispersion, being equal to the difference
        between the upper and lower quartiles */
    protected double iqr;
    /** the amount of different / unique values in the set */
    protected int uniques;

    public Statistics() {}

    public Statistics(Object numberArray) {
        this(numberArray, true);
    }

    public Statistics(Object numberArray, boolean signed) {
        double[] input = getDoubleArray(numberArray, signed);
        calculate(input);
    }

    public Statistics(double[] numberArray) {
        calculate(numberArray);
    }

    protected void calculate(double[] input) {
        Arrays.sort(input);

        samples = input.length;
        max = input[input.length - 1];
        min = input[0];
        mean = getMean(input);
        median = input[input.length / 2];
        midrange = (min + max) / 2;
        range = max - min;
        lowerQuartile = input[(int) (input.length * 0.25)];
        upperQuartile = input[(int) (input.length * 0.75)];
        iqr = upperQuartile - lowerQuartile;
        uniques = countUniques(input);
    }

    protected static double getMean(double[] input) {
        double sum = 0;
        for (double d : input) {
            sum += d;
        }
        return sum / input.length;
    }

    protected static int countUniques(double[] sortedInput) {
        int count = 0;
        double last = Double.NaN;
        for (double d : sortedInput) {
            if(d != last) {
                last = d;
                count++;
            }
        }
        return count;
    }

    public static double[] getDoubleArray(Object numberArray) {
        return getDoubleArray(numberArray, true);
    }

    public static double[] getDoubleArray(Object numberArray, boolean signed) {
        int arrlength = Array.getLength(numberArray);
        double[] outputArray = new double[arrlength];
        for(int i = 0; i < arrlength; ++i){
            if(signed) {
                outputArray[i] = ((Number) Array.get(numberArray, i)).doubleValue();
            } else {
                Number n = ((Number) Array.get(numberArray, i));
                if(n instanceof Byte) {
                    outputArray[i] = (n.byteValue() & 0xff);
                } else if(n instanceof Short) {
                    outputArray[i] = (n.shortValue() & 0xffff);
                } else if(n instanceof Integer) {
                    outputArray[i] = (n.intValue() & 0xffffffff);
                } else if(n instanceof Long) {
                    outputArray[i] = (n.longValue() & 0xffffffffffffffffL);
                } else {
                    outputArray[i] = n.doubleValue();
                }
            }
        }
        return outputArray;
    }



    public static Statistics of(int samples, double max, double min,
            double mean, double median, double lowerQuartile,
            double upperQuartile, int uniques) {
        Statistics s = new Statistics();
        s.samples = samples;
        s.max = max;
        s.min = min;
        s.mean = mean;
        s.median = median;
        s.midrange = (max + min) / 2.0;
        s.range = max - min;
        s.lowerQuartile = lowerQuartile;
        s.upperQuartile = upperQuartile;
        s.iqr = upperQuartile - lowerQuartile;
        s.uniques = uniques;
        return s;
    }

    /**
     * @return img.getProcessor()
     */
    public int getSamples() {
        return samples;
    }

    /**
     * @return the highest data point in the set
     */
    public double getMax() {
        return max;
    }

    /**
     * @see the lowest data point in the set
     */
    public double getMin() {
        return min;
    }

    /**
     * @return a.k.a. average. Sum of all the values divided by the number of
     * values.
     */
    public double getMean() {
        return mean;
    }

    /**
     * @return The midpoint of the data after being ranked (sorted in ascending
     * order). There are as many numbers below the median as above the median.
     */
    public double getMedian() {
        return median;
    }

    /**
     * @return The mean of the highest and lowest values. (Max + Min) / 2
     */
    public double getMidrange() {
        return midrange;
    }

    /**
     * @return The difference between the highest and lowest values. Max - Min
     */
    public double getRange() {
        return range;
    }

    /**
     * @return first Quartile, or 25th Percentile: 25 percent of the population
     * lie below this value
     */
    public double getLowerQuartile() {
        return lowerQuartile;
    }

    /**
     * @return third Quartile, or 75th Percentile: 75 percent of the population
     * lie below this value
     */
    public double getUpperQuartile() {
        return upperQuartile;
    }

    /**
     * @return the amount of different / unique values in the set
     */
    public int getUniques() {
        return uniques;
    }

    /**
     * @return Interquartile range (also called the midspread or middle fifty),
     * is a measure of statistical dispersion, being equal to the difference
     * between the upper and lower quartiles
     */
    public double getIqr() {
        return iqr;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Statistics [samples=" + samples + ", max=" + max + ", min="
                + min + ", mean=" + mean + ", median=" + median + ", midrange="
                + midrange + ", range=" + range + ", lowerQuartile="
                + lowerQuartile + ", upperQuartile=" + upperQuartile + ", iqr="
                + iqr + ", uniques=" + uniques + "]";
    }

}
