package com.bigbasti.network;

import com.bigbasti.model.Proxy;
import org.apache.log4j.Logger;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.settings.crypto.DefaultSettingsDecryptionRequest;
import org.apache.maven.settings.crypto.SettingsDecrypter;
import org.apache.maven.settings.crypto.SettingsDecryptionResult;

import java.util.ArrayList;
import java.util.List;

public class ProxyConfiguration {
    private final Logger LOG = Logger.getLogger(this.getClass());

    private Proxy proxySetting;
    private String proxyHost;
    private String proxyPort;
    private String nonProxyHosts;
    private boolean isProxyConfigredSuccessful;

    /**
     * Create the proxy configuration class based on the configured proxy
     * @param proxySetting the type of proxy to setup
     */
    public ProxyConfiguration(Proxy proxySetting, MavenSession mavenSession, SettingsDecrypter decrypter) {
        this.proxySetting = proxySetting;

        if (proxySetting == Proxy.MAVEN) {
            // try reading the proxy configuration from the maven settings.xml
            if (mavenSession == null ||
                    mavenSession.getSettings() == null ||
                    mavenSession.getSettings().getProxies() == null ||
                    mavenSession.getSettings().getProxies().isEmpty()) {
                // fallback to DEFAULT
            } else {
                final List<org.apache.maven.settings.Proxy> mavenProxies = mavenSession.getSettings().getProxies();

                final List<org.apache.maven.settings.Proxy> proxies = new ArrayList<org.apache.maven.settings.Proxy>(mavenProxies.size());

                for (org.apache.maven.settings.Proxy mavenProxy : mavenProxies) {
                    if (mavenProxy.isActive()) {
                        mavenProxy = decryptProxy(mavenProxy, decrypter);
                        proxies.add(new ProxyConfig.Proxy(mavenProxy.getId(), mavenProxy.getProtocol(), mavenProxy.getHost(),
                                mavenProxy.getPort(), mavenProxy.getUsername(), mavenProxy.getPassword(), mavenProxy.getNonProxyHosts()));
                    }
                }

                LOG.info("Found proxies: {}", proxies);
                return new ProxyConfig(proxies);
            }
        } else if (proxySetting == Proxy.SYSTEM){

        }

        // either the configured proxy is Proxy.DEFAULT or the desired config could not be found
        isProxyConfigredSuccessful = false;
    }

    public boolean isProxyConfigured(){
        return isProxyConfigredSuccessful;
    }

    public Proxy getProxy(){
        return null;
    }

    private org.apache.maven.settings.Proxy decryptProxy(org.apache.maven.settings.Proxy proxy, SettingsDecrypter decrypter) {
        final DefaultSettingsDecryptionRequest decryptionRequest = new DefaultSettingsDecryptionRequest(proxy);
        SettingsDecryptionResult decryptedResult = decrypter.decrypt(decryptionRequest);
        return decryptedResult.getProxy();
    }
}
