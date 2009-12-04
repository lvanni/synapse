package core.experiments;

import core.experiments.tools.ITracker;
import core.protocols.p2p.Node;
import core.protocols.transport.ITransport;
import core.protocols.transport.socket.SocketImpl;

public class Operation {

	/** address on the tracker which give the peerSet*/
	private static String TRACKER_HOST = "smart5.inria.fr";
	private static int 	  TRACKER_PORT = 8000;

	public static void main(String[] args){
		try{
			ITransport transport = null;
			do{
				transport = new SocketImpl(0);
			} while(transport == null);
			if(args[0].equals("put")){
				Node n = new Node(transport.forward(ITracker.GETCONNECTION + "," + args[3], new Node(TRACKER_HOST, 0, TRACKER_PORT)));
				transport.forward("put" + "," + args[1] + "," + args[2], n);
			} else if(args[0].equals("get")){
				Node n = new Node(transport.forward(ITracker.GETCONNECTION + "," + args[2], new Node(TRACKER_HOST, 0, TRACKER_PORT)));
				System.out.println(transport.forward("get" + "," + args[1], n));
			}
		} catch(ArrayIndexOutOfBoundsException e){
			System.out.println("invalid parameters!\n=> Operation put <key> <value> <IDNetwork>\n=> Operation get <key> <IDNetwork>");
		}
	}
}
