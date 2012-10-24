package simulation;

import core.protocols.p2p.Node;
import core.protocols.transport.IRequestHandler;
import core.protocols.transport.ITransport;
import core.protocols.transport.socket.request.RequestHandler;
import core.protocols.transport.socket.server.SocketImpl;

public class Oracle implements IRequestHandler{
	
	public String handleRequest(String message) {
		return null;
	}

	public void kill() {
		// TODO Auto-generated method stub
	}
	
	/**
	 * Main function to launch the application
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			
			ITransport transport = new SocketImpl(0, 10, RequestHandler.class.getName(),
					10, 1, 100, new Oracle());
			((SocketImpl) transport).launchServer();
			
			Node simulator = new Node("localhost", 8000);
			
			// TODO: implement message
			
			transport.sendRequest("message", simulator);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("usage: \nCreate node [chord|kad] <networkID>");
			System.out.println("Create synapse [-a [chord|kad] <networkID>]+");
		}
	}

}
