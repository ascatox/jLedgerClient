package it.eng.jledgerclient.fabric;

import it.eng.jledgerclient.exception.JLedgerClientException;
import it.eng.jledgerclient.fabric.config.ConfigManager;
import it.eng.jledgerclient.fabric.config.Configuration;
import it.eng.jledgerclient.fabric.config.Organization;
import it.eng.jledgerclient.fabric.helper.InvokeReturn;
import it.eng.jledgerclient.fabric.helper.LedgerInteractionHelper;
import it.eng.jledgerclient.fabric.helper.QueryReturn;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hyperledger.fabric.sdk.ChaincodeEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

final public class HLFLedgerClient {

    private final static Logger log = LogManager.getLogger(HLFLedgerClient.class);

    private LedgerInteractionHelper ledgerInteractionHelper;
    private ConfigManager configManager;

    public HLFLedgerClient() throws JLedgerClientException {
        doLedgerClient();
    }


    private void doLedgerClient() throws JLedgerClientException {
        try {
            configManager = ConfigManager.getInstance();
            Configuration configuration = configManager.getConfiguration();
            if (null == configuration || null == configuration.getOrganizations() || configuration.getOrganizations().isEmpty()) {
                log.error("Configuration missing!!! Check you config file!!!");
                throw new JLedgerClientException("Configuration missing!!! Check you config file!!!");
            }
            List<Organization> organizations = configuration.getOrganizations();
            if (null == organizations || organizations.isEmpty())
                throw new JLedgerClientException("Organizations missing!!! Check you config file!!!");
            //for (Organization org : organizations) {
            //FIXME multiple Organizations
            ledgerInteractionHelper = new LedgerInteractionHelper(configManager, organizations.get(0));
            //}
        } catch (Exception e) {
            log.error(e);
            throw new JLedgerClientException(e);
        }
    }


    public String doInvoke(String fcn, List<String> args) throws JLedgerClientException {
        final InvokeReturn invokeReturn = ledgerInteractionHelper.invokeChaincode(fcn, args);
        try {
            log.debug("BEFORE -> Store Completable Future at " + System.currentTimeMillis());
            invokeReturn.getCompletableFuture().get(configManager.getConfiguration().getTimeout(), TimeUnit.MILLISECONDS);
            log.debug("AFTER -> Store Completable Future at " + System.currentTimeMillis());
            final String payload = invokeReturn.getPayload();
            return payload;
        } catch (Exception e) {
            log.error(fcn.toUpperCase() + " " + e.getMessage());
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
            log.error(fcn + " " + e.getMessage());
            throw new JLedgerClientException(fcn + " " + e.getMessage());
        }
    }


    public String doRegisterEvents(String eventName, ChaincodeEventListener chaincodeEventListener) throws JLedgerClientException {
        return ledgerInteractionHelper.getEventHandler().register(eventName, chaincodeEventListener);
    }

    public void doUnregisterEvents(String chaincodeEventListenerHandle) throws JLedgerClientException {
        ledgerInteractionHelper.getEventHandler().unregister(chaincodeEventListenerHandle);
    }

}
