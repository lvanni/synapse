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

public class KadNode implements IDHT{

	private String identifier;
	private ITransport transport;
	private Node node;
	private Kademlia kad;
	/** Hash function */
	private HashFunction h;

	public KadNode(String identifier) {
		try {
			this.identifier = identifier;
			this.h = new HashFunction(identifier);
			ServerSocket serverSocket = new ServerSocket(0);
			node = new Node(InfoConsole.getIp(), 0, serverSocket.getLocalPort());
			kad = new Kademlia(Identifier.randomIdentifier(), serverSocket.getLocalPort());
			transport = new SocketImpl(0, 10, RequestHandler.class.getName(),
					10, 1, 50, this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void put(String key, String value) {
		Identifier id = new Identifier(BigInteger.valueOf(keyToH(key)));
		System.out.println("put(" + keyToH(key) + ", " + value + ")");
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
		// TODO Auto-generated method stub

	}

	public String handleRequest(String code) {
		// TODO Auto-generated method stub
		return null;
	}
}
