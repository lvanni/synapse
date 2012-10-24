package core.protocols.transport.local;

import simulation.ISimulator;
import simulation.Simulator;
import core.protocols.p2p.Node;
import core.protocols.transport.ITransport;

public class LocalImpl implements ITransport {
	
	private ISimulator simulator;
	
	public LocalImpl() {
		this.simulator = Simulator.getInstance();
	}

	public int getPort() {
		return Simulator.DEFAULT_PORT;
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
