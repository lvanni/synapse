package core.protocol.p2p.synapse;

/**
 * This interface represent a synapse and define the synapse API
 * 
 * @author laurent.vanni@sophia.inria.fr - logNet team 2010 - INRIA
 *         Sophia-Antipolis - France
 * 
 */
public interface ISynapse {

	// THE SYNAPSE API
	public final static int PUTCleanKey = 10;
	public final static int GETCleanKey = 11;

	public final static int INITCacheTable = 12;
	public final static int CacheTableExist = 13;
	public final static int REMOVECacheTable = 14;

	public final static int ADDCacheValue = 30;
	public final static int GETCacheValue = 31;

	/**
	 * ask to invite a new peer in the synapse network
	 */
	public void invite();

	/**
	 * Join the synapse peer to a control network
	 * 
	 * @param host
	 *            the address of an entry of the control network
	 * @param port
	 *            the port number of an entry of the control network
	 */
	public void join(String host, int port);

}
