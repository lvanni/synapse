package org.planx.xmlstore.routing.messaging;

/**
 * Thrown to indicate that a message type is unknown or a message with a given
 * communication identifier is unexpected.
 **/
public class UnknownMessageException extends RuntimeException {
    public UnknownMessageException() {
        super();
    }

    public UnknownMessageException(String message) {
        super(message);
    }

    public UnknownMessageException(Throwable cause) {
        super(cause);
    }

    public UnknownMessageException(String message, Throwable cause) {
        super(message, cause);
    }
}
