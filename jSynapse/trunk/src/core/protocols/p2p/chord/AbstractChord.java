package core.protocols.p2p.chord;

import java.util.HashMap;
import java.util.Map;

import core.protocols.p2p.Node;
import core.tools.Range;

/**
 * This abstract class represent a node in the jChord protocol
 * 
 * @author laurent.vanni@sophia.inria.fr - logNet team 2010 - INRIA
 *         Sophia-Antipolis - France
 * 
 */
public abstract class AbstractChord implements IChord {

	/** the degree of the address space of the chord */
	public static int SPACESIZE = 25;

	/** represent this node */
	private Node thisNode;

	/** Node predecessor */
	private Node predecessor;

	/** Node successor */
	private Node successor;

	/** Fingers table */
	private Node[] fingersTable = new Node[SPACESIZE];

	/** hash Table containing the keys i am responsible for */
	protected Map<Integer, String> table;

	/** index of the next finger */
	private int next = 0;

	/** Time between each stabilization/fix fingerstable */
	private int timeToCheck = 100;

	/** The status of the node */
	private boolean alive = true;

	/**
	 * Initialize a chord node
	 * 
	 * @param ip
	 *            the address ip of the node
	 * @param id
	 *            the id of the node
	 * @param port
	 *            the port number of the node to listen request
	 */
	public void initialize(String ip, int id, int port) {
		thisNode = new Node(ip, id, port);
		successor = thisNode;
		predecessor = thisNode;
		table = new HashMap<Integer, String>();
		for (int i = 1; i < SPACESIZE; i++)
			fingersTable[i] = thisNode;
	}

	// /////////////////////////////////////////// //
	// CHORD ALGORITHM
	// /////////////////////////////////////////// //
	/**
	 * @see core.protocols.p2p.chord.IChord#findSuccessor(int)
	 */
	public Node findSuccessor(int id) {
		if (Range.inside(id, thisNode.getId() + 1, successor.getId())) {
			return successor;
		} else {
			Node pred = closestPrecedingNode(id);
			String succ = sendRequest(FINDSUCC + "," + id, pred);
			return succ.equals("unreachable") ? thisNode : new Node(succ);
		}
	}

	/**
	 * @see core.protocols.p2p.chord.IChord#closestPrecedingNode(int)
	 */
	public Node closestPrecedingNode(int id) {
		for (int i = SPACESIZE - 1; i > 0; i--) {
			if (Range.inside(fingersTable[i].getId(), thisNode.getId() + 1,
					id - 1)) // && fingersTable[i].isAlive()
				return fingersTable[i];
		}
		return successor;
	}

	/**
	 * @see core.protocols.p2p.chord.IChord#join(Node)
	 */
	public void join(Node chord) {
		String succ = sendRequest(FINDSUCC + "," + thisNode.getId(), chord);
		if(!succ.equals("unreachable")){
//			predecessor = null;
			successor = new Node(succ);
			sendRequest(JOIN + "," + thisNode, successor);
		} else {
			System.out.println("Fail to join: " + chord.getIp() + ":" + chord.getPort());
		}
	}

	/**
	 * @see core.protocols.p2p.chord.IChord#stabilize()
	 */
	public void stabilize() {
		String pred = sendRequest(GETPRED + "", successor);
		if (!pred.equals(thisNode.toString()) && !pred.equals("unreachable")) { // PRED != NULL
			Node x = new Node(pred);
			if (Range.inside(x.getId(), thisNode.getId() + 1,
					successor.getId() - 1)) {
				successor = x;
			}
			sendRequest(NOTIF + "," + thisNode, successor);
		}
	}

	/**
	 * @see core.protocols.p2p.chord.IChord#notify()
	 */
	public void notify(Node node) {
		if ((predecessor == null)
				|| (Range.inside(node.getId(), predecessor.getId() + 1,
						thisNode.getId() - 1)))
			predecessor = node;
	}

	/**
	 * Fix the fingers table
	 */
	private void fixFingersTable() {
		next++;
		if (next > SPACESIZE - 1)
			next = 1;
		fingersTable[next] = findSuccessor((thisNode.getId() + (int) Math.pow(
				2, next - 1))
				% (int) Math.pow(2, SPACESIZE - 1));
	}

	/**
	 * checks whether predecessor has failed.
	 */
	public void checkPredecessor() {
		/* not yet implemented */
	}

	// /////////////////////////////////////////// //
	// ALGORITHM +
	// /////////////////////////////////////////// //
	/**
	 * Get the objects to the node responsible for
	 * 
	 * @param node
	 */
	protected void getObjectOnJoin(Node node) {
		Object[] keys = table.keySet().toArray();
		for (Object key : keys) {
			if (!Range.inside((Integer) key, node.getId(), getThisNode()
					.getId())) {
				sendRequest(PUT + "," + (Integer) key + "," + table.get(key), node);
				table.remove(key);
			}
		}
	}

	/**
	 * @see core.protocols.p2p.chord.IChord#kill()
	 */
	public void kill() {
		this.alive = false;
		sendRequest(SETSUCC + "," + successor, predecessor);
		sendRequest(SETPRED + "," + predecessor, successor);
		Object[] keys = table.keySet().toArray();
		for (Object key : keys) {
			sendRequest(PUT + "," + (Integer) key + "," + table.get(key), successor);
		}
		System.out.println("bye bye...");
		try {
			Thread.sleep(1000); // WAIT FOR STABILIZATION
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create a new thread to call periodically the stabilization algorithm
	 */
	public void checkStable() {
		new Thread(new Runnable() {
			public void run() {
				while (alive) {
					try {
						stabilize();
						fixFingersTable();
						Thread.sleep(timeToCheck);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	// /////////////////////////////////////////// //
	// ABSTRACT METHODS
	// /////////////////////////////////////////// //
	/**
	 * forward a message to an other node
	 * 
	 * @param message
	 * @param destination
	 * @return the response to this message
	 */
	public abstract String sendRequest(String message, Node destination);

	// /////////////////////////////////////////// //
	// DISPLAY
	// /////////////////////////////////////////// //
	public String toString() {
		String res = "<NODE: " + thisNode.getId() + ", PRED: "
				+ (predecessor == null ? predecessor : predecessor.getId())
				+ ", SUCC: "
				+ (successor == null ? successor : successor.getId()) + "> ";
		res += "\n\tFingers Table: ";
		if (fingersTable[1] != null) {
			res += "[";
			for (int i = 1; i < SPACESIZE - 1; i++) {
				res += fingersTable[i].getId() + ", ";
			}
			res += fingersTable[SPACESIZE - 1].getId() + "]";
		} else {
			res += "null";
		}
		if (!table.isEmpty()) {
			res += "\n\n\tData Content : ";
			for (Map.Entry<Integer, String> entry : table.entrySet()) {
				res += "\n\t  [" + entry.getKey() + "] - ";
				res += entry.getValue().toString();
			}
		}
		res += "\n\n";
		return res;
	}

	// /////////////////////////////////////////// //
	// GETTER AND SETTER //
	// /////////////////////////////////////////// //
	/**
	 * @see core.protocols.p2p.chord.IChord#getThisNode()
	 */
	public Node getThisNode() {
		return thisNode;
	}

	/**
	 * @see core.protocols.p2p.chord.IChord#getPredecessor()
	 */
	public Node getPredecessor() {
		return predecessor;
	}

	/**
	 * Set the predecessor of the node
	 * 
	 * @param n
	 */
	public void setPredecessor(Node n) {
		predecessor = n;
	}

	/**
	 * Set the successor of the node
	 * 
	 * @param n
	 */
	public void setSuccessor(Node n) {
		successor = n;
	}
}
