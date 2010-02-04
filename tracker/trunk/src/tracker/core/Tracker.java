package tracker.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tracker.api.TrackerAPI;

import core.protocols.p2p.Node;
import core.protocols.transport.IRequestHandler;
import core.protocols.transport.ITransport;
import core.protocols.transport.socket.request.RequestHandler;
import core.protocols.transport.socket.server.SocketImpl;

/**
 * The MyMed Tracker core
 * @author lvanni, 2010 LogNet team - INRIA - Sophia-Antipolis - France
 *
 */
public class Tracker implements TrackerAPI, IRequestHandler {

	/* ************** ATTRIBUTES ************** */
	/** peerSet<networkID, List<Node>> */
	private Map<String, List<Node>> peerSet;
	/** The transport level */
	private ITransport transport;
	/** The list of the available invitation from a network to an other*/
	private List<Invitation> invitations;
	/** The tracker singleton */
	private static Tracker tracker = new Tracker();
	/** The status of the tracker */
	public static boolean started = false;
	
	/* ************** DEFAULT CONSTRUCTOR ************** */	
	/**
	 * Default constructor
	 */
	private Tracker() {
		transport = new SocketImpl(TRACKER_PORT, 10, RequestHandler.class.getName(),
				10, 1, 50, this);
		((SocketImpl) transport).launchServer();
		peerSet = new HashMap<String, List<Node>>();
		invitations = new ArrayList<Invitation>();
	}
	
	/**
	 * Get the tracker instance
	 * @return Tracker, the singleton instance
	 */
	public static Tracker getTracker(){
		started = true;
		if(tracker != null){
			return tracker;
		} else {
			return tracker = new Tracker();
		}
	}

	/* ************** PUBLIC METHODS ************** */
	/**
	 * Allow to add a node to the peerSet
	 * @param networkID
	 * @param node
	 */
	public void putNode(String networkID, Node node) {
		List<Node> nodes = null;
		if ((nodes = peerSet.get(networkID)) != null) {
			nodes.add(node);
		} else {
			nodes = new ArrayList<Node>();
			nodes.add(node);
			peerSet.put(networkID, nodes);
		}
	}

	/**
	 * Return the an entry point of an Network 
	 * @param networkID
	 * @return Node.toString()
	 */
	public String getJoinEntry(String networkID) {
		List<Node> nodes = peerSet.get(networkID);
		return nodes != null && nodes.size() != 0 ? nodes.get(0).toString()
				: "null";
	}

	/**
	 * Remove the node from the tracker peerSet
	 * @param networkID
	 * @param node
	 */
	public void removeNode(String networkID, String node) {
		List<Node> nodes = peerSet.get(networkID);
		nodes.remove(node);
		if (nodes.size() == 0) {
			peerSet.remove(networkID);
		}
	}
	
	/**
	 * Add an invitation for a network
	 * @param networkID
	 * @param accessPass
	 */
	public synchronized void addInvitation(String networkID, String accessPass){
		invitations.add(new Invitation(networkID, accessPass));
	}

	/**
	 * @param code, the request code to handle
	 */
	public String handleRequest(String code) {
		String[] args = code.split(",");
		String result = "";
		int f = Integer.parseInt(args[0]);
		switch (f) {
		case ADDNODE:
			putNode(args[1], new Node(args[2], Integer.parseInt(args[3]),
					Integer.parseInt(args[4])));
			break;
		case GETCONNECTION:
			result = getJoinEntry(args[1]);
			break;
		case REMOVENODE:
			Node n = new Node(args[2], Integer.parseInt(args[3]), Integer
					.parseInt(args[4]));
			peerSet.get(args[1]).remove(n);
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
		started = false;
		peerSet.clear();
		invitations.clear();
		transport.stopServer();
		tracker = null; 
	}
	

	/* ************** GETTERS ************** */
	/**
	 * @return List<Invitation>, the list of the invitations known
	 */
	public List<Invitation> getInvitations() {
		return invitations;
	}
	
	/**
	 * @return String, The port number used by the tracker to listen
	 */
	public String getPort(){
		return transport.getPort() + "";
	}
	
	/**
	 * @return Map<String, List<Node>>, The peerSet managed by the tracker
	 */
	public Map<String, List<Node>> getPeerSet(){
		return peerSet;
	}
}
