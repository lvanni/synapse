package experiment.networking.current.node.kademlia;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.ServerSocket;


import core.protocol.p2p.IDHT;
import core.protocol.p2p.Node;
import core.protocol.p2p.kad.org.planx.xmlstore.routing.Identifier;
import core.protocol.p2p.kad.org.planx.xmlstore.routing.Kademlia;
import core.protocol.p2p.kad.org.planx.xmlstore.routing.RoutingException;
import core.protocol.transport.ITransport;
import core.protocol.transport.socket.request.RequestHandler;
import core.protocol.transport.socket.server.SocketImpl;
import core.tools.HashFunction;
import core.tools.InfoConsole;
import experiment.networking.current.Oracle;

public class KadNode implements IDHT{

	protected String identifier;
	protected ITransport transport;
	protected Node node;
	protected Kademlia kad;

	/** Hash function */
	protected HashFunction h;

	protected KadNode() {}

	public KadNode(String overlayIntifier) {
		this(overlayIntifier, null);
	}
	
	public KadNode(String overlayIntifier, ITransport transport) {
		try {
			this.identifier = overlayIntifier;
			this.h = new HashFunction(overlayIntifier);
			ServerSocket serverSocket = new ServerSocket(0);
			node = new Node(InfoConsole.getIp(), 0, serverSocket.getLocalPort());
			kad = new Kademlia(Identifier.randomIdentifier(), serverSocket.getLocalPort());

			if(transport == null) {
				// DEFAULT TRANSPORT LAYER BASED ON THE SOCKET IMPLEMENTATION
				transport = new SocketImpl(0, 10, RequestHandler.class.getName(),
						10, 1, 100, this);
				((SocketImpl) transport).launchServer();
			} else {
				this.transport = transport;
			}

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

	public String getOverlayIntifier() {
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
		if (args[0].equals(getOverlayIntifier())) {
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
			return getOverlayIntifier();
		}
		return result;
	}

	public void setKad(Kademlia kad) {
		this.kad = kad;
	}
}
