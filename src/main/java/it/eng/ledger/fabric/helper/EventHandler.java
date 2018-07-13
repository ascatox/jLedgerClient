package it.eng.ledger.fabric.helper;

import it.eng.ledger.fabric.config.Chaincode;
import it.eng.ledger.exception.JLedgerClientException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hyperledger.fabric.sdk.BlockEvent;
import org.hyperledger.fabric.sdk.ChaincodeEvent;
import org.hyperledger.fabric.sdk.ChaincodeEventListener;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;

import java.util.Vector;
import java.util.regex.Pattern;

/**
 * @author ascatox
 */
public class EventHandler {

    private final static Logger log = LogManager.getLogger(EventHandler.class);
    private static final String EXPECTED_EVENT_NAME = "EVENT";

    private static EventHandler ourInstance;
    private Channel channel;
    private Chaincode chaincode;

    public static EventHandler getInstance(Channel channel, Chaincode chaincode) throws JLedgerClientException, InvalidArgumentException {
        if (ourInstance == null || !ourInstance.channel.equals(channel) || !ourInstance.chaincode.equals(chaincode)) { //1
            synchronized (EventHandler.class) {
                if (ourInstance == null) {  //2
                    ourInstance = new EventHandler(channel, chaincode);
                }
            }
        }
        return ourInstance;
    }

    private EventHandler(Channel channel, Chaincode chaincode) {
        this.channel = channel;
        this.chaincode = chaincode;
    }

    class ChaincodeEventCapture { //A test class to capture chaincode events
        final String handle;
        final BlockEvent blockEvent;
        final ChaincodeEvent chaincodeEvent;

        ChaincodeEventCapture(String handle, BlockEvent blockEvent, ChaincodeEvent chaincodeEvent) {
            this.handle = handle;
            this.blockEvent = blockEvent;
            this.chaincodeEvent = chaincodeEvent;
        }
    }

    private Vector<ChaincodeEventCapture> chaincodeEvents = new Vector<>(); // Test list to capture chaincode events.

    public String register(String eventName, ChaincodeEventListener chaincodeEventListener) throws JLedgerClientException {
        try {
            // Register a chaincode event listener that will trigger for any chaincode id and only for EXPECTED_EVENT_NAME event.
            String event = EXPECTED_EVENT_NAME;
            if (StringUtils.isNotEmpty(eventName))
                event = eventName;
            String chaincodeEventListenerHandle = channel.registerChaincodeEventListener(Pattern.compile(chaincode.getName()),
                    Pattern.compile(Pattern.quote(event)), chaincodeEventListener

                    /*(handle, blockEvent, chaincodeEvent) -> {

                        chaincodeEvents.add(new ChaincodeEventCapture(handle, blockEvent, chaincodeEvent));

                        String es = blockEvent.getPeer() != null ? blockEvent.getPeer().getName() : blockEvent.getEventHub().getName();
                        log.info("RECEIVED Chaincode event with handle: %s, chaincode id: %s, chaincode event name: %s, "
                                        + "transaction id: %s, event payload: \"%s\", from eventhub: %s",
                                handle, chaincodeEvent.getChaincodeId(),
                                chaincodeEvent.getEventName(),
                                chaincodeEvent.getTxId(),
                                new String(chaincodeEvent.getPayload()), es);
                    }*/

            );
            return chaincodeEventListenerHandle;
        } catch (InvalidArgumentException e) {
            log.error(e);
            throw new JLedgerClientException(e);
        }
    }

    public Vector<ChaincodeEventCapture> getChaincodeEvents() {
        return chaincodeEvents;
    }

    public void unregister(String chaincodeEventListenerHandle) throws JLedgerClientException {
        try {
            channel.unregisterChaincodeEventListener(chaincodeEventListenerHandle);
        } catch (Exception e) {
            log.error(e);
            throw new JLedgerClientException(e);
        }
    }


}
