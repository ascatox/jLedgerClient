package it.eng.jledgerclient.fabric.config;

import java.io.File;
import java.io.InputStream;

/**
 * @author ascatox
 */
public class Certificates {
    private InputStream configFabricNetwork;
    private InputStream certificate;
    private InputStream keystore;
    private File certificateTls;

    public Certificates(InputStream configFabricNetwork, InputStream certificate, InputStream keystore) {
        this.configFabricNetwork = configFabricNetwork;
        this.certificate = certificate;
        this.keystore = keystore;
    }

    public Certificates(InputStream configFabricNetwork, InputStream certificate, InputStream keystore, File certificateTls) {
        this.configFabricNetwork = configFabricNetwork;
        this.certificate = certificate;
        this.keystore = keystore;
        this.certificateTls = certificateTls;
    }


    public InputStream getConfigFabricNetwork() {
        return configFabricNetwork;
    }

    public void setConfigFabricNetwork(InputStream configFabricNetwork) {
        this.configFabricNetwork = configFabricNetwork;
    }

    public InputStream getCertificate() {
        return certificate;
    }

    public void setCertificate(InputStream certificate) {
        this.certificate = certificate;
    }

    public InputStream getKeystore() {
        return keystore;
    }

    public void setKeystore(InputStream keystore) {
        this.keystore = keystore;
    }

    public File getCertificateTls() {
        return certificateTls;
    }

    public void setCertificateTls(File certificateTls) {
        this.certificateTls = certificateTls;
    }

    public boolean isCertTlsGiven() {
        if (null == certificateTls || !certificateTls.exists())
            return false;
        return true;
    }

    public boolean areGiven() {
        if (null == certificate
                || null == keystore)
            return false;
        return true;
    }
}
