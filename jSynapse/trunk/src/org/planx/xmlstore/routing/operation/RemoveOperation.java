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

/**
 * Removes the mapping at the <i>K</i> closest nodes to the key of the mapping.
 * If less than Space.K nodes exist in the network the mapping will be removed
 * at all nodes.
 **/
public class RemoveOperation extends Operation {
    private Configuration conf;
    private MessageServer server;
    private Space space;
    private Map localMap;
    private Node local;
    private Identifier key;

    public RemoveOperation(Configuration conf, MessageServer server, Space space,
                           Map localMap, Node local, Identifier key) {
        if (key == null) throw new NullPointerException("Key is null");
        this.conf = conf;
        this.server = server;
        this.space = space;
        this.localMap = localMap;
        this.local = local;
        this.key = key;
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

        // Send remove message to K closest nodes
        Message message = new RemoveMessage(local, key);
        for (int i=0, max=nodes.size(); i < max; i++) {
            Node node = (Node) nodes.get(i);
            if (node.equals(local)) {
                localMap.remove(key);
            } else {
                server.send(message, node.getInetAddress(), node.getPort(), null);
            }
        }
        return null;
    }
}
