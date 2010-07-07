package org.planx.xmlstore.routing.messaging;

import java.io.IOException;

/**
 * A Receiver waits for incoming messages and performs some action if various
 * events occur.
 **/
public interface Receiver {
    /**
     * Message is received with communication identifier <code>comm</code>.
     * The Receiver should use this value when sending replies using
     * {@link MessageServer#reply}.
     *
     * @throws IOException             if an I/O error occurs
     * @throws UnknownMessageException if the message or <code>comm</code> was
     *                                 unexpected
     **/
    public void receive(Message incoming, int comm)
       throws IOException, UnknownMessageException;

    /**
     * No reply received in <code>MessageServer.TIMEOUT</code> seconds for the
     * message with communication id <code>comm</code>.
     *
     * @throws IOException             if an I/O error occurs
     * @throws UnknownMessageException if the <code>comm</code> was unexpected
     **/
    public void timeout(int comm) throws IOException, UnknownMessageException;
}
