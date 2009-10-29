package core.protocols.p2p;

public class Node {
	private String ip;
	private int id;
	private int port;

	public Node(String ip, int id, int port){
		this.ip = ip;
		this.id = id;	
		this.port = port;
	}

	public Node(String node){
		String[] args = node.split(",");
		if(args.length == 3){
			ip = args[0];
			id = Integer.parseInt(args[1]);
			port = Integer.parseInt(args[2]);
		} 
	}

	public String toString(){
		return ip + "," + id + "," + port;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
}
