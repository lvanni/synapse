package core.experiments.networks2010;

import core.protocols.p2p.Node;
import core.protocols.transport.ITransport;
import core.protocols.transport.socket.SocketImpl;

public class Operation {

	public static void main(String[] args){
		try{
			ITransport transport = null;
			do{
				transport = new SocketImpl(0);
			} while(transport == null);
			if(args[0].equals("put")){
				Node n = new Node(args[3], Integer.parseInt(args[4]));
				transport.forward("put" + "," + args[1] + "," + args[2], n);
			} else if(args[0].equals("get")){
				Node n = new Node(args[2], Integer.parseInt(args[3]));
				System.out.println(transport.forward("get" + "," + args[1], n));
			}
		} catch(ArrayIndexOutOfBoundsException e){
			System.out.println("invalid parameters!\n" +
					"=> Operation put <key> <value> <ip> <port>\n" +
					"=> Operation get <key> <ip> <port>");
		}
	}
}
