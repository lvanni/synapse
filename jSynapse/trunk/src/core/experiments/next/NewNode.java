package core.experiments.next;
public class NewNode{
	private int port;
	private String hostToJoin;
	private int portToJoin;

	public NewNode(int port){
		this(port, "", 0);
	}

	public NewNode(int port, String hostToJoin, int portToJoin){
		this.port = port;
		this.hostToJoin = hostToJoin;
		this.portToJoin = portToJoin;
	}

	public int getPort() {
		return port;
	}

	public String getHostToJoin() {
		return hostToJoin;
	}

	public int getPortToJoin() {
		return portToJoin;
	}

}