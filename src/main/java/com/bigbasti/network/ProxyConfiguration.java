package com.bigbasti.network;

import com.bigbasti.model.ProxySelection;
import com.google.common.base.Strings;
import org.apache.log4j.Logger;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.settings.crypto.DefaultSettingsDecryptionRequest;
import org.apache.maven.settings.crypto.SettingsDecrypter;
import org.apache.maven.settings.crypto.SettingsDecryptionResult;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class ProxyConfiguration {
    private final Logger LOG = Logger.getLogger(this.getClass());

    private ProxySelection proxySelectionSetting;
    private String proxyHost;
    private Integer proxyPort;
    private String proxyProtocol = "http";
    private String nonProxyHosts = "";
    private String proxyUsername = "";
    private String proxyPassword = "";
    private String proxyName = "no proxy";
    private boolean isProxyConfigredSuccessful;

    /**
     * Create the proxy configuration class based on the configured proxy
     * @param proxySelectionSetting the type of proxy to setup
     */
    public ProxyConfiguration(ProxySelection proxySelectionSetting, MavenSession mavenSession, SettingsDecrypter decrypter) {
        this.proxySelectionSetting = proxySelectionSetting;

        if (proxySelectionSetting == ProxySelection.MAVEN) {
            // try reading the proxy configuration from the maven settings.xml
            if (mavenSession == null ||
                    mavenSession.getSettings() == null ||
                    mavenSession.getSettings().getProxies() == null ||
                    mavenSession.getSettings().getProxies().isEmpty()) {
                LOG.info("could not find a maven proxy - please make sure you are using the right maven config");
                // fallback to SYSTEM
            } else {
                final List<org.apache.maven.settings.Proxy> mavenProxies = mavenSession.getSettings().getProxies();
                final List<org.apache.maven.settings.Proxy> proxies = new ArrayList<org.apache.maven.settings.Proxy>(mavenProxies.size());

                // loop through found maven proxies and select the first active one
                for (org.apache.maven.settings.Proxy mavenProxy : mavenProxies) {
                    if (mavenProxy.isActive()) {
                        mavenProxy = decryptProxy(mavenProxy, decrypter);
                        proxyHost = mavenProxy.getHost();
                        proxyPort = mavenProxy.getPort();
                        proxyProtocol = mavenProxy.getProtocol();
                        proxyName = mavenProxy.getId();
                        proxyUsername = mavenProxy.getUsername();
                        proxyPassword = mavenProxy.getPassword();
                        nonProxyHosts = mavenProxy.getNonProxyHosts();

                        LOG.info("Using proxy " + proxyName + " from maven: " + proxyHost + ":" + proxyPort);
                        isProxyConfigredSuccessful = true;
                        return;
                    }
                }
                LOG.info("could not find an active maven proxy - please check your config");
            }
        } else if (proxySelectionSetting == ProxySelection.ENV){
            // try reading the proxy configuration from the system environment

            // first read the default proxy env variables
            String proxyHostFromSystem = System.getProperty("http.proxyHost", System.getenv("http.proxyHost"));
            String proxyPortFromSystem = System.getProperty("http.proxyPort", System.getenv("http.proxyPort"));
            Integer proxyPortConverted = null;

            try {
                proxyPortConverted = Integer.valueOf(proxyPortFromSystem);

                if (Strings.isNullOrEmpty(proxyHostFromSystem)){
                    LOG.info("could not find a proxy in env variables - please check our system config");
                } else {
                    proxyHost = proxyHostFromSystem;
                    proxyPort = proxyPortConverted;

                    LOG.info("Using env proxy: " + proxyHost + ":" + proxyPort);
                    isProxyConfigredSuccessful = true;
                    return;
                }
            } catch (NumberFormatException ignored) {
                LOG.debug("Invalid proxy port of '" + proxyPortFromSystem + "' found, ignoring...");
            }
        }

        LOG.info("defaulting to 'system proxy' configuration");
        // either the configured proxy is ProxySelection.SYSTEM or the desired config could not be found

        // try reading the default system proxy
        String useSystemProxy = System.getProperty("java.net.useSystemProxies");
        System.setProperty("java.net.useSystemProxies", "true");
        Proxy proxy = getProxy();

        if (null != proxy) {
            if (null != proxy.address()) {
                InetSocketAddress socketAddress = (InetSocketAddress) proxy.address();
                proxyHost = socketAddress.getHostName();
                proxyPort = socketAddress.getPort();
                isProxyConfigredSuccessful = true;

                System.setProperty("http.proxyHost", proxyHost);
                System.setProperty("http.proxyPort", "" + proxyPort);

                LOG.info("Using system proxy: " + proxyHost + ":" + proxyPort);

                // restore original env configuration
                if (Strings.isNullOrEmpty(useSystemProxy)) {
                    System.clearProperty("java.net.useSystemProxies");
                } else {
                    System.setProperty("java.net.useSystemProxies", useSystemProxy);
                }
                return;
            }
        }

        // could not find a valid proxy
        isProxyConfigredSuccessful = false;
    }

    public boolean isProxyConfigured(){
        return isProxyConfigredSuccessful;
    }

    private org.apache.maven.settings.Proxy decryptProxy(org.apache.maven.settings.Proxy proxy, SettingsDecrypter decrypter) {
        final DefaultSettingsDecryptionRequest decryptionRequest = new DefaultSettingsDecryptionRequest(proxy);
        SettingsDecryptionResult decryptedResult = decrypter.decrypt(decryptionRequest);
        return decryptedResult.getProxy();
    }

    /**
     * read prox from the system configuration
     * @return null or system proxy
     */
    private Proxy getProxy() {
        List<Proxy> proxyList = null;
        try {
            proxyList = ProxySelector.getDefault().select(new URI("http://foo.bar"));
        } catch (Exception ignored) {
        }
        if (null != proxyList) {
            for (Proxy proxy : proxyList) {
                if (null != proxy) {
                    return proxy;
                }
            }
        }
        return null;
    }

    public ProxySelection getProxySelectionSetting() {
        return proxySelectionSetting;
    }

    public void setProxySelectionSetting(ProxySelection proxySelectionSetting) {
        this.proxySelectionSetting = proxySelectionSetting;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public Integer getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(Integer proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getProxyProtocol() {
        return proxyProtocol;
    }

    public void setProxyProtocol(String proxyProtocol) {
        this.proxyProtocol = proxyProtocol;
    }

    public String getNonProxyHosts() {
        return nonProxyHosts;
    }

    public void setNonProxyHosts(String nonProxyHosts) {
        this.nonProxyHosts = nonProxyHosts;
    }

    public String getProxyUsername() {
        return proxyUsername;
    }

    public void setProxyUsername(String proxyUsername) {
        this.proxyUsername = proxyUsername;
    }

    public String getProxyPassword() {
        return proxyPassword;
    }

    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }

    public boolean isProxyConfigredSuccessful() {
        return isProxyConfigredSuccessful;
    }

    public void setProxyConfigredSuccessful(boolean proxyConfigredSuccessful) {
        isProxyConfigredSuccessful = proxyConfigredSuccessful;
    }

    @Override
    public String toString() {
        return "ProxyConfiguration{" +
                "proxyHost='" + proxyHost + '\'' +
                ", proxyPort=" + proxyPort +
                '}';
    }
}
