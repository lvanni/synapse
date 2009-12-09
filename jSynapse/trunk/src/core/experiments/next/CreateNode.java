package core.experiments.next;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import core.experiments.next.nodes.ChordNode;
import core.experiments.tools.InfoConsole;
import core.protocols.p2p.Node;
import core.protocols.transport.socket.SocketImpl;

public class CreateNode {

	public static void main(String[] args){
		try{
			// FIND PARAMS
			boolean join = false;
			String hostToJoin = "";
			int portToJoin = 0;
//			int portToListen = 0;
			for(int i=0 ; i<args.length ; i++){
				if(args[i].equals("-j")){
					join = true;
					hostToJoin = args[i+1];
					portToJoin = Integer.parseInt(args[i+2]);
				}
			}

			// BUILD NODE
			ChordNode node = null;
			if(join){
				SocketImpl s = new SocketImpl();
				String identifier = s.forward("getIdentifier", new Node(hostToJoin, portToJoin));
				node = new ChordNode(InfoConsole.getIp(), Integer.parseInt(args[0]), identifier);
				new Thread(node).start();
				Thread.yield();
				node.join(hostToJoin, portToJoin);
			} else {
				node = new ChordNode(InfoConsole.getIp(), Integer.parseInt(args[0]));
				new Thread(node).start();
				Thread.yield();
			}

			// STAND-BY
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("the node is successfully created!\n");
			while(true){
				System.out.println("0) Show node informations");
				System.out.println("1) Put");
				System.out.println("2) Get");
				System.out.println("3) Quit");
				System.out.print("---> ");
				try{
					int chx = Integer.parseInt(input.readLine().trim());
					String key;
					switch(chx){
					case 0 :
						System.out.println("\n" + node + "\n"); break;
					case 1 :
						System.out.print("\nkey = ");
						key = input.readLine();
						System.out.print("value = ");
						String value = input.readLine();
						node.put(key, value);
						break;
					case 2 :
						System.out.print("\nkey = ");
						key = input.readLine();
						System.out.println("found: " + node.get(key));
						break;
					case 3:
						node.kill();
					default : break;
					}
					System.out.println("\npress Enter to continue...");
					input.readLine();
				} catch (NumberFormatException e) {}
			}
		} catch (Exception e) {
			System.out.println("!!! CreateNode <port> -j <host> <port> -l <port>");
			e.printStackTrace();
		}
	}
}
