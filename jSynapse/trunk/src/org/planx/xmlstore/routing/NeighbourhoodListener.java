package org.planx.xmlstore.routing;

/**
 * Listens for newly arrived nodes in the neighbourhood of the local node.
 * The neighbourhood is defined as the smallest subtree around the local node with
 * at least <i>K</i> nodes.
 **/
public interface NeighbourhoodListener {
    /**
     * A new node has been inserted in a bucket in the neighbourhood.
     * This does <i>not</i> include the local node.
     **/
    public void nodeArrived(Node node);
}
