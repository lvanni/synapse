package experiment.simulation;

import core.protocol.p2p.Node;
import core.protocol.transport.ITransport;
import core.protocol.transport.socket.request.RequestHandler;
import core.protocol.transport.socket.server.SocketImpl;

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
			
			Node simulator = new Node("localhost", Simulator.DEFAULT_PORT);
			String reponse = transport.sendRequest("Hello", simulator);
			System.out.println(reponse);
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("usage: \nCreate node [chord|kad] <networkID>");
			System.out.println("Create synapse [-a [chord|kad] <networkID>]+");
		}
	}

}
