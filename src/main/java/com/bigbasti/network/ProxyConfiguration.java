package com.bigbasti.network;

import com.bigbasti.model.ProxySelection;
import org.apache.log4j.Logger;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.settings.crypto.DefaultSettingsDecryptionRequest;
import org.apache.maven.settings.crypto.SettingsDecrypter;
import org.apache.maven.settings.crypto.SettingsDecryptionResult;

import java.util.ArrayList;
import java.util.List;

public class ProxyConfiguration {
    private final Logger LOG = Logger.getLogger(this.getClass());

    private ProxySelection proxySelectionSetting;
    private String proxyHost;
    private Integer proxyPort;
    private String proxyProtocol;
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
                // fallback to DEFAULT
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
            }
        } else if (proxySelectionSetting == ProxySelection.SYSTEM){
            // try reading the proxy configuration from the system environment
        }

        LOG.info("defaulting to 'no proxy' configuration");
        // either the configured proxy is ProxySelection.DEFAULT or the desired config could not be found
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
}
