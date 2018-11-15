package it.eng.jledgerclient.fabric.config;

import java.io.InputStream;

/**
 * @author ascatox
 */
public class Certificates {
    private InputStream configFabricNetwork;
    private InputStream certificate;
    private InputStream keystore;

    public Certificates(InputStream configFabricNetwork, InputStream certificate, InputStream keystore) {
        this.configFabricNetwork = configFabricNetwork;
        this.certificate = certificate;
        this.keystore = keystore;
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

    public boolean areGiven() {
        if (null == certificate
                || null == keystore)
            return false;
        return true;
    }
}
