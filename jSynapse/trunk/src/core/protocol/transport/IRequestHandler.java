package core.protocol.transport;

/**
 * This class represent a request handler
 * 
 * @author laurent.vanni@sophia.inria.fr - logNet team 2010 - INRIA
 *         Sophia-Antipolis - France
 * 
 */
public interface IRequestHandler {
	
	/**
	 * Handle the request represented by the code
	 * @param code
	 * @return the response to this request
	 */
	public String handleRequest(String code);
	
	/**
	 * @see core.protocol.p2p.chord.IChord#kill()
	 */
	public void kill();
}
