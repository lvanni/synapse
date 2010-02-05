package core.protocols.p2p.chord;

import core.protocols.p2p.IOverlay;
import core.protocols.p2p.Node;

public interface IChord extends IOverlay{
	
	/** API OF A CHORD NODE */
	public final static int GETPRED  = 0;
	public final static int FINDSUCC = 1;
	public final static int NOTIF    = 2;
	public final static int JOIN     = 3; 
	public final static int PUT 	 = 4;
	public final static int GET		 = 5;
	public final static int SETSUCC	 = 6;	
	public final static int SETPRED	 = 7;	
	
	/**
	 * Find the node responsible for the id
	 * @param id
	 */
	public Node findSuccessor(int id);
	
	/**
	 * Find the closest Preceding Node of the id in the figer table
	 * @param id
	 */
	public Node closestPrecedingNode(int id);
	
	/**
	 * Join an other chord
	 * @param chord, An entry of the network to join
	 */
	public void join(Node chord);
	
	/**
	 * Call the stabilization algorithm
	 */
	public void stabilize();
	
	/**
	 * Notify a node to update the predecessor
	 * @param node
	 */
	public void notify(Node node);
	
	/**
	 * Join a chord with an entry represented by the host
	 * @param host
	 * @param port
	 */
	public void join(String host, int port);
	
	// TOOLS
	public void kill();

	// GETTER AND SETTER
	public Node getThisNode();
	public Node getPredecessor();
}
