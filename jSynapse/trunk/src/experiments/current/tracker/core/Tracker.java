package experiments.current.tracker.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.protocols.p2p.Node;
import core.protocols.transport.IRequestHandler;
import core.protocols.transport.ITransport;
import core.protocols.transport.socket.request.RequestHandler;
import core.protocols.transport.socket.server.SocketImpl;

public class Tracker implements ITracker, IRequestHandler {

	/** peerSet<networkID, List<Node>> */
	private Map<String, List<NodeInfo>> peerSet;

	private ITransport transport;
	
	private List<Invitation> invitations = new ArrayList<Invitation>();

	public Tracker(int port) {
		transport = new SocketImpl(port, 10, RequestHandler.class.getName(),
				10, 1, 50, this);
		((SocketImpl) transport).launchServer();
		peerSet = new HashMap<String, List<NodeInfo>>();
	}

	// //////////////////////////// //
	// CRUD NODE //
	// //////////////////////////// //
	public void putNode(String networkID, NodeInfo node) {
		List<NodeInfo> nodes = null;
		if ((nodes = peerSet.get(networkID)) != null) {
			nodes.add(node);
		} else {
			nodes = new ArrayList<NodeInfo>();
			nodes.add(node);
			peerSet.put(networkID, nodes);
		}
	}

	public String getJoinEntry(String networkID) {
		List<NodeInfo> nodes = peerSet.get(networkID);
		return nodes != null && nodes.size() != 0 ? nodes.get(0).getNode().toString()
				: "null";
	}

	// /////////////////////////////////////////// //
	// TRANSPORT //
	// /////////////////////////////////////////// //
	public String forward(String message, Node destination) {
		return transport.sendRequest(message, destination);
	}

	public String handleRequest(String code) {
		String[] args = code.split(",");
		String result = "";
		int f = Integer.parseInt(args[0]);
		switch (f) {
		case ADDNODE:
			Node node = new Node(args[2], Integer.parseInt(args[3]),
					Integer.parseInt(args[4]));
			putNode(args[1], new NodeInfo(node, Integer.parseInt(args[5])));
			break;
		case GETCONNECTION:
			result = getJoinEntry(args[1]);
			break;
		case REMOVENODE:
			node = new Node(args[2], Integer.parseInt(args[3]), Integer
					.parseInt(args[4]));
			peerSet.get(args[1]).remove(new NodeInfo(node));
			break;
		case JOIN:
			System.out.println("receive a join");
			result = "null";
			Invitation toRemove = null;
			for(Invitation i : invitations){
				if(i.getAccessPass().equals(args[1])){
					result = i.getNetworkID() + "," + getJoinEntry(i.getNetworkID());
					toRemove = i;
					break;
				}
			}
			if(toRemove != null)
				invitations.remove(toRemove);
			break;
		default:
			break;
		}
		return result;
	}

	public void kill() {
		System.err.println("kill request do nothing...");
	}

	// //////////////////////////// //
	// GETTER AND SETTER //
	// //////////////////////////// //
	public Map<String, List<NodeInfo>> getPeerSet() {
		return peerSet;
	}

	public void setPeerSet(Map<String, List<NodeInfo>> peerSet) {
		this.peerSet = peerSet;
	}

	public ITransport getTransport() {
		return transport;
	}

	public void setTransport(ITransport transport) {
		this.transport = transport;
	}
	
	public synchronized void addInvitation(String networkID, String accessPass){
		invitations.add(new Invitation(networkID, accessPass));
	}

	public List<Invitation> getInvitations() {
		return invitations;
	}
}
