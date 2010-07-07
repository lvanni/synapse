package experiments.current.synapse.plugin.kademlia;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

import org.planx.xmlstore.routing.Identifier;
import org.planx.xmlstore.routing.Kademlia;
import org.planx.xmlstore.routing.RoutingException;

import core.protocols.p2p.IDHT;
import core.protocols.p2p.Node;
import core.protocols.transport.ITransport;
import core.protocols.transport.socket.request.RequestHandler;
import core.protocols.transport.socket.server.SocketImpl;
import core.tools.HashFunction;
import core.tools.InfoConsole;
import experiments.current.synapse.Synapse;

public class KadNodePlugin implements IDHT{

	private String identifier;
	private ITransport transport;
	private Node node;
	private Kademlia kad;
	private Synapse synapse;
	/** Hash function */
	private HashFunction h;

	public KadNodePlugin(String identifier, Synapse synapse) {
		try {
			this.identifier = identifier;
			this.synapse = synapse;
			this.h = new HashFunction(identifier);
			ServerSocket serverSocket = new ServerSocket(0);
			node = new Node(InfoConsole.getIp(), 0, serverSocket.getLocalPort());
			kad = new Kademlia(Identifier.randomIdentifier(), serverSocket.getLocalPort(), this);
			transport = new SocketImpl(0, 10, RequestHandler.class.getName(),
					10, 1, 50, this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void put(String key, String value) {
		Identifier id = new Identifier(BigInteger.valueOf((Integer.parseInt(key))));
		try {
			kad.put(id, value);
		} catch (RoutingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String get(String key) {
		Identifier id = new Identifier(BigInteger.valueOf((Integer.parseInt(key))));
		try {
			String cleanKey = synapse.getInCleanTable(key);
			if (cleanKey != null && !cleanKey.equals("null")
					&& !cleanKey.equals("")) {
				if (synapse.cacheTableExist(cleanKey).equals("1")) {
					// THEN SYNAPSE AND USE THE CACHE TABLE
					synapse.synapseGet(cleanKey, identifier);
				}
			}
			return (String) kad.get(id);
		} catch (RoutingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} return "err";
	}

	public void join(String host, int port) {
		try {
			kad.connect(new InetSocketAddress(host, port));
		} catch (RoutingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getIdentifier() {
		return identifier;
	}

	public Node getThisNode() {
		return node;
	}

	public ITransport getTransport() {
		return transport;
	}

	public int keyToH(String key) {
		return h.SHA1ToInt(key);
	}

	public void kill() {
		// TODO Auto-generated method stub
	}

	public String handleRequest(String code) {
//		System.out.println("receive: " + code);
		String[] args = code.split(",");
		for(String arg : args){
			if(arg.split("=")[0].equals("lookup")){
				String key = arg.split("=")[1].split("]")[0];
				String cleanKey = synapse.getInCleanTable(key+ "|" + identifier);
//				String cleanKey = synapse.getInCleanTable(key);
				if (cleanKey != null && !cleanKey.equals("null")
						&& !cleanKey.equals("")) {
					System.out.println("CleanKey found!\t" + key + " => " + cleanKey);
					if (synapse.cacheTableExist(cleanKey).equals("1")) {
						// THEN SYNAPSE AND USE THE CACHE TABLE
						synapse.synapseGet(cleanKey, identifier);
					}
				} else {
					System.out.println("CleanKey not found!\t" + key);
				}
			}
		}
		return null;
	}
}
