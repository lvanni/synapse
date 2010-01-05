package core.experiments.next.nodes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import core.protocols.p2p.Node;
import core.protocols.p2p.chord.AbstractChord;
import core.protocols.p2p.chord.IChord;
import core.protocols.transport.ITransport;
import core.protocols.transport.socket.SocketImpl;
import core.tools.HashFunction;
import core.tools.Range;

public class ChordNode extends AbstractChord implements Runnable{
	// /////////////////////////////////////////// //
	//                 ATTRIBUTES                  //
	// /////////////////////////////////////////// //
	/** name of the service*/
	private static SimpleDateFormat formater = new SimpleDateFormat( "dd/MM/yy_H:mm:ss" );
	protected static String time = formater.format( new Date() );
	public String overlayIntifier = "chord"; // use an unique ID is possible

	/** Transport protocol */
	protected ITransport transport;

	/** Hash function */
	private HashFunction h;

	/* just for the dev */
	public static boolean debugMode = false;

	// /////////////////////////////////////////// //
	//                CONSTRUCTOR                  //
	// /////////////////////////////////////////// //
	public ChordNode(String ip, int port){
		this(ip, port, "<"+ip+port+">");
	}
	public ChordNode(String ip, int port, String overlayIntifier) {
		this.overlayIntifier = overlayIntifier;
		this.h = new HashFunction(overlayIntifier);
		int id = h.SHA1ToInt(ip+port+time);
		try {
			transport = new SocketImpl(port);
		} catch (IOException e) {
			System.out.println("port " + port + " already in use: exit(1)");
			System.exit(1);
		} // TRANSPORT CHOICE
		initialise(ip, id, transport.getPort());
	}

	// /////////////////////////////////////////// //
	//               PUBLIC METHODS                //
	// /////////////////////////////////////////// //
	public String forward(String message, Node destination){
		String res = "";
		res = transport.forward(getIdentifier() + "," + message, destination);
		if(res == null || res.equals(""))
			res = getThisNode().toString(); // <================== A REVOIR
		return res;
	}

	public void join(String host, int port) {
		Node chord = new Node(host,  keyToH(host+port), port);
		join(chord);
	}

	public void put(String key, String value){
		int hKey;
		hKey = keyToH(key);
		put(hKey, value);
	}

	public void put(int hKey, String value){
		if(Range.inside(hKey, getPredecessor().getId() + 1, getThisNode().getId())){
			if(table.containsKey(hKey)){
				table.put(hKey, table.get(hKey) + "****" + value);
			} else {
				table.put(hKey, value);
			}
		} else {
			forward(IChord.PUT + "," + hKey + "," + value, closestPrecedingNode(hKey));
		}
	}

	public String get(String key){
		int hKey;
		String message = "";
		hKey = keyToH(key);
		message = get(hKey);
		return message;
	}

	public String get(int hKey){
		if(Range.inside(hKey, getPredecessor().getId() + 1, getThisNode().getId())){
			return table.get(hKey);
		} else {
			return forward(IChord.GET + "," + hKey, closestPrecedingNode(hKey));
		}
	}

	/**
	 * For the transport protocol
	 */
	public String doStuff(String code){
		if(debugMode){
			System.out.println("\n** DEBUG: doStuff\n*\tcode: " + code);
		}
		String[] args = code.split(",");
		String result = "";
		if(args[0].equals(getIdentifier())){
			int f = Integer.parseInt(args[1]);
			switch(f){
			case IChord.GETPRED :
//				System.out.println("*\taction: getPredecessor()");
				if(getPredecessor() != null)
					result = getPredecessor().toString();
				break;
			case IChord.FINDSUCC :
//				System.out.println("*\taction: findSuccessor(" + args[2] + ")");
				result = findSuccessor(Integer.parseInt(args[2])).toString();
				break;
			case IChord.NOTIF :
//				System.out.println("*\taction: notify(" + args[3] + ")");
				notify(new Node(args[2], Integer.parseInt(args[3]), Integer.parseInt(args[4])));
				break;
			case IChord.JOIN :
//				System.out.println("*\taction: join(" + args[3] + ")");
				getObjectOnJoin(new Node(args[2], Integer.parseInt(args[3]), Integer.parseInt(args[4])));
				break;
			case IChord.PUT :
//				System.out.println("*\taction: put(" + args[2] + "," + args[3] + ")");
				put(Integer.parseInt(args[2]), args[3]);
				break;
			case IChord.GET :
//				System.out.println("*\taction: get(" + args[2] + ")");
				result = get(Integer.parseInt(args[2]));
				break;
			case IChord.SETSUCC :
//				System.out.println("*\taction: setSuccessor(" + args[3] + ")");
				setSuccessor(new Node(args[2], Integer.parseInt(args[3]), Integer.parseInt(args[4])));
				break;
			case IChord.SETPRED :
//				System.out.println("*\taction: setPredecessor(" + args[3] + ")");
				setPredecessor(new Node(args[2], Integer.parseInt(args[3]), Integer.parseInt(args[4])));
				break;
			default: break;
			}
		} else if(args[0].equals("getIdentifier")){
			return getIdentifier();
		}
		if(debugMode){
			System.out.println("*\tresult: " + result + "\n************************************");
		}
		return result;
	}

	public String toString(){
		return getIdentifier() + " on "+ getThisNode().getIp() + ":" + getThisNode().getPort() + "\n" + super.toString();
	}

	// /////////////////////////////////////////// //
	//            IMPLEMENTS RUNNABLE              //
	// /////////////////////////////////////////// //
	public void run() {
		ServerSocket serverSocket = null;
		BufferedReader pin = null;
		PrintWriter pout = null;

		serverSocket = ((SocketImpl)transport).getServerSocket();
		Socket soc = null;
		checkStable(); // LAUNCHING CHORD STABILIZATION
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
						pout.flush();
					}
				} catch (IOException e) {
					continue ACCEPT;
				}
			}
	}


	// /////////////////////////////////////////// //
	//              GETTER AND SETTER              //
	// /////////////////////////////////////////// //
	public ITransport getTransport() {
		return transport;
	}

	public String getIdentifier() {
		return overlayIntifier;
	}

	public int keyToH(String key){          // A CHANGER!
		return h.SHA1ToInt(key);
	}
}
