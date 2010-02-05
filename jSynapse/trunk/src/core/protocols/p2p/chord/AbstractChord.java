package core.protocols.p2p.chord;

import java.util.HashMap;
import java.util.Map;

import core.protocols.p2p.Node;
import core.tools.Range;

public abstract class AbstractChord implements IChord {

	/** the degree of the address space of the chord */
	public static int SPACESIZE = 10;

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
	private int timeToCheck = 200;

	/** The status of the node*/
	private boolean alive = true;

	/**
	 * Initialize a chord node
	 * @param ip, the address ip of the node
	 * @param id, the id of the node
	 * @param port, the port number of the node to listen request
	 */
	public void initialize(String ip, int id, int port){
		thisNode = new Node(ip, id, port);
		successor = thisNode;
		predecessor = thisNode;
		table = new HashMap<Integer, String>();
		for (int i = 1; i < SPACESIZE; i++)
			fingersTable[i] = thisNode;
	}

	// /////////////////////////////////////////// //
	//               CHORD ALGORITHM               //
	// /////////////////////////////////////////// //

	public Node findSuccessor(int id) {
		if (Range.inside(id, thisNode.getId() + 1, successor.getId())) {
			return successor;
		} else {
			Node pred = closestPrecedingNode(id);
			String succ = forward(FINDSUCC + "," + id, pred);
			return new Node(succ);
		} 
	}


	public Node closestPrecedingNode(int id) {
		for (int i = SPACESIZE - 1; i > 0; i--) {
			if (Range.inside(fingersTable[i].getId(), thisNode.getId() + 1, id - 1)) // && fingersTable[i].isAlive()
				return fingersTable[i];
		}
		return successor;
	}


	public void join(Node chord) {
		predecessor = null;
		String succ = forward(FINDSUCC + "," + thisNode.getId(), chord);
		successor = new Node(succ);
		// GET OBJECT ON JOIN
		while(getPredecessor() == null){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		forward(JOIN + "," + thisNode, successor);
	}


	public void stabilize() {
		String pred = forward(GETPRED + "", successor);
		if(!pred.equals(thisNode.toString())){ // PRED != NULL
			Node x = new Node(pred);
			if (Range.inside(x.getId(), thisNode.getId() + 1, successor.getId() - 1)) {
				successor = x;
			}
			forward(NOTIF + "," + thisNode, successor);
		}
	}


	public void notify(Node node) {
		if ((predecessor == null) || (Range.inside(node.getId(), predecessor.getId() + 1, thisNode.getId() - 1)))
			predecessor = node;
	}

	/**
	 * Fix the fingers table
	 */
	private void fixFingersTable(){
		next++;
		if (next > SPACESIZE - 1)
			next = 1;
		fingersTable[next] = findSuccessor((thisNode.getId() + (int) Math.pow(2, next - 1))% (int) Math.pow(2, SPACESIZE - 1));
	}

	/**
	 * checks whether predecessor has failed.
	 */
	public void checkPredecessor() {
		/* not yet implemented */
	}

	// /////////////////////////////////////////// //
	//                  ALGORITHM +                //
	// /////////////////////////////////////////// //
	/**
	 * Get the objects to the node responsible for
	 * @param node
	 */
	protected void getObjectOnJoin(Node node){
		Object[] keys = table.keySet().toArray();
		for(Object key : keys){
			if(!Range.inside((Integer)key, node.getId(), getThisNode().getId())){
				forward(PUT + "," + (Integer)key + "," + table.get(key), node);
				table.remove(key);
			}
		}
	}

	/**
	 * "Fairplay" kill of the node 
	 */
	public void kill() {
		this.alive = false;
		forward(SETSUCC + "," + successor, predecessor);
		forward(SETPRED + "," + predecessor, successor);
		Object[] keys = table.keySet().toArray();
		for(Object key : keys){
			forward(PUT + "," + (Integer)key + "," + table.get(key), successor);
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
	//              ABSTRACT METHODS               //
	// /////////////////////////////////////////// //
	/**
	 * forward a message to an other node
	 * @param message
	 * @param destination
	 * @return the response to this message
	 */
	public abstract String forward(String message, Node destination);

	// /////////////////////////////////////////// //
	//                   DISPLAY                   //
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
	//              GETTER AND SETTER              //
	// /////////////////////////////////////////// //
	/**
	 * @return a instance of the node
	 */
	public Node getThisNode() {
		return thisNode;
	}

	/**
	 * @return the predecessor of the node
	 */
	public Node getPredecessor() {
		return predecessor;
	}
	
	/**
	 * Set the predecessor of the node
	 * @param n
	 */
	public void setPredecessor(Node n){
		predecessor = n;
	}
	
	/**
	 * Set the successor of the node
	 * @param n
	 */
	public void setSuccessor(Node n){
		successor = n;
	}
}
