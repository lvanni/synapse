package core.protocol.p2p;

/**
 * This class represent the node information of a peer
 * 
 * @author laurent.vanni@sophia.inria.fr - logNet team 2010 - INRIA
 *         Sophia-Antipolis - France
 * 
 */
public class Node {
	/** Address ip of the peer */
	private String ip;

	/** id of the peer in the chord */
	private int id;
	
	/** port number used for the transport layer */
	private int port;
	
	/** the id of the network related to the node */
	private String networkId;
	
	/** the type of the node (chord, kad, synapse, ...) */
	private String nodeType;

	/** For the new parameters used by the simulator */
	private static String UNKNOWN_MESSAGE = "this parameter is only used with the simulator";
	
	/**
	 * Default constructor of Node
	 * 
	 * @param ip
	 * @param id
	 * @param port
	 */
	public Node(String ip, int id, int port) {
		this.ip = ip;
		this.id = id;
		this.port = port;
		this.networkId = UNKNOWN_MESSAGE;
		this.nodeType = UNKNOWN_MESSAGE;
	}

	/**
	 * Constructor of Node without id
	 * 
	 * @param ip
	 * @param port
	 */
	public Node(String ip, int port) {
		this(ip, 0, port);
	}

	/**
	 * Constructor based on the toString method
	 * 
	 * @param node
	 */
	public Node(String node) {
		String[] args = node.split(",");
		if (args.length == 3) {
			ip = args[0];
			id = Integer.parseInt(args[1]);
			port = Integer.parseInt(args[2]);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Node) {
			Node n = (Node) obj;
			return n.getId() == id && n.getPort() == port
					&& n.getIp().equals(ip);
		}
		return false;
	}

	@Override
	public String toString() {
		return ip + "," + id + "," + port;
	}

	/**
	 * 
	 * @return the id of the node
	 */
	public int getId() {
		return id;
	}

	/**
	 * Set the id of the node
	 * 
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * 
	 * @return the ip of the node
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * Set the ip of the node
	 * 
	 * @param ip
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * 
	 * @return the port number of the node
	 */
	public int getPort() {
		return port;
	}

	/**
	 * set the port number of the node
	 * 
	 * @param port
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * 
	 * @return get the networkId of the node
	 */
	public String getNetworkId() {
		return networkId;
	}

	/**
	 * Set the networkId
	 * @param networkId
	 */
	public void setNetworkId(String networkId) {
		this.networkId = networkId;
	}

	/**
	 * 
	 * @return the type of the node
	 */
	public String getNodeType() {
		return nodeType;
	}

	/**
	 * set the type of the node
	 * @param type
	 */
	public void setNodeType(String type) {
		this.nodeType = type;
	}
	
}
