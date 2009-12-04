package ui;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Set;

import core.Tracker;
import core.protocols.p2p.Node;
import core.protocols.transport.ITransport;
import core.protocols.transport.socket.SocketImpl;

public class LaunchTracker {

	public static void main(String[] args){
		try{
			System.out.print("transport protocol launching");
			ITransport transport = new SocketImpl(8000);
			do{
				Thread.sleep(1000);
			} while(transport == null);
			System.out.println("\t\t[ ok ]");
			Thread.sleep(300);
			System.out.print("tracker creation");
			Tracker tracker = new Tracker(transport);
			System.out.println("\t\t\t[ ok ]");
			Thread.sleep(300);
			System.out.print("tracker launching");
			new Thread(tracker).start();
			System.out.println("\t\t\t[ ok ]");

			BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
			while(true){
				System.out.println("\n\n1) print status");
				System.out.println("0) exit");
				System.out.print("---> ");
				int chx = Integer.parseInt(input.readLine().trim());
				switch(chx){
				case 0 :
					System.exit(0);
				case 1 :
					Set<String> keys = tracker.getPeerSet().keySet();
					for(String key : keys){
						System.out.println(key + ":");
						for(Node n :  tracker.getPeerSet().get(key)){
							System.out.println("\t" + n);
						}
					}
					break;
				default : break;
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
