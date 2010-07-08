package experiments.current.node.kademlia;

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
import experiments.current.Oracle;

public class KadNode implements IDHT{

	protected String identifier;
	protected ITransport transport;
	protected Node node;
	protected Kademlia kad;

	/** Hash function */
	protected HashFunction h;
	
	protected KadNode() {}

	public KadNode(String identifier) {
		try {
			this.identifier = identifier;
			this.h = new HashFunction(identifier);
			ServerSocket serverSocket = new ServerSocket(0);
			node = new Node(InfoConsole.getIp(), 0, serverSocket.getLocalPort());
			kad = new Kademlia(Identifier.randomIdentifier(), serverSocket.getLocalPort());
			transport = new SocketImpl(0, 10, RequestHandler.class.getName(),
					10, 1, 100, this);
			((SocketImpl) transport).launchServer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void put(String key, String value) {
		Identifier id = new Identifier(BigInteger.valueOf(keyToH(key)));
//		System.out.println("put(" + keyToH(key) + ", " + value + ")");
		try {
			kad.put(id, value);
		} catch (RoutingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String get(String key) {
		Identifier id = new Identifier(BigInteger.valueOf(keyToH(key)));
		try {
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
		((SocketImpl) transport).stopServer();
	}

	public String handleRequest(String code) {
//		System.out.println("request handled: " + code);
		String[] args = code.split(",");
		String result = "";
		if (args[0].equals(getIdentifier())) {
			int f = Integer.parseInt(args[1]);
			switch (f) {
			case Oracle.PUT:
				put(args[2], args[3]);
				break;
			case Oracle.GET:
				result = get(args[2]);
				break;
			default:
				break;
			}
		} else if (args[0].equals("getIdentifier")) {
			return getIdentifier();
		}
		return result;
	}
	
	public void setKad(Kademlia kad) {
		this.kad = kad;
	}
}
