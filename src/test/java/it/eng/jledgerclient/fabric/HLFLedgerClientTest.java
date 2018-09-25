package it.eng.jledgerclient.fabric;

import it.eng.jledgerclient.exception.JLedgerClientException;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author ascatox
 */
public class HLFLedgerClientTest {
    private HLFLedgerClient ledgerClient;

    @Before
    public void setUp() throws Exception {
        ledgerClient = new HLFLedgerClient();
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
            final String result = ledgerClient.doInvoke("putData", args);
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