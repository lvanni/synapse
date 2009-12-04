package core.experiments.networks2010;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import core.experiments.networks2010.synapse.Synapse;
import core.experiments.networks2010.synapse.plugins.ChordNodePlugin;
import core.experiments.networks2010.tools.InfoConsole;
import core.protocols.p2p.Node;
import core.protocols.transport.ITransport;
import core.protocols.transport.socket.SocketImpl;

public class CreateSynapse implements Runnable{

	private Synapse node;
	protected ITransport transport;

	public CreateSynapse(Synapse node, int port){
		this.node = node;
		try {
			this.transport = new SocketImpl(port);
		} catch (IOException e) {
			System.out.println("port" + port + " already in use: exit(1)");
			node.kill();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}

	public void put(String key, String value) {
		node.put(key, value);
	}

	public String get(String key) {
		return node.get(key);
	}

	public String doStuff(String code){
		String[] args = code.split(",");
		String result = "";
		if(args[0].equals("put")){
			put(args[1], args[2]);
		} else if(args[0].equals("get")){
			return get(args[1]);
		} else if(args[0].equals("kill")){
			node.kill();
		}
		return result;
	}

	public void run() {
		ServerSocket serverSocket = null;
		BufferedReader pin = null;
		PrintWriter pout = null;

		serverSocket = ((SocketImpl)transport).getServerSocket();
		Socket soc = null;
		ACCEPT:
			while(true){
				try {
					if((soc = serverSocket.accept()) != null){
						pin  = new BufferedReader(new InputStreamReader(soc.getInputStream()));
						pout = new PrintWriter(new BufferedWriter(
								new OutputStreamWriter(soc.getOutputStream())),
								true);
						String message = pin.readLine(); // receive a message
						String response = "";
						if(message != null)
							response = this.doStuff(message);
						pout.println(response);// sending a response <IP>,<ID>,<Port>
					}
				} catch (IOException e) {
					continue ACCEPT;
				}
			}
	}

	public ITransport getTransport() {
		return transport;
	}

	public static void main(String[] args){
		try{
			// FIND PARAMS
			boolean join = false;
			String hostToJoin = "";
			int portToJoin = 0;
			int portToListen = 0;
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
				} else if (args[i].equals("-l")){
					portToListen = Integer.parseInt(args[i+1]);
				}
			}

			// BUILD SYNAPSE
			Synapse synapse = new Synapse(InfoConsole.getIp(), Integer.parseInt(args[0]));
			new Thread(synapse).start();
			Thread.yield();
			if(join){
				synapse.join(hostToJoin, portToJoin);
			}

			// BUILD LISTENING
			CreateSynapse cn = new CreateSynapse(synapse, portToListen);
			new Thread(cn).start();
			do{} while(cn.getTransport() == null);

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
				}
				synapse.getNetworks().add(plugin);
			}

			// STAND-BY
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("the synapse is successfully created!");
			while(true){ // WAIT
//				System.out.println("1) Publish");
//				System.out.println("2) Search");
//				System.out.println("3) Quit");
//				System.out.print("---> ");
				try{
					input.readLine();
//					System.out.println("\n" + synapse + "\n");
//					int chx = Integer.parseInt(input.readLine().trim());
//					String key;
//					switch(chx){
//					case 0 :
//						System.out.println("\n" + synapse + "\n"); break;
//					case 1 :
//						System.out.print("\nkey = ");
//						key = input.readLine();
//						System.out.print("value = ");
//						String value = input.readLine();
//						synapse.put(key, value);
//						break;
//					case 2 :
//						System.out.print("\nkey = ");
//						key = input.readLine();
//						System.out.println("found: " + synapse.get(key));
//						break;
//					case 3:
//						synapse.kill();
//					default : break;
//					}
//					System.out.println("\npress Enter to continue...");
//					input.readLine();
				} catch (Exception e) {}
			}
		} catch(ArrayIndexOutOfBoundsException e){
			System.out.println("!!! CreateSynapse <port> -js <host> <port> -l <port> (-a <port> -j <hostToJoin> <portToJoin>)+");
			e.printStackTrace();
		}
	}
}
