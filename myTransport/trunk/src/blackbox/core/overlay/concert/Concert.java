package blackbox.core.overlay.concert;

import java.text.SimpleDateFormat;
import java.util.Date;

import core.protocols.p2p.Node;
import core.protocols.p2p.chord.AbstractChord;
import core.protocols.p2p.chord.IChord;
import core.protocols.transport.ITransport;
import core.protocols.transport.socket.request.RequestHandler;
import core.protocols.transport.socket.server.SocketImpl;
import core.tools.HashFunction;
import core.tools.Range;

public class Concert extends AbstractChord {

	// /////////////////////////////////////////// //
	//                 ATTRIBUTES                  //
	// /////////////////////////////////////////// //
	/** name of the service*/
	private static SimpleDateFormat formater = new SimpleDateFormat( "dd/MM/yy_H:mm:ss" );
	protected static String time = formater.format( new Date() );
	public static String OVERLAY_IDENTIFIER = "Concert"; // use an unique ID is possible

	/** Transport protocol */
	protected ITransport transport;

	/** Hash function */
	private HashFunction h;

	/* just for the dev */
	public static boolean debugMode = false;

	// /////////////////////////////////////////// //
	//                CONSTRUCTOR                  //
	// /////////////////////////////////////////// //
	protected Concert(){}

	public Concert(String ip, int port) {
		this.h = new HashFunction(OVERLAY_IDENTIFIER);
		int id = h.SHA1ToInt(ip+port+time);
		
		transport = new SocketImpl(port, 10, RequestHandler.class.getName(), 10, 1, 50, this);
		((SocketImpl) transport).launchServer();
		initialize(ip, id, transport.getPort());
		checkStable();
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
			System.out.println("New entry in the hash table...");
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
		if(debugMode){
			System.out.println("\n** DEBUG: doStuff\n*\tcode: " + code);
		}
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
		} else {
			System.err.println("!!! " + code + " FAIL !!!");
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
	//              GETTER AND SETTER              //
	// /////////////////////////////////////////// //
	public ITransport getTransport() {
		return transport;
	}

	public String getIdentifier() {
		return OVERLAY_IDENTIFIER;
	}

	public int keyToH(String key){          // A CHANGER!
		return h.SHA1ToInt(key);
	}
}

