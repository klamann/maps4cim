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
package de.nx42.maps4cim.gui.util;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Locale;
import java.util.TreeSet;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;

/**
 * Alternative implementation to {@link FileNameExtensionFilter}, which differs
 * in one major point:
 *
 * <p><b>Supports filtering of file name extensions which contain dots, e.g. "tar.gz"</b></p>
 *
 * Also, adds some convenient constructors and some other minor goodies
 * (removing of duplicates in accepted file extensions, sorting of extensions,
 * description including file extension list)
 *
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
public class FileExtensionFilter extends FileFilter {

    /** Description of this filter */
    private final String description;
    /** accepted file extensions */
    private final String[] extensions;

    /**
     * Creates a {@code FileExtensionFilter} with the specified description and
     * file name extensions. The returned {@code FileExtensionFilter} will
     * accept all directories and any file with a file name extension contained
     * in {@link #extensions}.
     *
     * @param description textual description for the filter, may be
     * {@code null}. Adds all valid file extensions in parenthesis after the
     * description.
     * @param extensions the accepted file name extensions
     * @throws IllegalArgumentException if extensions is {@code null}, empty,
     * contains {@code null}, or contains an empty string
     * @see #accept
     */
    public FileExtensionFilter(String description, String... extensions) {
        this(description, false, extensions);
    }

    /**
     * Creates a {@code FileExtensionFilter} with the specified description and
     * file name extensions. The returned {@code FileExtensionFilter} will
     * accept all directories and any file with a file name extension contained
     * in {@link #extensions}.
     *
     * @param description textual description for the filter, may be
     * {@code null}. Adds all valid file extensions in parenthesis after the
     * description.
     * @param duplSort removes duplicates and sorts all file extensions, if set
     * to true.
     * @param extensions the accepted file name extensions
     * @throws IllegalArgumentException if extensions is {@code null}, empty,
     * contains {@code null}, or contains an empty string
     * @see #accept
     */
    public FileExtensionFilter(String description, boolean duplSort, String... extensions) {
        if (extensions == null || extensions.length == 0) {
            throw new IllegalArgumentException(
                    "Extensions must be non-null and not empty");
        }

        // prepare extensions
        Collection<String> ext = new LinkedList<String>();
        for (String s : extensions) {
            if (Strings.isNullOrEmpty(s)) {
                throw new IllegalArgumentException(
                    "Each extension must be non-null and not empty");
            }
            ext.add(s.toLowerCase(Locale.ENGLISH));
        }

        // remove duplicates & sort
        if(duplSort) {
            ext = new TreeSet<String>(ext);
        }

        // set extension (final)
        this.extensions = ext.toArray(new String[ext.size()]);

        // generate description
        String extList = Joiner.on(", ").join(this.extensions);
        if(!Strings.isNullOrEmpty(description)) {
            this.description = String.format("%s (%s)", description, extList);
        } else {
            this.description = extList;
        }
    }


    /**
     * Tests the specified file, returning true if the file is accepted, false
     * otherwise. True is returned if the extension matches one of the file
     * name extensions of this {@code FileFilter}, or the file is a directory.
     * @param f the {@code File} to test
     * @return true if the file is to be accepted, false otherwise
     */
    @Override
    public boolean accept(File f) {
        if(f == null) {
            return false;
        }
        if (f.isDirectory()) {
            return true;
        }

        String fileName = f.getName().toLowerCase(Locale.ENGLISH);
        for (String ext : extensions) {
            if(fileName.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    /**
     * The description of this filter.
     * @return the description of this filter
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Returns the set of file name extensions files are tested against.
     * @return the set of file name extensions files are tested against
     */
    public String[] getExtensions() {
        return Arrays.copyOf(extensions, extensions.length);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "FileExtensionFilter [description=" + description
                + ", extensions=" + Arrays.toString(extensions) + "]";
    }

}
