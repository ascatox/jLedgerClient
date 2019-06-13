package it.eng.jledgerclient.fabric;

import it.eng.jledgerclient.exception.JLedgerClientException;
import it.eng.jledgerclient.fabric.config.Certificates;
import it.eng.jledgerclient.fabric.config.ConfigManager;
import it.eng.jledgerclient.fabric.config.Configuration;
import it.eng.jledgerclient.fabric.config.Organization;
import it.eng.jledgerclient.fabric.helper.InvokeReturn;
import it.eng.jledgerclient.fabric.helper.LedgerInteractionHelper;
import it.eng.jledgerclient.fabric.helper.QueryReturn;
import org.hyperledger.fabric.sdk.ChaincodeEventListener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class HLFLedgerClient {

    protected final static Logger log = Logger.getLogger(HLFLedgerClient.class.getName());

    protected LedgerInteractionHelper ledgerInteractionHelper;
    protected ConfigManager configManager;

    public HLFLedgerClient() {
        doLedgerClient(null, null, null);
    }

    public HLFLedgerClient(InputStream configFabricNetwork,
                           InputStream certificate, InputStream keystore) {
        doLedgerClient(configFabricNetwork, certificate, keystore);
    }

    protected void doLedgerClient(InputStream configFabricNetwork,
                                InputStream certificate, InputStream keystore) {
        try {
            Certificates certificates = new Certificates(configFabricNetwork, certificate, keystore);
            configManager = ConfigManager.getInstance(certificates);
            Configuration configuration = configManager.getConfiguration();
            if (null == configuration || null == configuration.getOrganizations() || configuration.getOrganizations().isEmpty()) {
                log.severe("Configuration missing!!! Check you config file!!!");
                throw new JLedgerClientException("Configuration missing!!! Check your config file!!!");
            }
            List<Organization> organizations = configuration.getOrganizations();
            if (null == organizations || organizations.isEmpty())
                throw new JLedgerClientException("Organizations missing!!! Check your config file!!!");
            //for (Organization org : organizations) {
            //FIXME multiple Organizations
            ledgerInteractionHelper = new LedgerInteractionHelper(configManager, organizations.get(0));
            //}
        } catch (Exception e) {
            log.severe(e.getMessage());
        }
    }


    public String doInvoke(String fcn, List<String> args) throws JLedgerClientException {
        final InvokeReturn invokeReturn = ledgerInteractionHelper.invokeChaincode(fcn, args);
        try {
            String payload = null;
            log.fine("BEFORE -> Store Completable Future at " + System.currentTimeMillis());
            // final CompletableFuture<Object> completedFuture = invokeReturn.getCompletableFuture().completedFuture("message");
            // get(configManager.getConfiguration().getTimeout(), TimeUnit.MILLISECONDS);
            // if(completedFuture.isDone()) {
             payload = invokeReturn.getPayload();
            // }
            return payload;
        } catch (Exception e) {
            log.severe(fcn.toUpperCase() + " " + e.getMessage());
            throw new JLedgerClientException(fcn + " " + e.getMessage());
        }
    }


    public List<String> doQuery(String fcn, List<String> args) throws JLedgerClientException {
        List<String> data = new ArrayList<>();
        try {
            final List<QueryReturn> queryReturns = ledgerInteractionHelper.queryChainCode(fcn, args, null);
            for (QueryReturn queryReturn : queryReturns) {
                data.add(queryReturn.getPayload());
            }
            return data;
        } catch (Exception e) {
            log.severe(fcn + " " + e.getMessage());
            throw new JLedgerClientException(fcn + " " + e.getMessage());
        }
    }


    public String doRegisterEvent(String eventName, ChaincodeEventListener chaincodeEventListener) throws JLedgerClientException {
        return ledgerInteractionHelper.getEventHandler().register(eventName, chaincodeEventListener);
    }

    public void doUnregisterEvent(String chaincodeEventListenerHandle) throws JLedgerClientException {
        ledgerInteractionHelper.getEventHandler().unregister(chaincodeEventListenerHandle);
    }

}
