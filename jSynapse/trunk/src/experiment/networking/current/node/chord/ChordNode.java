package experiment.networking.current.node.chord;

import java.text.SimpleDateFormat;
import java.util.Date;

import core.protocol.p2p.Node;
import core.protocol.p2p.chord.AbstractChord;
import core.protocol.p2p.chord.IChord;
import core.protocol.transport.ITransport;
import core.protocol.transport.socket.request.RequestHandler;
import core.protocol.transport.socket.server.SocketImpl;
import core.tools.HashFunction;
import core.tools.Range;
import experiment.networking.current.Oracle;

/**
 * This is an implementation of a chord node
 * 
 * @author laurent.vanni@sophia.inria.fr - logNet team 2010 - INRIA
 *         Sophia-Antipolis - France
 * 
 */
public class ChordNode extends AbstractChord {
	/** name of the service */
	private static SimpleDateFormat formater = new SimpleDateFormat(
	"dd/MM/yy_H:mm:ss");
	protected static String time = formater.format(new Date());
	public String overlayIndentifier = "chordNetwork"; // use an unique ID is possible
	/** Transport protocol */
	protected ITransport transport;
	/** Hash function */
	private HashFunction h;
	/* just to debug */
	public static boolean debugMode = false;

	/**
	 * Constructor without overlayIntifier (it is auto-generate)
	 * 
	 * @param ip
	 * @param port
	 */
	public ChordNode(String ip, int port) {
		this(ip, port, "<" + ip + port + ">");
	}
	
	/**
	 * Default constructor
	 * 
	 * @param ip
	 * @param port
	 * @param overlayIdentifier
	 *            the identifier of the chord network
	 */

	public ChordNode(String ip, int port, String overlayIdentifier) {
		// DEFAULT TRANSPORT LAYER BASED ON THE SOCKET IMPLEMENTATION
		this.transport = new SocketImpl(port, 10, RequestHandler.class.getName(),
				10, 1, 100, this);
		((SocketImpl) transport).launchServer();
		this.overlayIndentifier = overlayIdentifier;
		this.h = new HashFunction(overlayIdentifier);
		int id = h.SHA1ToInt(ip +  transport.getPort() + time);
		
		initialize(ip, id, transport.getPort());
		checkStable();
	}
	
	/**
	 * Constructor with transport parameter
	 * @param ip
	 * @param port
	 * @param overlayIdentifier
	 * @param transport
	 */
	public ChordNode(String ip, int port, String overlayIdentifier, ITransport transport) {
		this.transport = transport;
		this.overlayIndentifier = overlayIdentifier;
		this.h = new HashFunction(overlayIdentifier);
		int id = h.SHA1ToInt(ip +  transport.getPort() + time);

		initialize(ip, id, transport.getPort());
		checkStable();
	}

	/**
	 * @see core.protocol.p2p.chord.AbstractChord#sendRequest(String, Node)
	 */
	public String sendRequest(String message, Node destination) {
		return transport.sendRequest(getIdentifier() + "," + message,
				destination);
	}

	/**
	 * @see core.protocol.p2p.chord.AbstractChord#join(String, int)
	 */
	public void join(String host, int port) {
		Node chord = new Node(host, keyToH(host + port), port);
		join(chord);
	}

	/**
	 * @see core.protocol.p2p.IDHT#put(String, String)
	 */
	public void put(String key, String value) {
		int hKey;
		hKey = keyToH(key);
		put(hKey, value);
	}

	/**
	 * Recursive put
	 * 
	 * @param hKey
	 * @param value
	 */
	private void put(int hKey, String value) {
		if (Range.inside(hKey, getPredecessor().getId() + 1, getThisNode()
				.getId())) {
				table.put(hKey, value);
		} else {
			sendRequest(IChord.PUT + "," + hKey + "," + value,
					closestPrecedingNode(hKey));
		}
	}

	/**
	 * @see core.protocol.p2p.IDHT#get(String)
	 */
	public String get(String key) {
		int hKey;
		String message = "";
		hKey = keyToH(key);
		message = get(hKey);
		return message;
	}

	/**
	 * recursive get
	 * 
	 * @param hKey
	 * @return the value associate to the key
	 */
	public String get(int hKey) {
		if (Range.inside(hKey, getPredecessor().getId() + 1, getThisNode()
				.getId())) {
			return table.get(hKey);
		} else {
			return sendRequest(IChord.GET + "," + hKey,
					closestPrecedingNode(hKey));
		}
	}

	/**
	 * @see core.protocol.transport.IRequestHandler#handleRequest(String)
	 */
	public String handleRequest(String code) {
		if (debugMode) {
			System.out.println("\n** DEBUG: doStuff\n*\tcode: " + code);
		}
		String[] args = code.split(",");
		String result = "";
		if (args[0].equals(getIdentifier())) {
			int f = Integer.parseInt(args[1]);
			switch (f) {
			case IChord.GETPRED:
				if (getPredecessor() != null)
					result = getPredecessor().toString();
				break;
			case IChord.FINDSUCC:
				result = findSuccessor(Integer.parseInt(args[2])).toString();
				break;
			case IChord.NOTIF:
				notify(new Node(args[2], Integer.parseInt(args[3]), Integer
						.parseInt(args[4])));
				break;
			case IChord.JOIN:
				getObjectOnJoin(new Node(args[2], Integer.parseInt(args[3]),
						Integer.parseInt(args[4])));
				break;
			case IChord.PUT:
				put(Integer.parseInt(args[2]), args[3]);
				break;
			case IChord.GET:
				result = get(Integer.parseInt(args[2]));
				break;
			case IChord.SETSUCC:
				setSuccessor(new Node(args[2], Integer.parseInt(args[3]),
						Integer.parseInt(args[4])));
				break;
			case IChord.SETPRED:
				setPredecessor(new Node(args[2], Integer.parseInt(args[3]),
						Integer.parseInt(args[4])));
				break;
			// ORACLE TESTS
			case Oracle.PUT:
				put(args[2], args[3]);
				break;
			case Oracle.GET:
				result = get(args[2]);
				break;
			default:
				break;
			}
		} else if (args[0].equals("getIdentifier")) {
			return getIdentifier();
		}
		if (debugMode) {
			System.out.println("*\tresult: " + result
					+ "\n************************************");
		}
		return result;
	}

	@Override
	public String toString() {
		return getIdentifier() + " on " + getThisNode().getIp() + ":"
		+ getThisNode().getPort() + "\n" + super.toString();
	}

	/**
	 * @see core.protocol.p2p.IDHT#getTransport()
	 */
	public ITransport getTransport() {
		return transport;
	}

	/**
	 * @see core.protocol.p2p.IDHT#getIdentifier()
	 */
	public String getIdentifier() {
		return overlayIndentifier;
	}

	/**
	 * @param key
	 * @return the hash value of the key
	 */
	public int keyToH(String key) { // A CHANGER!
		return h.SHA1ToInt(key);
	}
}
