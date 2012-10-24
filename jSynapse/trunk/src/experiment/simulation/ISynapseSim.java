package experiment.simulation;

import core.protocol.p2p.Node;
import core.protocol.transport.IRequestHandler;

/**
 * This interface define the Simulator
 * 
 * @author laurent.vanni@sophia.inria.fr - logNet team 2010 - INRIA
 *         Sophia-Antipolis - France
 * 
 */
public interface ISynapseSim {
	
	public static int DEFAULT_PORT = 8000;
	
	public static enum NodeType {
		CHORD, KAD, SYNAPSE
	}
	
	public static enum Command {
		CREATE, PUT, GET;
	}
	
	public IRequestHandler getReceiver(Node node) ;
	
	public int createNode(NodeType nodeType, String networkId);
	
	public int put(String key, String value);
	
	public int get(String key);

}
