package it.eng.ledger.exception;


public class JLedgerClientException extends Exception {

    public JLedgerClientException() {
        super();
    }

    public JLedgerClientException(String message) {
        super(message);
    }

    public JLedgerClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public JLedgerClientException(Throwable cause) {
        super(cause);
    }

    protected JLedgerClientException(String message, Throwable cause, boolean enableSuppression, boolean
            writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }



}

