package simulation;

import core.protocols.p2p.Node;
import core.protocols.transport.IRequestHandler;

/**
 * This interface define the Simulator
 * 
 * @author laurent.vanni@sophia.inria.fr - logNet team 2010 - INRIA
 *         Sophia-Antipolis - France
 * 
 */
public interface ISimulator {
	
	public static int DEFAULT_PORT = 8000;
	
	public IRequestHandler getReceiver(Node node) ;

}
