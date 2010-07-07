package org.planx.xmlstore.routing.operation;

import java.io.IOException;
import java.io.Serializable;
import org.planx.xmlstore.routing.Configuration;
import org.planx.xmlstore.routing.Identifier;
import org.planx.xmlstore.routing.RoutingException;
import org.planx.xmlstore.routing.Node;
import org.planx.xmlstore.routing.Space;
import org.planx.xmlstore.routing.messaging.*;

/**
 * Looks up the specified identifier and returns the value associated with it.
 * It is not checked if the data is available in the local IdentifierMap, so
 * this should be checked before this operation is executed.
 * The subclassing is merely for code reuse.
 **/
public class DataLookupOperation extends NodeLookupOperation {
    /** The found value or <code>null</code> if it has not been found. */
    protected Serializable value = null;

    public DataLookupOperation(Configuration conf, MessageServer server, Space space,
                                                           Node local, Identifier id) {
        super(conf, server, space, local, id);
        // Change lookup message sent to peers
        lookupMessage = new DataLookupMessage(local, id);
    }

    /**
     * @return The value associated with the key or <code>null</code> if no
     *         mapping with the specified key could be found.
     *
     * @throws RoutingException If the lookup operation timed out
     * @throws IOException      If a network error occurred
     **/
    public synchronized Object execute() throws IOException, RoutingException {
        super.execute();
        return value;
    }

    /**
     * Once a DataMessage is received the algorithm terminates.
     * If a NodeReplyMessage is received the algorithm continues as by
     * the superclass.
     **/
    public synchronized void receive(Message incoming, int comm) throws IOException {
        if (incoming instanceof DataMessage) {
            DataMessage mess = (DataMessage) incoming;
            space.insertNode(mess.getOrigin());
            if (id.equals(mess.getKey())) {
                value = mess.getValue().getObject();
                error = false;
                notify();
            } else {
                throw new IOException("Key in DataMessage received does not "+
                                      "match looked up key");
            }
        } else {
            super.receive(incoming, comm);
        }
    }
}
