package org.planx.xmlstore.routing;

/**
 * A set of Kademlia configuration parameters. Default values are
 * supplied and can be changed by the application as necessary.
 **/
public class Configuration {
    /**
     * Interval in milliseconds between execution of RestoreOperations.
     **/
    public long RESTORE_INTERVAL = 60*60*1000;

    /**
     * If no reply received from a node in this period (in milliseconds)
     * consider the node unresponsive.
     **/
    public long RESPONSE_TIMEOUT = 3000;

    /**
     * Maximum number of milliseconds for performing an operation.
     **/
    public long OPERATION_TIMEOUT = 10000;

    /**
     * Maximum number of concurrent messages in transit.
     **/
    public int CONCURRENCY = 2;

    /**
     * Log base exponent.
     **/
    public int B = 2;

    /**
     * Bucket size.
     **/
    public int K = 3;

    /**
     * Size of replacement cache.
     **/
    public int RCSIZE = 3;

    /**
     * Number of times a node can be marked as stale before it is actually removed.
     **/
    public int STALE = 1;
}
