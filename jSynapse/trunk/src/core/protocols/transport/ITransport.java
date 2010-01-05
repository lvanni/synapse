package core.protocols.transport;

import core.protocols.p2p.Node;

public interface ITransport {
	public String sendRequest(String message, Node destination);
	public int getPort();
}
