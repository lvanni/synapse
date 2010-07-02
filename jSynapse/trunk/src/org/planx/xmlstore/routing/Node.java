package org.planx.xmlstore.routing;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import org.planx.xmlstore.routing.messaging.Streamable;

/**
 * Represents a node and contains information about the IP address,
 * UDP port, and ID of the node.
 **/
public class Node implements Streamable {
    public static final Comparator LASTSEEN_COMPARATOR = new LastSeenComparator();
    public static final Comparator FIRSTSEEN_COMPARATOR = new FirstSeenComparator();

    private InetAddress ip;
    private int port;
    private Identifier id;
    private long lastSeen;
    private long firstSeen;
    private int failCount = 0;

    /**
     * Constructs a node with the specified IP address, UDP port, and identifier.
     * This constructor should be used for foreign nodes.
     **/
    public Node(InetAddress ip, int port, Identifier id) {
        this.ip = ip;
        this.port = port;
        this.id = id;
        firstSeen = System.currentTimeMillis();
        seenNow();
    }

    /**
     * Constructs a node by reading the state from a DataInput.
     **/
    public Node(DataInput in) throws IOException {
        fromStream(in);
    }

    public void fromStream(DataInput in) throws IOException {
        id = new Identifier(in);
        byte[] a = new byte[4];
        in.readFully(a);
        ip = InetAddress.getByAddress(a);
        port = ((int) in.readShort()) & 0xFFFF;
        firstSeen = System.currentTimeMillis();
        seenNow();
    }

    public void toStream(DataOutput out) throws IOException {
        id.toStream(out);
        byte[] a = ip.getAddress();
        if (a.length != 4) {
            throw new RuntimeException("Expected InetAddress of 4 bytes, got "+a.length);
        }
        out.write(a);
        out.writeShort((short) port);
    }

    /**
     * Update time last seen for this node and reset fail count.
     **/
    public void seenNow() {
        lastSeen = System.currentTimeMillis();
        failCount = 0;
    }

    /**
     * Returns the time this node was first seen.
     **/
    public long firstSeen() {
        return firstSeen;
    }

    /**
     * Returns the time this node was last seen.
     **/
    public long lastSeen() {
        return lastSeen;
    }

    /**
     * Returns the IP address of this node.
     **/
    public InetAddress getInetAddress() {
        return ip;
    }

    /**
     * Returns the UDP port of this node.
     **/
    public int getPort() {
        return port;
    }

    /**
     * Returns the identifier of this node.
     **/
    public Identifier getId() {
        return id;
    }

    /**
     * Increments the failure counter and returns the new value.
     **/
    public int incFailCount() {
        return ++failCount;
    }

    /**
     * Returns <code>true</code> if <code>o</code> is a <code>Node</code>
     * and has the same identifier as this. Note that IP address and port
     * are ignored in this comparison.
     **/
    public boolean equals(Object o) {
        if (o instanceof Node) {
            return id.equals(((Node) o).id);
        }
        return false;
    }

    public int hashCode() {
        return id.hashCode();
    }

    public String toString() {
        return "Node[ip="+ip.getHostAddress()+",port="+port+",id="+id+"]";
    }

    /**
     * Sorts the nodes in the specified list in order of increasing distance
     * to the specified identifier.
     **/
    public static Node[] sort(Collection nodes, Identifier rel) {
        Node[] sorted = new Node[nodes.size()];
        sorted = (Node[]) nodes.toArray(sorted);
        Arrays.sort(sorted, new Node.DistanceComparator(rel));
        return sorted;
    }


    /**
     * A DistanceComparator is capable of comparing Node objects according to
     * closeness to a predetermined identifier using the XOR metric.
     **/
    public static class DistanceComparator implements Comparator {
        private BigInteger relval;

        /**
         * The identifier relative to which the distance should be measured.
         **/
        public DistanceComparator(Identifier relId) {
            relval = relId.value();
        }

        /**
         * Compare two objects which must both be of type <code>Node</code>
         * and determine which is closest to the identifier specified in the
         * constructor.
         **/
        public int compare(Object o1, Object o2) {
            Node n1 = (Node) o1;
            Node n2 = (Node) o2;
            BigInteger distance1 = relval.xor(n1.id.value());
            BigInteger distance2 = relval.xor(n2.id.value());
            return distance1.compareTo(distance2);
        }
    }

    /**
     * A LastSeenComparator is capable of comparing Node objects according to
     * time last seen.
     **/
    public static class LastSeenComparator implements Comparator {
        /**
         * Compare two objects which must both be of type <code>Node</code>
         * and determine which is seen last. If <code>o1</code> is seen more
         * recently than <code>o2</code> a positive integer is returned, etc.
         **/
        public int compare(Object o1, Object o2) {
            Node n1 = (Node) o1;
            Node n2 = (Node) o2;
            return (int) (n1.lastSeen - n2.lastSeen);
        }
    }

    /**
     * A FirstSeenComparator is capable of comparing Node objects according to
     * time first seen.
     **/
    public static class FirstSeenComparator implements Comparator {
        /**
         * Compare two objects which must both be of type <code>Node</code>
         * and determine which is seen first. If <code>o1</code> is seen before
         * <code>o2</code> a negative integer is returned, etc.
         **/
        public int compare(Object o1, Object o2) {
            Node n1 = (Node) o1;
            Node n2 = (Node) o2;
            return (int) (n1.firstSeen - n2.firstSeen);
        }
    }
}
