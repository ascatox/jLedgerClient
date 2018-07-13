package it.eng.ledger.fabric.base;


import it.eng.ledger.exception.JLedgerClientException;
import org.hyperledger.fabric.sdk.ChaincodeEventListener;

import java.util.List;

public interface LedgerClient {

    String doInvoke(String fcn, List<String> args) throws JLedgerClientException;

    List<String> doQuery(String fcn, List<String> args) throws JLedgerClientException;

    String doRegisterEvents(String eventName, ChaincodeEventListener chaincodeEventListener) throws JLedgerClientException;

    void doUnregisterEvents(String chaincodeEventListenerHandle) throws JLedgerClientException;

}