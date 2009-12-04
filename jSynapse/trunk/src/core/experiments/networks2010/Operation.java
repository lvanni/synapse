package core.experiments.networks2010;

import java.io.IOException;

import core.protocols.p2p.Node;
import core.protocols.transport.ITransport;
import core.protocols.transport.socket.SocketImpl;

public class Operation {

	public static void main(String[] args){
		try{
			ITransport transport = null;
			try {
				transport = new SocketImpl(0);
			} catch (IOException e) {
				System.out.println("no port available: exit(1)");
				System.exit(1);
			}

			if(args[0].equals("put")){
				Node n = new Node(args[3], Integer.parseInt(args[4]));
				transport.forward("put" + "," + args[1] + "," + args[2], n);
			} else if(args[0].equals("get")){
				Node n = new Node(args[2], Integer.parseInt(args[3]));
				String found = transport.forward("get" + "," + args[1], n);
				int res = 0;
				if(found != null){
					for(String f : found.split("\\*\\*\\*\\*")){
						if(!f.equals("") && !f.equals("null")){
							res = 1;
							break;
						}
					}
				}
				System.out.println(res);
			} else if(args[0].equals("kill")){
				Node n = new Node(args[2], Integer.parseInt(args[3]));
				transport.forward("kill", n);
			}
		} catch(ArrayIndexOutOfBoundsException e){
			System.out.println("invalid parameters!\n" +
					"=> Operation put <key> <value> <ip> <port>\n" +
					"=> Operation get <key> <ip> <port>" +
					"=> Operation kill <ip> <port>");
		}
	}
}
