package experiment.networking.current.tracker.core;

import core.protocol.p2p.Node;

public class NodeInfo {
	
	private Node node;
	private int port = 0;
	
	public NodeInfo(Node node){
		this.node = node;
	}
	
	public NodeInfo(Node node, int port) {
		this.node = node;
		this.port = port;
	}
	
	public String toString(){
		return node.getIp() + ":" + node.getPort() + ":" + port;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Node) {
			Node n = (Node) obj;
			return node.equals(n);
		}
		return false;
	}

	public Node getNode() {
		return node;
	}
}
