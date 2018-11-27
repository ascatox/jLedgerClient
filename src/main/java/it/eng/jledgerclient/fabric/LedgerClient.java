package it.eng.jledgerclient.fabric;

import it.eng.jledgerclient.exception.JLedgerClientException;
import org.hyperledger.fabric.sdk.ChaincodeEventListener;

import java.util.List;

/**
 * @author ascatox
 *
 * MORE INFO https://hyperledger-fabric.readthedocs.io/en/latest/
 */
public interface LedgerClient {

    /**
     * Invoke method for HLF
     * @param fcn
     * @param args
     * @return
     * @throws JLedgerClientException
     */
    String doInvoke(String fcn, List<String> args) throws JLedgerClientException;

    /**
     * Query method for HLF
     * @param fcn
     * @param args
     * @return
     * @throws JLedgerClientException
     */
    List<String> doQuery(String fcn, List<String> args) throws JLedgerClientException;

    /**
     * Create an Event Subscription to Peer/Channel sent by Chaincode
     * @param eventName
     * @param chaincodeEventListener
     * @return
     */
    String doRegisterEvent(String eventName, ChaincodeEventListener chaincodeEventListener) throws JLedgerClientException;

    /**
     * Remove an Event Subscription to Peer/Channel sent by Chaincode
     * @param chaincodeEventListenerHandle
     * @throws JLedgerClientException
     */
    void doUnregisterEvent(String chaincodeEventListenerHandle) throws JLedgerClientException;
}
