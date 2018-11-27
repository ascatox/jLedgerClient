package it.eng.jledgerclient.fabric;

import it.eng.jledgerclient.exception.JLedgerClientException;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author ascatox
 */
public class HLFLedgerClientTestWithFiles {
    private HLFLedgerClient ledgerClient;
    private String keystore = "/Users/ascatox/Desktop/crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/keystore/b757dc7a858985bd4d62e04e796d5f8bccff7d06ae3fe6540c3613e6467005d7_sk";
    private String cert = "/Users/ascatox/Desktop/crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/signcerts/Admin@org1.example.com-cert.pem";
    private String config = "/Users/ascatox/Documents/Sviluppo/workspace_hyperledger/jLedgerClient/src/test/resources/config-fabric-network.json";
    private String certTls = "/Users/ascatox/Desktop/crypto-config/ordererOrganizations/example.com/orderers/orderer.example.com/tls/server.crt";

    @Before
    public void setUp() throws Exception {
        File keystore = new File(this.keystore);
        InputStream keystoreIn = FileUtils.openInputStream(keystore);
        File cert = new File(this.cert);
        InputStream certIn = FileUtils.openInputStream(cert);
        File config = new File(this.config);
        InputStream configIn = FileUtils.openInputStream(config);
        File certTls = new File(this.certTls);

        ledgerClient = new HLFLedgerClient(configIn, certIn, keystoreIn, certTls);
    }

    @After
    public void tearDown() throws Exception {
        ledgerClient = null;
    }

    // This test case use analytics-chaincode download/install/instantiate
    // from here https://github.com/ascatox/analytics-chaincode
    @Test
    public void doInvoke() {
        List<String> args = new ArrayList<>();
        args.add("1");
        args.add("Test 2 Invoke");
        try {
            ledgerClient.doInvoke("putData", args);
            args.remove(1);
            final String result = ledgerClient.doInvoke("getData", args);
            assertNotNull(result);
            assertNotEquals("Not empty value expected", result, "");
        } catch (JLedgerClientException e) {
            assertFalse(e.getMessage(), true);
        }
    }

    @Ignore
    @Test
    public void doQuery() {
    }

    @Ignore
    @Test
    public void doRegisterEvent() {
    }

    @Ignore
    @Test
    public void doUnregisterEvent() {
    }

}