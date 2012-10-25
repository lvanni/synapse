package experiment.networking.current.node.synapse;

import core.protocol.p2p.synapse.AbstractSynapse;

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
		super(ip, port, "synapse");
	}
}
