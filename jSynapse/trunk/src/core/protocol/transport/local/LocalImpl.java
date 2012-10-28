package core.protocol.transport.local;

import core.protocol.p2p.Node;
import core.protocol.transport.ITransport;
import experiment.simulation.ISynapseSim;
import experiment.simulation.SynapseSim;

public class LocalImpl implements ITransport {
	
	private ISynapseSim simulator;
	private int port;
	
	public LocalImpl() {
		this(SynapseSim.DEFAULT_PORT);
	}

	public LocalImpl(int port) {
		this.simulator = SynapseSim.getInstance();
		this.port = port;
	}
	
	public int getPort() {
		return this.port;
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
