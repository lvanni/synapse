package core.protocols.p2p.chord;

import core.protocols.p2p.IDHT;
import core.protocols.p2p.Node;

/**
 * This interface represent a node in the jChord protocol
 * 
 * @author laurent.vanni@sophia.inria.fr - logNet team 2010 - INRIA
 *         Sophia-Antipolis - France
 * 
 */
public interface IChord extends IDHT {

	/** API OF A CHORD NODE */
	public final static int GETPRED = 0;
	public final static int FINDSUCC = 1;
	public final static int NOTIF = 2;
	public final static int JOIN = 3;
	public final static int PUT = 4;
	public final static int GET = 5;
	public final static int SETSUCC = 6;
	public final static int SETPRED = 7;

	/**
	 * Find the node responsible for the id
	 * 
	 * @param id
	 */
	public Node findSuccessor(int id);

	/**
	 * Find the closest Preceding Node of the id in the figer table
	 * 
	 * @param id
	 */
	public Node closestPrecedingNode(int id);

	/**
	 * Join an other chord
	 * 
	 * @param chord
	 *            , An entry of the network to join
	 */
	public void join(Node chord);

	/**
	 * Call the stabilization algorithm
	 */
	public void stabilize();

	/**
	 * Notify a node to update the predecessor
	 * 
	 * @param node
	 */
	public void notify(Node node);

	/**
	 * Join a chord with an entry represented by his the host address and port
	 * number
	 * 
	 * @param host
	 * @param port
	 */
	public void join(String host, int port);

	/**
	 * "Fairplay" kill of the node
	 */
	public void kill();

	/**
	 * @return a instance of the node
	 */
	public Node getThisNode();

	/**
	 * @return the predecessor of the node
	 */
	public Node getPredecessor();
}
