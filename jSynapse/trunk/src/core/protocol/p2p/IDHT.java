package core.protocol.p2p;

import core.protocol.transport.IRequestHandler;
import core.protocol.transport.ITransport;

/**
 * This interface represent a DHT peer
 * 
 * @author laurent.vanni@sophia.inria.fr - logNet team 2010 - INRIA
 *         Sophia-Antipolis - France
 * 
 */
public interface IDHT extends IRequestHandler {
	/**
	 * @return The identifier of the network
	 */
	public String getIdentifier();

	/**
	 * @param key
	 *            the key to hash
	 * @return the identifier associated to the key in the network
	 */
	public int keyToH(String key);

	/**
	 * @return The transport layer of the network
	 */
	public ITransport getTransport();

	/**
	 * @return a instance of the node
	 */
	public Node getThisNode();

	/**
	 * Put<key,value> : the Common command of a DHT
	 * 
	 * @param key
	 * @param value
	 */
	public void put(String key, String value);

	/**
	 * Get<key> : the Common command of a DHT
	 * 
	 * @param key
	 * @return the value associated to the key
	 */
	public String get(String key);

	/**
	 * Join a chord with an entry represented by his the host address and port
	 * number
	 * 
	 * @param host
	 * @param port
	 */
	public void join(String host, int port);

	/**
	 * "Fairplay" kill of the node
	 */
	public void kill();
}