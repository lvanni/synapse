package experiment.networking.old2010.nodes;

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

import core.protocol.p2p.Node;
import core.protocol.p2p.chord.AbstractChord;
import core.protocol.p2p.chord.IChord;
import core.protocol.transport.ITransport;
import core.protocol.transport.socket.SimpleSocketImpl;
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
			transport = new SimpleSocketImpl(port);
		} catch (IOException e) {
			System.out.println("port " + port + " already in use: exit(1)");
			System.exit(1);
		} // TRANSPORT CHOICE
		initialize(ip, id, transport.getPort());
	}

	// /////////////////////////////////////////// //
	//               PUBLIC METHODS                //
	// /////////////////////////////////////////// //
	public String sendRequest(String message, Node destination){
		String res = "";
		res = transport.sendRequest(getIdentifier() + "," + message, destination);
		if(res == null || res.equals(""))
			res = getThisNode().toString(); // <================== A REVOIR
		return res;
	}

	public void join(String host, int port) {
		Node chord = new Node(host,  keyToH(host+port), port);
//		this.overlayIntifier = transport.forward("getIdentifier", chord);
//		this.h = new HashFunction(overlayIntifier);
//		int id = h.SHA1ToInt(getThisNode().getIp()+getThisNode().getPort()+time);
//		initialise(getThisNode().getIp(), id, getThisNode().getPort());
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
//			System.out.println("New entry in the hash table...");
		} else {
			sendRequest(IChord.PUT + "," + hKey + "," + value, findSuccessor(hKey));
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
			return sendRequest(IChord.GET + "," + hKey, findSuccessor(hKey));
		}
	}

	/**
	 * For the transport protocol
	 */
	public String handleRequest(String code){
		String[] args = code.split(",");
		String result = "";
		if(args[0].equals(getIdentifier())){
			int f = Integer.parseInt(args[1]);
			switch(f){
			case IChord.GETPRED :
				if(getPredecessor() != null)
					result = getPredecessor().toString();
				break;
			case IChord.FINDSUCC :
				result = findSuccessor(Integer.parseInt(args[2])).toString();
				break;
			case IChord.NOTIF :
				notify(new Node(args[2], Integer.parseInt(args[3]), Integer.parseInt(args[4])));
				break;
			case IChord.JOIN :
				getObjectOnJoin(new Node(args[2], Integer.parseInt(args[3]), Integer.parseInt(args[4])));
				break;
			case IChord.PUT :
				put(Integer.parseInt(args[2]), args[3]);
				break;
			case IChord.GET :
				result = get(Integer.parseInt(args[2]));
				break;
			case IChord.SETSUCC :
				setSuccessor(new Node(args[2], Integer.parseInt(args[3]), Integer.parseInt(args[4])));
				break;
			case IChord.SETPRED :
				setPredecessor(new Node(args[2], Integer.parseInt(args[3]), Integer.parseInt(args[4])));
				break;
			default: break;
			}
		} else if(args[0].equals("getIdentifier")){
			return getIdentifier();
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

		serverSocket = ((SimpleSocketImpl)transport).getServerSocket();
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
							response = this.handleRequest(message);
						pout.println(response);// sending a response <IP>,<ID>,<Port>
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
