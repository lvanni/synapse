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
	
	public static String DEFAULT_IP = "127.0.0.1";
	public static int DEFAULT_PORT = 8000;
	
	/**
	 * 
	 * @author lvanni
	 *
	 */
	public static enum NodeType {
		CHORD(0), 
		KAD(1), 
		SYNAPSE(2);
		
		private final int value;
	 
		private NodeType(int value) {
			this.value = value;
		}
	 
		public int getValue() {
			return this.value;
		}
	}
	
	/**
	 * 
	 * @author lvanni
	 *
	 */
	public static enum Command {
		CREATE(0), 
		PUT(1), 
		GET(2);
		
		private final int value;
		 
		private Command(int value) {
			this.value = value;
		}
	 
		public int getValue() {
			return this.value;
		}
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
	public String put(IDHT node, String key, String value);

	/**
	 * 
	 * @param key
	 * @return
	 */
	public String get(IDHT node, String key);

}
