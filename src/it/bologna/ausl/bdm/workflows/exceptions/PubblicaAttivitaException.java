package it.bologna.ausl.bdm.workflows.exceptions;

import it.bologna.ausl.bdm.exception.ProcessWorkFlowException;

/**
 *
 * @author gdm
 */
public class PubblicaAttivitaException extends ProcessWorkFlowException {

    public PubblicaAttivitaException(String message) {
        super(message);
    }

    public PubblicaAttivitaException(Throwable cause) {
        super(cause);
    }

    public PubblicaAttivitaException(String message, Throwable cause) {
        super(message, cause);
    }
}
