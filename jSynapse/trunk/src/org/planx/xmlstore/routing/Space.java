package org.planx.xmlstore.routing;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Represents a space (or subspace) containing nodes. The spaces are organized
 * in a binary tree where only the leaf spaces have buckets containing nodes.
 * The internal spaces each contain exactly two child spaces (never one).
 * <p>
 * All identifiers are transformed from the real identifier space to the
 * "distance from the local node"-space using the XOR metric. In other words,
 * the local node has id 0 and the ids increase as the distance from the local
 * node increases. Note that this is a loss-free transformation in that the
 * original id can be recovered by the XOR of the id in this space and the
 * id of the local node in the real space.
 * <p>
 * Space is synchronized.
 **/
public class Space {
    private static Random random = new Random();

    /**
     * The id of the local node.
     **/
    private final Identifier localId;

    /**
     * The parent of this space in the space tree.
     **/
    private final Space parent;

    /**
     * The subspace which is closest to the local node, that is, a 0-branch.
     * Always null for leaf spaces.
     * If null 'farChild' is also null.
     **/
    private Space closeChild = null;

    /**
     * The subspace which is farthest away from the local node, that is, a 1-branch.
     * Always null for leaf spaces.
     * If null 'closeChild' is also null.
     **/
    private Space farChild = null;

    /**
     * The depth in the tree of this space. Top level is 0.
     * Alternatively, the number of
     * most significant bits that indentifiers in this space have in common.
     * This also indicates the size of the range that this space covers:
     * rangeSize = 2**(IDSIZE-depth)
     **/
    private final int depth;

    /**
     * Consider the complete binary tree where the complete identifier space
     * at this depth is divided into 2**depth equally sized subspaces.
     * The <code>startIndex</code> is then the number of spaces between this
     * space and 00..00 (the local node).
     * Thus, this space is responsible for identifiers in the range:
     * [startIndex * rangeSize; startIndex * (rangeSize+1))
     **/
    private final BigInteger startIndex;

    /**
     * The nodes contained in this space. Only not null for leaf spaces.
     * The bucket will contain at most <i>K</i> nodes. The map consists of
     * Identifier to Node mappings.
     **/
    private Map bucket;
    private long lastLookup = 0;

    /**
     * Nodes that can replace failed nodes in a full bucket.
     * This will only contain nodes if <code>nodes</code> contains exactly <i>K</i>
     * nodes. The replacement cache will at most contain <i>RCSIZE</i> nodes.
     * The map consists of Identifier to Node mappings.
     **/
    private Map cache;

    private Configuration conf;
    private NeighbourhoodListener listener;

    ///////////////////////////////////////////////////////////////////////////
    /////////////////////////////// Constructors //////////////////////////////
    ///////////////////////////////////////////////////////////////////////////


    /**
     * Construct an empty top level space that covers the complete range of identifiers.
     * The local node is inserted into the space and this will call the listener.
     **/
    public Space(Node local, Configuration conf) {
        this(local, conf, null);
    }

    /**
     * Construct an empty top level space that covers the complete range of identifiers.
     * The local node is inserted into the space and this will call the listener.
     *
     * @param local    The local node
     * @param conf     Configuration parameters
     * @param listener A listener for arriving nodes in the neighbourhood of the local node
     **/
    public Space(Node local, Configuration conf, NeighbourhoodListener listener) {
        this.conf = conf;
        parent = null;
        localId = local.getId();
        this.listener = listener;
        depth = 0;
        startIndex = BigInteger.ZERO;
        bucket = new HashMap();
        cache = new HashMap();
        insertNode(local);
    }

    /**
     * Construct a new space with the specified parent and depth.
     **/
    private Space(Space parent, int depth, Identifier localId, BigInteger startIndex,
                                  Configuration conf, NeighbourhoodListener listener) {
        this.parent = parent;
        this.depth = depth;
        this.localId = localId;
        this.startIndex = startIndex;
        this.conf = conf;
        this.listener = listener;
        bucket = new HashMap();
        cache = new HashMap();
    }

    ///////////////////////////////////////////////////////////////////////////
    //// Public Interface Methods: getClosestNodes, removeNode, insertNode ////
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Returns the <i>K</i> nodes known by this space closest to the specified id.
     * The order of the nodes is not specified.
     **/
    public synchronized List getClosestNodes(Identifier id) {
        List foundNodes = new ArrayList(conf.K);
        gatherNodes(id, id.value().xor(localId.value()), foundNodes);
        return foundNodes;
    }

    /**
     * @param id         Identifier for which the closest K nodes should be found
     * @param distance   Distance from <code>id</code> to local node
     * @param foundNodes Nodes found so far (updated during execution).
     *                   This will contain the result.
     **/
    private void gatherNodes(Identifier id, BigInteger distance, List foundNodes) {
        if (bucket == null) {

            // This is an internal space, visit closest subspace first
            closestChild(distance).gatherNodes(id, distance, foundNodes);

            // If less than K nodes found so far also visit far away subtree
            if (foundNodes.size() < conf.K) {
                farthestChild(distance).gatherNodes(id, distance, foundNodes);
            }
        } else {
            // This space is a leaf and contains some or all of the K closest nodes
            // Add all nodes if this space contains fewer (or equal)
            // nodes than the number of nodes remaining

            if ((conf.K - foundNodes.size()) >= bucket.size()) {
                foundNodes.addAll(bucket.values());
            } else {

                // Only some of the nodes should be added, find the closest

                Object[] sorted = new Object[bucket.size()];
                sorted = bucket.values().toArray(sorted);
                Arrays.sort(sorted, new Node.DistanceComparator(id));

                // Add enough of the closest nodes to fill up list
                for (int i=0, max=(conf.K-foundNodes.size()); i<max; i++) {
                    foundNodes.add(sorted[i]);
                }
            }
        }
    }

    /**
     * Marks a node as unresponsive and removes it from the tree of spaces rooted
     * at this space if it has been marked in this way STALE times. Returns
     * <code>true</code> if the node was actually removed.
     **/
    public synchronized boolean removeNode(Node node) {
        BigInteger distance = localId.value().xor(node.getId().value());
        return removeNode(node, distance);
    }

    /**
     * Merely skip computing the distance from the node to local node every time.
     **/
    private boolean removeNode(Node node, BigInteger distance) {
        if (bucket == null) {
            return closestChild(distance).removeNode(node, distance);
        } else {
            boolean res = false;
            // Get node from bucket or cache
            Identifier id = node.getId();
            Node fn = (Node) bucket.get(id);
            if (fn == null) fn = (Node) cache.get(id);

            // Check if stale and should be removed
            if ((fn != null) && (fn.incFailCount() >= conf.STALE)) {
                // If in bucket, move a node from replacement cache to bucket, call listener
                if ((bucket.remove(id) != null) && (cache.size() > 0)) {
                    Node mostRecent = (Node) Collections.max(cache.values(),
                                                             Node.LASTSEEN_COMPARATOR);
                    cache.remove(mostRecent.getId());
                    bucket.put(mostRecent.getId(), mostRecent);
                    checkCallListener(mostRecent);
                }
                cache.remove(id);
                res = true;
            } else {
                res = (fn == null);
            }
            compactify();
            return res;
        }
    }

    /**
     * Inserts the specified node into this space. Depending on the distance from
     * the local node the new node is either dropped or inserted.
     * The node is inserted if the bucket to which it should belong is not full
     * (i.e. contains less than <i>K</i> nodes) <i>or</i> in some cases a full
     * bucket is split in order to make room for the node. A bucket is split if
     * either the space contains the local node <i>or</i> the split is needed
     * because of an unbalanced tree.
     **/
    public synchronized void insertNode(Node newNode) {
        BigInteger distance = localId.value().xor(newNode.getId().value());
        insertNode(newNode, distance);
    }

    /**
     * Merely skip computing the distance from newNode to local node every time.
     **/
    private void insertNode(Node newNode, BigInteger distance) {
        if (bucket == null) {
            // This is an internal space, choose appropriate subspace
            closestChild(distance).insertNode(newNode, distance);
        } else {
            // Bucket found, check if node is already present
            Node oldNode = (Node) bucket.get(newNode.getId());
            if (oldNode != null) {
                // Node is known, update time last seen
                oldNode.seenNow();
            } else {
                // Node is unknown, possibly insert node
                if (bucket.size() < conf.K) {
                    // Still room in bucket, just insert node, call listener
                    bucket.put(newNode.getId(), newNode);
                    checkCallListener(newNode);
                } else {
                    // Bucket is full, split or insert in replacement cache
                    if (isSplittable()) {
                        // Split space into two subspaces of equal size and
                        // insert node again
                        split();
                        insertNode(newNode, distance);
                    } else {
                        // Insert node in replacement cache
                        oldNode = (Node) cache.get(newNode.getId());
                        if (oldNode != null) {
                            // Node already in cache, update time last seen
                            oldNode.seenNow();
                        } else if (cache.size() < conf.RCSIZE) {
                            // Still room in replacement cache, just insert node
                            cache.put(newNode.getId(), newNode);
                        } else {
                            // Replacement cache full, replace least recently seen node
                            Node leastSeen = (Node) Collections.min(
                                cache.values(), Node.LASTSEEN_COMPARATOR);
                            cache.remove(leastSeen.getId());
                            cache.put(newNode.getId(), newNode);
                        }
                    }
                }
            }
            // Actually overkill to compactify everytime
            compactify();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    ////////////////////// Public Miscellaneous Get Methods ///////////////////
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Returns a list containing random Identifiers in bucket ranges that needs
     * to be refreshed. Currently all "buckets" are refreshed. This is "buckets"
     * in the sense of possible buckets in a fully populated tree of spaces.
     * Thus, there are IDSIZE buckets in this interpretation. The first bucket
     * containing only the local node is skipped.
     **/
    public synchronized List getRefreshList() {
        List refreshList = new ArrayList();
        for (int i=1; i<Identifier.IDSIZE; i++) {
            BigInteger start = BigInteger.ONE.shiftLeft(i);
            BigInteger rnd = new BigInteger(i, random);
            Identifier id = new Identifier(start.add(rnd).xor(localId.value()));
            refreshList.add(id);
        }
        return refreshList;
    }

    /**
     * Returns all nodes in the tree of spaces rooted at this space.
     * The nodes' order is not specified.
     **/
    public synchronized List getAll() {
        List nodes = new ArrayList();
        addAll(nodes);
        return nodes;
    }

    private void addAll(List nodes) {
        if (bucket != null) {
            nodes.addAll(bucket.values());
        } else {
            closeChild.addAll(nodes);
            farChild.addAll(nodes);
        }
    }

    /**
     * Returns all nodes in the neighbourhood of the local node. The neighbourhood
     * is the smallest subspace around the local node with at least <i>K</i> nodes.
     **/
    public synchronized List getNeighbourhood() {
        if (bucket == null) {
            return closeChild.getNeighbourhood();
        } else {
            List nodes = new ArrayList();
            nodes.addAll(bucket.values());
            addFarNodes(nodes);
            return nodes;
        }
    }

    private void addFarNodes(List nodes) {
        if ((parent != null) && (nodes.size() < conf.K)) {
            parent.farChild.addAll(nodes);
            parent.addFarNodes(nodes);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    ////////////////// Private Splitting and Merging Methods //////////////////
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Splits this space into two equally sized subspaces with regards to the
     * range covered by this space.
     **/
    private void split() {
        BigInteger closeIndex = startIndex.shiftLeft(1);
        closeChild = new Space(this, depth+1, localId, closeIndex, conf, listener);
        farChild = new Space(this, depth+1, localId, closeIndex.or(BigInteger.ONE),
                                                                    conf, listener);

        // For each node in the bucket and the replacement cache
        // assign it to the appropriate subspace

        assignNodes(bucket.values(), farChild.bucket, closeChild.bucket);
        assignNodes(cache.values(), farChild.cache, closeChild.cache);

        bucket = null;
        cache = null;
    }

    /**
     * For each node in the list <code>l</code> assign it to either the
     * left (far away) map or the right (close) map according to the
     * distance to local node.
     **/
    private void assignNodes(Collection l, Map left, Map right) {
        Iterator it = l.iterator();
        while (it.hasNext()) {
            Node node = (Node) it.next();
            BigInteger distance = localId.value().xor(node.getId().value());
            Map map = distance.testBit(Identifier.IDSIZE-1-depth) ? left : right;
            map.put(node.getId(), node);
        }
    }

    /**
     * If possible, compactifies the tree of spaces from here to the root.
     * For each space from this one up to the root it is tested if the topology has
     * changed so that two subspaces can be joined. If this is the case, the spaces
     * are joined.
     **/
    private void compactify() {
        if ((bucket == null) && (closeChild.bucket != null) &&
                (farChild.bucket != null) && !isSplittable()) {

            bucket = new HashMap();
            cache = new HashMap();

            // Order all nodes in child buckets by time first seen and
            // prefer old nodes, when bucket is full put remaining nodes
            // in replacement cache

            List tmp = new ArrayList(2*conf.K);
            tmp.addAll(closeChild.bucket.values());
            tmp.addAll(farChild.bucket.values());
            Collections.sort(tmp, Node.FIRSTSEEN_COMPARATOR);

            Iterator it = tmp.iterator();
            while (it.hasNext()) {
                Node node = (Node) it.next();
                if (bucket.size() < conf.K) {
                    bucket.put(node.getId(), node);
                } else if (cache.size() < conf.RCSIZE) {
                    cache.put(node.getId(), node);
                } else {
                    break;
                }
            }

            // Order all nodes in child replacement caches by time last seen
            // and prefer most recently seen nodes

            tmp = new ArrayList(2*conf.RCSIZE);
            tmp.addAll(closeChild.cache.values());
            tmp.addAll(farChild.cache.values());
            Collections.sort(tmp, Node.LASTSEEN_COMPARATOR);

            it = tmp.iterator();
            while (it.hasNext() && (cache.size() < conf.RCSIZE)) {
                Node node = (Node) it.next();
                cache.put(node.getId(), node);
            }

            closeChild = null;
            farChild = null;
        }

        if ((bucket != null) && (parent != null)) parent.compactify();
    }

    /**
     * Returns <code>true</code> if this space can be split into two
     * subspaces of equal size (in terms of identifier range).
     **/
    private boolean isSplittable() {
        if (startIndex.compareTo(BigInteger.ONE.shiftLeft(depth % conf.B)) < 0) {
            return true;
        } else {
            return isUnbalanced();
        }
    }

    /**
     * Returns <code>true</code> if the smallest subtree T containing both
     * this space and the space containing the local node is such that
     * there are less than <i>K</i> nodes in the subtree of T containing
     * the local node.
     **/
    private boolean isUnbalanced() {
        if ((bucket == null) && startIndex.equals(BigInteger.ZERO)) {
            return closeChild.nodeCount() < conf.K;
        } else if (parent != null) {
            return parent.isUnbalanced();
        } else {
            // Only happens for an unsplit top-level space
            return false;
        }
    }

    /**
     * Returns <code>true</code> if this space is a leaf space that contains
     * the local node.
     **/
    private boolean isHome() {
        return ((bucket != null) && startIndex.equals(BigInteger.ZERO));
    }

    ///////////////////////////////////////////////////////////////////////////
    /////////////////////////// Convenience Methods ///////////////////////////
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Returns the subspace of this space closest to the local node given
     * the specified distance to the local node.
     **/
    private Space closestChild(BigInteger distance) {
        return distance.testBit(Identifier.IDSIZE-1-depth) ? farChild : closeChild;
    }

    /**
     * Returns the subspace of this space farthest away from the local node given
     * the specified distance to the local node.
     **/
    private Space farthestChild(BigInteger distance) {
        return distance.testBit(Identifier.IDSIZE-1-depth) ? closeChild : farChild;
    }

    /**
     * Returns the number of nodes in the range covered by this space.
     **/
    public int nodeCount() {
        if (bucket != null) {
            return bucket.size();
        } else {
            return closeChild.nodeCount() + farChild.nodeCount();
        }
    }

    /**
     * Called when node is inserted in bucket. Checks if neighbourhood changed and
     * calls the listener if so.
     **/
    private void checkCallListener(Node node) {
        if ((listener != null) && (isHome() || isUnbalanced())) {
            listener.nodeArrived(node);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    /////////////////////// Methods for Testing Purposes //////////////////////
    ///////////////////////////////////////////////////////////////////////////

    /**
     * For debugging purposes, returns a string containing an XML document
     * representing the binary tree of spaces rooted at this space.
     **/
    public synchronized String toString() {
        StringBuffer sb = new StringBuffer();
        toString("", sb);
        return sb.toString();
    }

    private void toString(String prefix, StringBuffer sb) {
        if (sb.length() > 10000) {
            System.out.println("Probable endless loop detected, contents so far:");
            System.out.println(sb);
            System.exit(0);
        }

        if (bucket != null) {
            sb.append(prefix);
            sb.append("<space depth=\""+depth+"\" startIndex=\""+startIndex+"\">\n");

            sb.append(prefix);
            sb.append("  <bucket size=\""+Integer.toString(bucket.size())+"\">\n");
            Node[] ns = Node.sort(bucket.values(), localId);
            for (int i = 0; i < ns.length; i++) {
                sb.append(prefix);
                sb.append("    <node xor=\"");
                sb.append(Identifier.toBinary(ns[i].getId().value().xor(localId.value())));
                sb.append("\"/>\n");
            }
            sb.append(prefix);
            sb.append("  </bucket>\n");

            sb.append(prefix);
            sb.append("  <cache size=\""+Integer.toString(cache.size())+"\">\n");
            ns = Node.sort(cache.values(), localId);
            for (int i = 0; i < ns.length; i++) {
                sb.append(prefix);
                sb.append("    <node xor=\"");
                sb.append(Identifier.toBinary(ns[i].getId().value().xor(localId.value())));
                sb.append("\"/>\n");
            }
            sb.append(prefix);
            sb.append("  </cache>\n");

            sb.append(prefix);
            sb.append("</space>\n");
        } else {
            sb.append(prefix);
            sb.append("<space depth=\""+depth+"\" startIndex=\""+startIndex+"\">\n");
            closeChild.toString(prefix+"  ", sb);
            farChild.toString(prefix+"  ", sb);
            sb.append(prefix);
            sb.append("</space>\n");
        }
    }
}
