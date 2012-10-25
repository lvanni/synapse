package experiment.networking.current.node.synapse;

import core.protocol.p2p.Node;
import core.protocol.p2p.synapse.AbstractSynapse;
import core.protocol.transport.ITransport;

/**
 * This is an implementation of a synapse node
 * 
 * @author laurent.vanni@sophia.inria.fr - logNet team 2010 - INRIA
 *         Sophia-Antipolis - France
 * 
 */
public class Synapse extends AbstractSynapse {

	/**
	 * Default constructor
	 * 
	 * @param ip
	 * @param port
	 */
	public Synapse(String ip, int port) {
		this(ip, port, null);
	}
	
	/**
	 * Default constructor
	 * 
	 * @param ip
	 * @param port
	 */
	public Synapse(String ip, int port, ITransport transport) {
		super(ip, port, "synapse", transport);
	}
	
	/**
	 * Default constructor
	 * 
	 * @param ip
	 * @param port
	 */
	public Synapse(Node nodeInfo, ITransport transport) {
		super(nodeInfo, transport);
	}
}
