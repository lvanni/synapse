package core.experiments.next;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import core.experiments.next.synapse.Synapse;
import core.experiments.next.synapse.plugins.ChordNodePlugin;
import core.experiments.tools.InfoConsole;
import core.protocols.p2p.Node;
import core.protocols.transport.socket.SocketImpl;


public class CreateSynapse {

	public static void main(String[] args){
		try{
			// FIND PARAMS
			boolean join = false;
			String hostToJoin = "";
			int portToJoin = 0;
//			int portToListen = 0;
			List<NewNode> l = new ArrayList<NewNode>();
			for(int i=0 ; i<args.length ; i++){
				if(args[i].equals("-js")){
					join = true;
					hostToJoin = args[i+1];
					portToJoin = Integer.parseInt(args[i+2]);
				} else if (args[i].equals("-a")){
					if(args.length > i+2 && args[i+2].equals("-j")){
						l.add(new NewNode(Integer.parseInt(args[i+1]), args[i+3], Integer.parseInt(args[i+4])));
					} else {
						l.add(new NewNode(Integer.parseInt(args[i+1])));
					}
				} 
			}

			// BUILD SYNAPSE
			Synapse synapse = new Synapse(InfoConsole.getIp(), Integer.parseInt(args[0]));
			new Thread(synapse).start();
			Thread.yield();
			if(join){
				synapse.join(hostToJoin, portToJoin);
			}

			// ADDING PLUGIN
			for(NewNode n : l){
				ChordNodePlugin plugin = null;
				if(n.getPortToJoin() != 0){ // IF JOIN
					SocketImpl s = new SocketImpl();
					String identifier = s.forward("getIdentifier", new Node(n.getHostToJoin(),  n.getPortToJoin()));
					plugin = new ChordNodePlugin(InfoConsole.getIp(), n.getPort(), synapse, identifier);
					new Thread(plugin).start();
					Thread.yield();
					plugin.join(n.getHostToJoin(), n.getPortToJoin());
				} else {
					plugin = new ChordNodePlugin(InfoConsole.getIp(), n.getPort(), synapse);
					new Thread(plugin).start();
					Thread.yield();
				}
				synapse.getNetworks().add(plugin);
			}

			// STAND-BY
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("the synapse is successfully created!\n");
			while(true){ // WAIT
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
						System.out.println("\n" + synapse + "\n"); break;
					case 1 :
						System.out.print("\nkey = ");
						key = input.readLine();
						System.out.print("value = ");
						String value = input.readLine();
						synapse.put(key, value);
						break;
					case 2 :
						System.out.print("\nkey = ");
						key = input.readLine();
						System.out.println("found: " + synapse.get(key));
						break;
					case 3:
						synapse.kill();
					default : break;
					}
					System.out.println("\npress Enter to continue...");
					input.readLine();
				} catch (Exception e) {}
			}
		} catch(ArrayIndexOutOfBoundsException e){
			System.out.println("!!! CreateSynapse <port> -js <host> <port> -l <port> (-a <port> -j <hostToJoin> <portToJoin>)+");
			e.printStackTrace();
		}
	}
}
