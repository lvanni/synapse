package core.protocol.transport.local;

import core.protocol.p2p.Node;
import core.protocol.transport.ITransport;
import experiment.simulation.ISynapseSim;
import experiment.simulation.SynapseSim;

public class LocalImpl implements ITransport {
	
	private ISynapseSim simulator;
	
	public LocalImpl() {
		this.simulator = SynapseSim.getInstance();
	}

	public int getPort() {
		return SynapseSim.DEFAULT_PORT;
	}

	/**
	 * Send a message to the Node destination
	 */
	public String sendRequest(String message, Node node) {
		return simulator.getReceiver(node).handleRequest(message);
	}

	/**
	 * Stop the transport server
	 */
	public void stopServer() {
		// nothing to do with local implementation
	}

}
