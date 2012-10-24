package experiment.networking.current.synapse.plugin.kademlia;

import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;


import core.protocol.p2p.Node;
import core.protocol.p2p.kad.org.planx.xmlstore.routing.Identifier;
import core.protocol.p2p.kad.org.planx.xmlstore.routing.Kademlia;
import core.protocol.p2p.kad.org.planx.xmlstore.routing.RoutingException;
import core.protocol.transport.socket.request.RequestHandler;
import core.protocol.transport.socket.server.SocketImpl;
import core.tools.HashFunction;
import core.tools.InfoConsole;
import experiment.networking.current.node.kademlia.KadNode;
import experiment.networking.current.synapse.Synapse;

public class KadNodePlugin extends KadNode{

	private Synapse synapse;

	public KadNodePlugin(String identifier, Synapse synapse) {
		try {
			this.identifier = identifier;
			this.synapse = synapse;
			this.h = new HashFunction(identifier);
			ServerSocket serverSocket = new ServerSocket(0);
			node = new Node(InfoConsole.getIp(), 0, serverSocket.getLocalPort());
			kad = new Kademlia(Identifier.randomIdentifier(), serverSocket.getLocalPort(), this);
			transport = new SocketImpl(0, 10, RequestHandler.class.getName(),
					10, 1, 100, this);
			((SocketImpl) transport).launchServer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String get(String key){
		Identifier id = new Identifier(BigInteger.valueOf(keyToH(key)));
		try {
			return (String) kad.get(id);
		} catch (RoutingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} return "err";
	}

	public String handleRequest(String code) {
//		System.out.println("receive: " + code);
		String[] args = code.split(",");
		if (args[0].equals(getIdentifier())) { 
			super.handleRequest(code);
		} else {
			for(String arg : args){
				if(arg.split("=")[0].equals("lookup")){
					String key = arg.split("=")[1].split("]")[0];
					String cleanKey = synapse.getInCleanTable(key+ "|" + identifier);
//					String cleanKey = synapse.getInCleanTable(key);
//					System.out.println("search " + key);
					if (cleanKey != null && !cleanKey.equals("null")
							&& !cleanKey.equals("")) {
//						System.out.println("CleanKey found!\t" + key + " => " + cleanKey);
						if (synapse.cacheTableExist(cleanKey).equals("1")) {
							// THEN SYNAPSE AND USE THE CACHE TABLE
							synapse.synapseGet(cleanKey, identifier);
						}
					} else {
//						System.out.println("CleanKey not found!\t" + key);
					}
				}
			}
		}
		return null;
	}
}
