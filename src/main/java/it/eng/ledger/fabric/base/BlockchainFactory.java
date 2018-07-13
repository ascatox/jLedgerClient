package it.eng.ledger.fabric.base;

import it.eng.ledger.fabric.HLFLedgerClient;
import it.eng.ledger.exception.JLedgerClientException;

import java.util.Properties;

/**
 * @author ascatox
 * This class decides the type of blockchain we can implement
 */
public final class BlockchainFactory {
    /**
     * @param type describer the type of blockchain
     */
//    private static Properties resourceBundle = ResourceBundle.getBundle("application", java.util.Locale.getDefault());
    private static Properties properties = new Properties();
    private static String type = properties.getProperty("BLOCKCHAIN_TYPE");

    public LedgerClient getType(BlockchainType blockchainType) throws JLedgerClientException {
        if (blockchainType.equals(BlockchainType.HL_FABRIC))
            return new HLFLedgerClient();
        return null;

    }

    public LedgerClient getType() throws JLedgerClientException {
        return new HLFLedgerClient();
    }
}
