package core.protocol.transport.local;

import core.protocol.p2p.Node;
import core.protocol.transport.ITransport;
import experiment.simulation.ISimulator;
import experiment.simulation.Simulator;

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
