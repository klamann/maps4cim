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

import java.io.IOException;
import java.io.InputStream;

/**
 * Knuth-Morris-Pratt Algorithm for Pattern Matching
 */
public class KMPMatch {


    /**
     * Finds the first occurrence of the pattern in the byte-stream
     * @throws IOException
     */
    public static int indexOf(InputStream stream, byte[] pattern) throws IOException {
        if (stream.available() < 1) {
            return -1;
        }

        stream.mark(Integer.MAX_VALUE);
        try {
            int[] failure = computeFailure(pattern);
            int i = 0;
            int j = 0;
            int last = 0;
            byte data;

            while (last != -1) {
                last = stream.read();
                data = (byte) last;

                while (j > 0 && pattern[j] != data) {
                    j = failure[j - 1];
                }
                if (pattern[j] == data) {
                    j++;
                }
                if (j == pattern.length) {
                    return i - pattern.length + 1;
                }
                i++;
            }
        } finally {
            stream.reset();
        }

        return -1;
    }

    /**
     * Finds the first occurrence of the pattern in the byte-array
     */
    public static int indexOf(byte[] data, byte[] pattern) {
        return indexOf(data, pattern, 0);
    }


    /**
     * Finds the first occurrence of the pattern in the byte-array
     */
    public static int indexOf(byte[] data, byte[] pattern, int off) {
        if (data.length == 0 || off >= data.length) {
            return -1;
        }
        int[] failure = computeFailure(pattern);

        int j = 0;
        for (int i = off; i < data.length; i++) {
            while (j > 0 && pattern[j] != data[i]) {
                j = failure[j - 1];
            }
            if (pattern[j] == data[i]) {
                j++;
            }
            if (j == pattern.length) {
                return i - pattern.length + 1;
            }
        }

        return -1;
    }


    /**
     * Computes the failure function using a boot-strapping process,
     * where the pattern is matched against itself.
     */
    private static int[] computeFailure(byte[] pattern) {
        int[] failure = new int[pattern.length];

        int j = 0;
        for (int i = 1; i < pattern.length; i++) {
            while (j > 0 && pattern[j] != pattern[i]) {
                j = failure[j - 1];
            }
            if (pattern[j] == pattern[i]) {
                j++;
            }
            failure[i] = j;
        }

        return failure;
    }

}