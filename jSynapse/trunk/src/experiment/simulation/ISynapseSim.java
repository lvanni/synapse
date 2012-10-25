package experiment.simulation;

import core.protocol.p2p.IDHT;
import core.protocol.p2p.Node;
import core.protocol.transport.IRequestHandler;
import experiment.networking.current.node.synapse.Synapse;

/**
 * This interface define the Simulator
 * 
 * @author laurent.vanni@sophia.inria.fr - logNet team 2010 - INRIA
 *         Sophia-Antipolis - France
 * 
 */
public interface ISynapseSim {
	
	public static int DEFAULT_PORT = 8000;
	
	/**
	 * 
	 * @author lvanni
	 *
	 */
	public static enum NodeType {
		CHORD, KAD, SYNAPSE
	}
	
	/**
	 * 
	 * @author lvanni
	 *
	 */
	public static enum Command {
		CREATE, PUT, GET;
	}
	
	/**
	 * 
	 * @param node
	 * @return
	 */
	public IRequestHandler getReceiver(Node node) ;
	
	
	/**
	 * 
	 * @param nodeType
	 * @param networkId
	 * @return
	 */
	public IDHT createNode(NodeType nodeType, String networkId);
	
	/**
	 * 
	 * @param nodeType
	 * @param networkId
	 * @param synapse
	 * @return
	 */
	public IDHT createNode(NodeType nodeType, String networkId, Synapse synapse);
	
	/**
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public int put(String key, String value);

	/**
	 * 
	 * @param key
	 * @return
	 */
	public int get(String key);

}
