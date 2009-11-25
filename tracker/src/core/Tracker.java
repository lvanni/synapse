package core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.protocols.p2p.ITracker;
import core.protocols.p2p.Node;
import core.protocols.transport.ITransport;
import core.protocols.transport.socket.SocketImpl;

public class Tracker implements ITracker, Runnable{

	/** peerSet<networkID, <id, address>>*/
	private Map<String, List<Node>> peerSet;

	private ITransport transport;

	public Tracker(ITransport transport){
		this.transport = transport;
		peerSet = new HashMap<String, List<Node>>();
	}

	// //////////////////////////// //
	//           CRUD NODE          //
	// //////////////////////////// //
	public void putNode(String networkID, Node node){
		List<Node> nodes = null;
		if((nodes = peerSet.get(networkID)) != null){
			nodes.add(node);
		} else {
			nodes = new ArrayList<Node>();
			nodes.add(node);
			peerSet.put(networkID, nodes);
		}
	}

	public String getJoinEntry(String networkID){
		List<Node> nodes = peerSet.get(networkID);
		return nodes != null && nodes.size() != 0 ? nodes.get(0).toString() : "null";
	}

	public void removeNode(String networkID, String node){
		List<Node> nodes = peerSet.get(networkID);
		nodes.remove(node);
		if(nodes.size() == 0){
			peerSet.remove(networkID);
		}
	}

	// /////////////////////////////////////////// //
	//                  TRANSPORT                  //
	// /////////////////////////////////////////// //
	public String forward(String message, Node destination){
		return transport.forward(message, destination);
	}

	/**
	 * For the transport protocol
	 */
	public String doStuff(String code){
		String[] args = code.split(",");
		String result = "";
			int f = Integer.parseInt(args[0]);
			switch(f){
			case ADDNODE :
				putNode(args[1], new Node(args[2], Integer.parseInt(args[3]), Integer.parseInt(args[4])));
				break;
			case GETCONNECTION :
				result = getJoinEntry(args[1]);
				break;
			default: break;
			}
		return result;
	}

	// /////////////////////////////////////////// //
	//            IMPLEMENTS RUNNABLE              //
	// /////////////////////////////////////////// //
	public void run() {
		ServerSocket serverSocket = null;
		BufferedReader pin = null;
		PrintWriter pout = null;
		serverSocket = ((SocketImpl)transport).getServerSocket();
		Socket soc = null;
		ACCEPT:
			while(true){
				try {
					if((soc = serverSocket.accept()) != null){
						pin  = new BufferedReader(new InputStreamReader(soc.getInputStream()));
						pout = new PrintWriter(new BufferedWriter(
								new OutputStreamWriter(soc.getOutputStream())),
								true);
						String message = pin.readLine(); // receive a message
						String response = "";
						if(message != null)
							response = this.doStuff(message);
						pout.println(response);// sending a response <IP>,<ID>,<Port>
					}
				} catch (IOException e) {
					continue ACCEPT;
				}
			}
	}

	// //////////////////////////// //
	//      GETTER AND SETTER       //
	// //////////////////////////// //
	public Map<String, List<Node>> getPeerSet() {
		return peerSet;
	}

	public void setPeerSet(Map<String, List<Node>> peerSet) {
		this.peerSet = peerSet;
	}

	public ITransport getTransport() {
		return transport;
	}

	public void setTransport(ITransport transport) {
		this.transport = transport;
	}
}
