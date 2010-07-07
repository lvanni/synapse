package org.planx.xmlstore.routing.operation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import org.planx.xmlstore.routing.Configuration;
import org.planx.xmlstore.routing.Identifier;
import org.planx.xmlstore.routing.Node;
import org.planx.xmlstore.routing.RoutingException;
import org.planx.xmlstore.routing.Space;
import org.planx.xmlstore.routing.messaging.*;

/**
 * Finds the <i>K</i> closest nodes to a specified identifier.
 * The algorithm terminates when the operation has gotten responses from the
 * <i>K</i> closest nodes it has seen. Nodes that fail to respond are
 * removed from consideration.
 **/
public class NodeLookupOperation extends Operation implements Receiver {
    private static final Byte UNASKED  = new Byte((byte) 0x00);
    private static final Byte AWAITING = new Byte((byte) 0x01);
    private static final Byte ASKED    = new Byte((byte) 0x02);
    private static final Byte FAILED   = new Byte((byte) 0x03);

    protected Configuration conf;
    protected MessageServer server;
    protected Space space;
    protected Node local;
    protected Identifier id;
    protected boolean error;

    /**
     * Message sent to each peer. Subclasses could change this field to send
     * other kinds of lookup messages.
     **/
    protected LookupMessage lookupMessage;

    /**
     * Comparator used to select CONCURRENCY nodes among the K closest nodes.
     **/
    private Comparator cmp;

    /**
     * Keeps track of which nodes have been asked and gotten reply from.
     * Nodes are keys mapped to a Byte indicating the status.
     * The map is kept sorted according to closeness to the sought after identifier.
     **/
    private SortedMap nodes;

    /**
     * Keeps track of messages in transit and awaiting reply. No more than
     * <code>CONCURRENCY</code> message may be in transit and unreplied at
     * the same time. Communication ids as Integers are keys and Nodes are
     * values.
     **/
    private Map transit;

    /**
     * Create a NodeLookupOperation for the specified <code>id</code>.
     **/
    public NodeLookupOperation(Configuration conf, MessageServer server,
                               Space space, Node local, Identifier id) {
        this.conf = conf;
        this.server = server;
        this.space = space;
        this.local = local;
        this.id = id;
        lookupMessage = new NodeLookupMessage(local, id);
        Comparator distCmp = new Node.DistanceComparator(id);
        nodes = new TreeMap(distCmp);
        transit = new HashMap();
        cmp = distCmp;
    }

    /**
     * @return A List containing the <i>K</i> closest nodes to the id provided
     *         in the constructor.
     * @throws RoutingException If the lookup operation timed out
     * @throws IOException      If a network error occurred
     **/
    public synchronized Object execute() throws IOException, RoutingException {
        try {
            error = true;
            nodes.put(local, ASKED);
            addNodes(space.getAll());
            if (!askNodesOrFinish()) {
                // Nodes were asked, wait for termination
                wait(conf.OPERATION_TIMEOUT);
                if (error) throw new RoutingException("Lookup timeout");
            }
            return closestNodes(ASKED);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Receives an incoming NodeReplyMessage.
     **/
    public synchronized void receive(Message incoming, int comm) throws IOException {
        NodeReplyMessage mess = (NodeReplyMessage) incoming;
        Node origin = mess.getOrigin();

        // Insert origin node or refresh time last seen, update status
        space.insertNode(origin);
        nodes.put(origin, ASKED);
        transit.remove(new Integer(comm));

        addNodes(mess.getNodes());
        askNodesOrFinish();
    }

    /**
     * A node does not respond or a packet was lost.
     **/
    public synchronized void timeout(int comm) throws IOException {
        Integer c = new Integer(comm);
        Node node = (Node) transit.get(c);
        if (node == null)
            throw new UnknownMessageException("Incoming comm "+comm+" unknown");

        // Mark as unresponsive
        nodes.put(node, FAILED);
        space.removeNode(node);
        transit.remove(c);

        askNodesOrFinish();
    }

    /**
     * Asks some of the K closest nodes seen but not yet queried. Assures that no
     * more than CONCURRENCY messages are in transit at a time. If this limit is
     * reached, no nodes are queried. Instead, this method should be called again
     * when a reply is received or a timeout occurs. If all K closest nodes have
     * been asked and there are no messages in transit the algorithm is finished
     * and a thread waiting on this object is woken up.
     * Returns <code>true</code> if finished and <code>false</code> otherwise.
     **/
    private boolean askNodesOrFinish() throws IOException {
        if (transit.size() >= conf.CONCURRENCY) return false;

        // Find nodes not yet queried among the K closest seen
        List ask = filterClosestNodes(UNASKED);

        // Are none of the K nodes unasked and no messages in transit?
        if ((ask.size() == 0) && (transit.size() == 0)) {
            // At this point all the K closest nodes seen are marked ASKED
            // none can be AWAITING or UNASKED (FAILED are ignored).
            error = false;
            notify();
            return true;
        }

        // Sort nodes according to criteria
        Collections.sort(ask, cmp);

        // Send messages to the first nodes in the list and ensure that no more
        // than CONCURRENCY messages are in transit
        for (int i=0; (transit.size()<conf.CONCURRENCY) && (i<ask.size()); i++) {
            Node node = (Node) ask.get(i);
            int comm = server.send(lookupMessage, node.getInetAddress(),
                                   node.getPort(), this);
            nodes.put(node, AWAITING);
            transit.put(new Integer(comm), node);
        }
        return false;
    }

    /**
     * Returns the K nodes encountered closest to the target that have the
     * specified status.
     **/
    private List closestNodes(Byte status) {
        int remaining = conf.K;
        List matches = new ArrayList(conf.K);

        // Note that "nodes" are kept sorted according to closeness to target
        Iterator it = nodes.entrySet().iterator();
        while (it.hasNext() && (remaining > 0)) {
            Map.Entry entry = (Map.Entry) it.next();
            if (status.equals(entry.getValue())) {
                matches.add(entry.getKey());
                remaining--;
            }
        }
        return matches;
    }

    /**
     * Returns the subset of nodes with the specified status among the K
     * nodes encountered closest to the target that have status not FAILED.
     **/
    private List filterClosestNodes(Byte status) {
        int remaining = conf.K;
        List matches = new ArrayList(conf.K);

        // Note that "nodes" are kept sorted according to closeness to target
        Iterator it = nodes.entrySet().iterator();
        while (it.hasNext() && (remaining > 0)) {
            Map.Entry entry = (Map.Entry) it.next();
            Object value = entry.getValue();
            if (!FAILED.equals(value)) {
                remaining--;
                if (status.equals(value)) {
                    matches.add(entry.getKey());
                }
            }
        }
        return matches;
    }

    private void addNodes(List list) {
        for (int i = 0, max = list.size(); i < max; i++) {
            Object o = list.get(i);
            if (!nodes.containsKey(o)) nodes.put(o, UNASKED);
        }
    }
}

