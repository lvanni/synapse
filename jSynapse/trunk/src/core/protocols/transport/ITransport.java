package core.protocols.transport;

import core.protocols.p2p.Node;

/**
 * This interface represent the transport layer
 * 
 * @author laurent.vanni@sophia.inria.fr - logNet team 2010 - INRIA
 *         Sophia-Antipolis - France
 * 
 */
public interface ITransport {

	/**
	 * Send a request the destination node
	 * 
	 * @param message
	 * @param destination
	 * @return the response to this request
	 */
	public String sendRequest(String message, Node destination);

	/**
	 * 
	 * @return the port number used by the transport layer
	 */
	public int getPort();

	/**
	 * stop the server of the transport layer
	 */
	public void stopServer();
}
