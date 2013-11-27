package de.nx42.maps4cim.gui.util;

import java.util.prefs.Preferences;

public class PrefsHelper {

    // base path for maps4cim-gui (Preferences-API)
    protected static final String registryPath = "/de/nx42/maps4cim-gui";

    // Preferences-object for maps4cim-gui
    protected static final Preferences prefs = Preferences.userRoot().node( registryPath );

    /**
     * @return the Preferences-Object
     */
    public static Preferences getPrefs() {
        return prefs;
    }

    public static boolean exists(String key) {
        return prefs.get(key, null) != null;
    }

}
