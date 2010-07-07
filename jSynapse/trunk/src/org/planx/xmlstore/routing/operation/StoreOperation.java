package org.planx.xmlstore.routing.operation;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.planx.xmlstore.routing.messaging.*;
import org.planx.xmlstore.routing.Configuration;
import org.planx.xmlstore.routing.Node;
import org.planx.xmlstore.routing.RoutingException;
import org.planx.xmlstore.routing.Identifier;
import org.planx.xmlstore.routing.Space;
import org.planx.xmlstore.routing.TimestampedValue;

/**
 * Stores the mapping at the <i>K</i> closest nodes to the key.
 * If less than Space.K nodes exist in the network the mapping
 * will be stored at all nodes.
 **/
public class StoreOperation extends Operation {
    private Configuration conf;
    private MessageServer server;
    private Space space;
    private Map localMap;
    private Node local;
    private Identifier key;
    private TimestampedValue value;

    public StoreOperation(Configuration conf, MessageServer server, Space space,
                          Map localMap, Node local, Identifier key, TimestampedValue value) {
        if (key == null) throw new NullPointerException("Key is null");
        if (value == null) throw new NullPointerException("Value is null");
        this.conf = conf;
        this.server = server;
        this.space = space;
        this.localMap = localMap;
        this.local = local;
        this.key = key;
        this.value = value;
    }

    /**
     * @return <code>null</code>
     * @throws RoutingException If the operation timed out
     * @throws IOException      If a network error occurred
     **/
    public synchronized Object execute() throws IOException, RoutingException {
        // Perform lookup for K closest nodes
        Operation op = new NodeLookupOperation(conf, server, space, local, key);
        List nodes = (List) op.execute();

        // Send store message to K closest nodes
        Message message = new StoreMessage(local, key, value, false);
        for (int i=0, max=nodes.size(); i < max; i++) {
            Node node = (Node) nodes.get(i);
            if (node.equals(local)) {
                localMap.put(key, value);
            } else {
                server.send(message, node.getInetAddress(), node.getPort(), null);
            }
        }
        return null;
    }
}
