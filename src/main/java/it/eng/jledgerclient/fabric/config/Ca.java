package it.eng.jledgerclient.fabric.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.Objects;

public class Ca {

    private String url;
    private String name;
    @JsonIgnore
    private HFCAClient caClient;

    private final static Logger log = LogManager.getLogger(Ca.class);


    public Ca(String url, String name) {
        this.url = url;
        this.name = name;
    }

    public Ca() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty
    public HFCAClient getCaClient() {
        if (caClient == null) {
            try {
                this.caClient = HFCAClient.createNewInstance(this.getName(), this.getUrl(), null);
                this.caClient.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
            } catch (MalformedURLException | InvalidArgumentException | InstantiationException | InvocationTargetException
                    | NoSuchMethodException | IllegalAccessException | org.hyperledger.fabric.sdk.exception.InvalidArgumentException |
                    CryptoException | ClassNotFoundException e) {
                log.error(e);
            }
        }
        return caClient;
    }

    @JsonIgnore
    public void setCaClient
            (HFCAClient
                     caClient) {
        this.caClient = caClient;
    }

    @Override
    public boolean equals
            (Object
                     o) {
        if (this == o)
            return true;
        if (!(o instanceof Ca))
            return false;
        Ca ca = (Ca) o;
        return Objects.equals(url, ca.url) && Objects.equals(name, ca.name);
    }

    @Override
    public int hashCode
            () {

        return Objects.hash(url, name);
    }
}
