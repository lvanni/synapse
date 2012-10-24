package simulation;

import core.protocols.p2p.Node;
import core.protocols.transport.ITransport;
import core.protocols.transport.socket.request.RequestHandler;
import core.protocols.transport.socket.server.SocketImpl;
import core.protocols.transport.local.LocalImpl;

public class Oracle {
	
	public Oracle(){
		
	}
	
	
	/**private void lauchCommandLine(){
		while(true){
			
		}
	}**/
	
	
	/**
	 * Main function to launch the application
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			
			ITransport transport = new SocketImpl(0, 10, RequestHandler.class.getName(),
					10, 1, 100, null);
			((SocketImpl) transport).launchServer();
			
			Node simulator = new Node("localhost", LocalImpl.DEFAULT_PORT);
			transport.sendRequest("create synapse 1 chord 2 kad 3", simulator);
			
			// TODO: implement message
			
			transport.sendRequest("message", simulator);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("usage: \nCreate node [chord|kad] <networkID>");
			System.out.println("Create synapse [-a [chord|kad] <networkID>]+");
		}
	}

}
