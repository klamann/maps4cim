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
package de.nx42.maps4cim.gui;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyHelper {

    private static final Logger log = LoggerFactory.getLogger(ProxyHelper.class);

    // registry paths (Preferences-API)
    protected static final String registryPath = "/de/nx42/maps4cim-gui";
    protected static final String regKeyProxySetting = "proxy";
    protected static final String regKeyProxyServer = "proxyServer";
    protected static final String regKeyProxyPort = "proxyPort";

    // preference-object for maps4cim
    protected static final Preferences prefs = Preferences.userRoot().node( registryPath );

    // static helper functions

    public static void restoreProxy() {
        if(hasProxySettings()) {
            ProxySetting ps = getProxySettings();
            String host = getProxyServer();
            String port = getProxyPort();
            ps.setProxySettings(host, port);
        }
    }

    protected static boolean hasProxySettings() {
        return prefs.get(regKeyProxySetting, null) != null;
    }

    protected static boolean hasProxyEnabled() {
        return hasProxySettings() && getProxySettings() != ProxySetting.DIRECT;
    }

    public static ProxySetting getProxySettings() {
        String storedState = prefs.get(regKeyProxySetting, null);
        return ProxySetting.parse(storedState);
    }

    public static String getProxyServer() {
        return prefs.get(regKeyProxyServer, "");
    }

    public static String getProxyPort() {
        return prefs.get(regKeyProxyPort, "");
    }

    public static void setProxySettings(ProxySetting ps, String host, String port) {
        ps.setProxySettings(host, port);
        prefs.put(regKeyProxySetting, ps.toString());
        prefs.put(regKeyProxyServer, host);
        prefs.put(regKeyProxyPort, port);
    }

    // proxy settings (enum)

    public enum ProxySetting {
        DIRECT("direct") {
            @Override
            public void setProxySettings(String host, String port) {
                System.clearProperty("java.net.useSystemProxies");
                System.clearProperty("http.proxyHost");
                System.clearProperty("http.proxyPort");
            }
        },
        SYSTEM("system") {
            @Override
            public void setProxySettings(String host, String port) {
                System.setProperty("java.net.useSystemProxies", "true");

                List<Proxy> proxyList = null;
                try {
                    proxyList = ProxySelector.getDefault().select(new URI("http://foo/bar"));
                } catch (URISyntaxException e) {
                    log.error("error while selecting system proxy", e);
                }

                if (proxyList != null) {
                    for (Proxy proxy : proxyList) {
                        InetSocketAddress addr = (InetSocketAddress) proxy.address();
                        if (addr != null) {
                            System.setProperty("http.proxyHost", addr.getHostName());
                            System.setProperty("http.proxyPort", String.valueOf(addr.getPort()));
                        }
                    }
                }
            }
        },
        CUSTOM("custom") {
            @Override
            public void setProxySettings(String host, String port) {
                System.clearProperty("java.net.useSystemProxies");
                System.setProperty("http.proxyHost", host);
                System.setProperty("http.proxyPort", port);
            }
        };

        protected String name;

        ProxySetting(String name) {
            this.name = name;
        }

        protected abstract void setProxySettings(String host, String port);

        @Override
        public String toString() {
            return name;
        }

        public static ProxySetting parse(String name) {
            for (ProxySetting ps : ProxySetting.values()) {
                if(ps.name.equals(name)) {
                    return ps;
                }
            }
            return null;
        }

    }

}
