package core.experiments.networks2010;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import core.experiments.networks2010.nodes.ChordNode;
import core.experiments.tools.InfoConsole;
import core.protocols.p2p.Node;
import core.protocols.transport.socket.SimpleSocketImpl;

//public class CreateNode implements Runnable{

//	private ChordNode node;
//	protected ITransport transport;
//
//	public CreateNode(ChordNode node, int port){
//		this.node = node;
//		try{
//			this.transport = new SocketImpl(port);
//		} catch(Exception e){
//			System.out.println("port" + port + " already in use: exit(1)");
//			node.kill();
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e1) {
//				e1.printStackTrace();
//			}
//		}
//	}
//
//	public void put(String key, String value) {
//		node.put(key, value);
//	}
//
//	public String get(String key) {
//		return node.get(key);
//	}
//
//	public String doStuff(String code){
//		String[] args = code.split(",");
//		String result = "";
//		if(args[0].equals("put")){
//			put(args[1], args[2]);
//		} else if(args[0].equals("get")){
//			return get(args[1]);
//		}  else if(args[0].equals("kill")){
//			node.kill();
//		}
//		return result;
//	}
//
//	public void run() {
//		ServerSocket serverSocket = null;
//		BufferedReader pin = null;
//		PrintWriter pout = null;
//
//		serverSocket = ((SocketImpl)transport).getServerSocket();
//		Socket soc = null;
//		ACCEPT:
//			while(true){
//				try {
//					if((soc = serverSocket.accept()) != null){
//						pin  = new BufferedReader(new InputStreamReader(soc.getInputStream()));
//						pout = new PrintWriter(new BufferedWriter(
//								new OutputStreamWriter(soc.getOutputStream())),
//								true);
//						String message = pin.readLine(); // receive a message
//						String response = "";
//						if(message != null)
//							response = this.doStuff(message);
//						pout.println(response);// sending a response <IP>,<ID>,<Port>
//					}
//				} catch (IOException e) {
//					continue ACCEPT;
//				}
//			}
//	}
//
//	public ITransport getTransport() {
//		return transport;
//	}

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
//				if(args[i].equals("-l")){
//					portToListen = Integer.parseInt(args[i+1]);
//				}
			}

			// BUILD NODE
			ChordNode node = null;
			if(join){
				SimpleSocketImpl s = new SimpleSocketImpl();
				String identifier = s.sendRequest("getIdentifier", new Node(hostToJoin, portToJoin));
				node = new ChordNode(InfoConsole.getIp(), Integer.parseInt(args[0]), identifier);
				new Thread(node).start();
				Thread.yield();
				node.join(hostToJoin, portToJoin);
			} else {
				node = new ChordNode(InfoConsole.getIp(), Integer.parseInt(args[0]));
				new Thread(node).start();
			}

			// BUILD LISTENING
//			CreateNode cn = new CreateNode(node, portToListen);
//			new Thread(cn).start();
//			do{} while(cn.getTransport() == null);

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
