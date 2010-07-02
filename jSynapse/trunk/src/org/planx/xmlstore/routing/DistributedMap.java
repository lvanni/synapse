package org.planx.xmlstore.routing;

import java.io.IOException;
import java.io.Serializable;

/**
 * Maps keys to values in a distributed setting using <code>Identifier</code>s
 * as keys. Methods throw IOException if a network error occurs and
 * RoutingException if a routing specific error occurs in the overlay network.
 * Note that RoutingException is a subclass of IOException so if the
 * distinction is not need, IOException is the only checked exception that
 * needs to be handled.
 **/
public interface DistributedMap {
    /**
     * Closes the map and releases all ressources.
     * Any subsequent calls to methods are invalid.
     **/
    public void close() throws IOException;

    /**
     * Returns <code>true</code> if the map contains the specified key and
     * <code>false</code> otherwise.
     **/
    public boolean contains(Identifier key) throws IOException, RoutingException;

    /**
     * Returns the value associated with the specified key or <code>null</code>
     * if does not exist.
     **/
    public Serializable get(Identifier key) throws IOException, RoutingException;

    /**
     * Associates the specified value with the specified key.
     **/
    public void put(Identifier key, Serializable value) throws IOException, RoutingException;

    /**
     * Removes the mapping with the specified key from this map.
     **/
    public void remove(Identifier key) throws IOException, RoutingException;
}
