package experiment.networking.current.node.kademlia.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import core.protocol.p2p.kad.org.planx.xmlstore.routing.Identifier;
import core.protocol.p2p.kad.org.planx.xmlstore.routing.Kademlia;
import core.protocol.p2p.kad.org.planx.xmlstore.routing.RoutingException;

public class Kad {

	public static void main(String args[]){
		try {
			int port = 8000;
			if (args.length == 2 && (args[0].equals("-p") || args[0].equals("--port"))) {
				port = Integer.parseInt(args[1]);
			}
			Kademlia kad = new Kademlia(Identifier.randomIdentifier(), port);
			if(port != 8000){
				kad.connect(new InetSocketAddress(InetAddress.getLocalHost(), 8000));
			}
			
			Identifier key;
			BufferedReader input = new BufferedReader(new InputStreamReader(
					System.in));
			while (true) {
				System.out.println("0) Status");
				System.out.println("1) Put");
				System.out.println("2) Get");
				System.out.println("3) Quit");
				System.out.print("---> ");
				int chx = Integer.parseInt(input.readLine().trim());
				switch (chx) {
				case 0:
					System.out.println("\n" + kad + "\n");
					break;
				case 1:
					System.out.print("\nkey = ");
					key = new Identifier(BigInteger.valueOf((Integer.parseInt(input.readLine().trim()))));
					System.out.print("value = ");
					String value = input.readLine();
					kad.put(key, value);
					break;
				case 2:
					System.out.print("\nkey = ");
					key = new Identifier(BigInteger.valueOf((Integer.parseInt(input.readLine().trim()))));
					System.out.println("found: " + kad.get(key));
					break;
				case 3:
				default:
					break;
				}
			}
		} catch (RoutingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
}
