package org.planx.xmlstore.routing.operation;

import java.io.IOException;
import org.planx.xmlstore.routing.Node;
import org.planx.xmlstore.routing.Space;
import org.planx.xmlstore.routing.messaging.*;

/**
 * Handles updating of the local space when messages are received and provides
 * easy implementation of more specific receivers.
 **/
public abstract class OriginReceiver implements Receiver {
    protected MessageServer server;
    protected Node local;
    protected Space space;

    public OriginReceiver(MessageServer server, Node local, Space space) {
        this.server = server;
        this.local = local;
        this.space = space;
    }

    /**
     * Updates the local space by inserting the origin node. Note that if the
     * node already exists in the local space, it's time last seen is updated
     * instead.
     **/
    public void receive(Message incoming, int comm) throws IOException {
        OriginMessage mess = (OriginMessage) incoming;
        space.insertNode(mess.getOrigin());
    }

    /**
     * Does nothing, can be overridden by subclasses to change timeout functionality.
     **/
    public void timeout(int comm) throws IOException {}
}
