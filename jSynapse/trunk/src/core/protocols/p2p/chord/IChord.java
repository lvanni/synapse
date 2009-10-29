package core.protocols.p2p.chord;

import core.protocols.p2p.IOverlay;
import core.protocols.p2p.Node;

public interface IChord extends IOverlay{
	
	/** Code for the transport*/
	public final static int GETPRED  = 0; // GETPRED
	public final static int FINDSUCC = 1; // FINDSUCC,<id>
	public final static int NOTIF    = 2; // NOTIF,<ip>,<id>,<port>
	public final static int JOIN     = 3; 
	public final static int PUT 	 = 4;
	public final static int GET		 = 5;
	public final static int SETSUCC	 = 6;	
	public final static int SETPRED	 = 7;	
	
	// ALGO
	public Node findSuccessor(int id);
	public Node closestPrecedingNode(int id);
	public void join(Node chord);
	public void stabilize();
	public void notify(Node node);
	
	public void join(String host, int port);
	
	// TOOLS
	public void kill();

	// GETTER AND SETTER
	public Node getThisNode();
	public Node getPredecessor();
}
