package core.protocol.transport.local;

import core.protocol.p2p.Node;
import core.protocol.transport.IRequestHandler;
import core.protocol.transport.ITransport;
import experiment.simulation.ISynapseSim;
import experiment.simulation.SynapseSim;

public class LocalImpl implements ITransport {
	
	private ISynapseSim synapseSim;
	
	public LocalImpl() {
		this.synapseSim = SynapseSim.getInstance();
	}

	public int getPort() {
		return SynapseSim.DEFAULT_PORT;
	}

	/**
	 * Send a message to the Node destination
	 */
	public String sendRequest(String message, Node node) {
		IRequestHandler requestHandler = synapseSim.getReceiver(node);
		if(requestHandler != null){
			return requestHandler.handleRequest(message);
		} else {
			return "";
		}
	}

	/**
	 * Stop the transport server
	 */
	public void stopServer() {
		// nothing to do with local implementation
	}

}
