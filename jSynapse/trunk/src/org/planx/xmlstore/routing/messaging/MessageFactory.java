package org.planx.xmlstore.routing.messaging;

import java.io.DataInput;
import java.io.IOException;

/**
 * Creates Message and Receiver objects based on message code types.
 **/
public interface MessageFactory {
    /**
     * Creates a Message of type <code>code</code> from the specified stream.
     *
     * @throws UnknownMessageException if the message code type is not supported
     **/
    public Message createMessage(byte code, DataInput in)
                   throws IOException, UnknownMessageException;

    /**
     * Creates a Receiver for handling a Message of type <code>code</code>.
     * If no standard Receiver should handle the message <code>null</code> is
     * returned. In this case, an application specific receiver will be used
     * if one is registered.
     **/
    public Receiver createReceiver(byte code, MessageServer server);
}
