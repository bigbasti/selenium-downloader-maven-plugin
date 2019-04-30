package com.bigbasti.model;

public class FileDownload {
    private OS os;
    private Browser driver;
    private String version;
    private Architecture architecture;
    private String url;
    private String hash;
    private Algorithm algorithm;

    public FileDownload(OS os, Browser driver, String version, Architecture architecture, String url, String hash, Algorithm algorithm) {
        this.os = os;
        this.driver = driver;
        this.version = version;
        this.architecture = architecture;
        this.url = url;
        this.hash = hash;
        this.algorithm = algorithm;
    }

    public OS getOs() {
        return os;
    }

    public void setOs(OS os) {
        this.os = os;
    }

    public Browser getDriver() {
        return driver;
    }

    public void setDriver(Browser driver) {
        this.driver = driver;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Architecture getArchitecture() {
        return architecture;
    }

    public void setArchitecture(Architecture architecture) {
        this.architecture = architecture;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    @Override
    public String toString() {
        return "FileDownload{" +
                "os=" + os +
                ", driver=" + driver +
                ", version='" + version + '\'' +
                ", architecture=" + architecture +
                ", url='" + url + '\'' +
                ", hash='" + hash + '\'' +
                ", algorithm=" + algorithm +
                '}';
    }
}
