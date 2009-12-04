package core.protocols.transport;

import java.io.Serializable;

import core.protocols.p2p.Node;

public interface ITransport {
	
	public String forward(String message, Node destination);
	public int getPort();
	
}
