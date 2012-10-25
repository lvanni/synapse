package core.protocol.p2p.synapse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.protocol.p2p.IDHT;
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
 * This abstract class represent a synapse peer
 * 
 * @author laurent.vanni@sophia.inria.fr - logNet team 2010 - INRIA
 *         Sophia-Antipolis - France
 * 
 */
public abstract class AbstractSynapse extends AbstractChord implements ISynapse {

	/** networks where the synapse is registered */
	protected List<IDHT> networks;
	/** information to construct an unique identifier */
	private static SimpleDateFormat formater = new SimpleDateFormat(
			"dd/MM/yy_H:mm:ss");
	protected static String time = formater.format(new Date());
	/** The control network identifier */
	public String overlayIdentifier;
	/** Hash function */
	protected HashFunction h;
	/** The transport layer */
	protected ITransport transport;
	/* just to debug */
	public static boolean debugMode = false;
	/** cleanKeyTable */
	private Map<Integer, String> cleanKeyTable;
	/** cacheTable */
	private Map<String, Cache> cacheTable;

	/**
	 * 
	 * @param ip
	 * @param port
	 * @param overlayIdentifier
	 */
	protected AbstractSynapse(String ip, int port, String overlayIdentifier) {
		this(ip, port, overlayIdentifier, null);
	}
	
	/**
	 * The default constructor
	 * 
	 * @param ip
	 * @param port
	 * @param overlayIdentifier
	 */
	protected AbstractSynapse(String ip, int port, String overlayIdentifier, ITransport transport) {
		this.overlayIdentifier = overlayIdentifier;
		this.h = new HashFunction(overlayIdentifier);
		this.networks = new ArrayList<IDHT>();
		this.cleanKeyTable = new HashMap<Integer, String>();
		this.cacheTable = new HashMap<String, Cache>();
		int id = h.SHA1ToInt(ip + port + time);
		
		if(transport == null) {
			// DEFAULT TRANSPORT LAYER BASED ON THE SOCKET IMPLEMENTATION
			transport = new SocketImpl(port, 10, RequestHandler.class.getName(),
					10, 1, 100, this);
			((SocketImpl) transport).launchServer();
		} else {
			this.transport = transport;
		}
		
		initialize(ip, id, transport.getPort());
		checkStable();
	}
	
	/**
	 * The default constructor
	 * 
	 * @param ip
	 * @param port
	 * @param overlayIdentifier
	 */
	protected AbstractSynapse(Node nodeInfo, ITransport transport) {
	
		this.overlayIdentifier = nodeInfo.getNetworkId();
		this.h = new HashFunction(overlayIdentifier);
		this.networks = new ArrayList<IDHT>();
		this.cleanKeyTable = new HashMap<Integer, String>();
		this.cacheTable = new HashMap<String, Cache>();
		this.transport = transport;
		int id = this.h.SHA1ToInt(nodeInfo.getIp() + nodeInfo.getPort() + time);
		
		nodeInfo.setId(id);
		initialize(nodeInfo);
		checkStable();
	}

	/**
	 * @see core.protocol.p2p.synapse.ISynapse#invite()
	 */
	public void invite() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see core.protocol.p2p.synapse.ISynapse#join(String, int)
	 */
	public void join(String host, int port) {
		Node chord = new Node(host, h.SHA1ToInt(host + port + overlayIdentifier), port);
		join(chord); // chord join
	}

	/**
	 * @see core.protocol.p2p.chord.IChord#kill()
	 */
	public void kill() {
		for (IDHT o : networks) {
			((AbstractChord) o).kill();
		}
		super.kill();
	}

	/**
	 * @see core.protocol.p2p.IDHT#put(String, String)
	 */
	@Deprecated
	/* multiPut */
	public void put(final String key, final String value) {
		for (final IDHT o : networks) {
			new Thread(new Runnable() {
				public void run() {
					int hKey = keyToH(o.keyToH(key) + "|" + o.getOverlayIntifier()); // h(key)|IDENT
					putInCleanTable(hKey, key); // SAVE THE CLEAN KEY
					putInCleanTable(o.keyToH(key), key);
					o.put(key, value); // MULTIPUT
				}
			}).start();
		}
	}

	/**
	 * @see core.protocol.p2p.IDHT#get(String)
	 */
	public String get(String key) {
		// INIT CACHE TABLE
		initCacheTable(key);
		// FIND RESULTS
		synapseGet(key, "");
		// WAIT FOR COLLECTING VALUES AND REMOVE CACHE TABLE
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String res = getValues(key);
		removeCacheTable(key);
		return res;
	}

	/**
	 * Bypass the initialization of the cache table
	 * 
	 * @see core.protocol.p2p.IDHT#get(String)
	 * @param key
	 * @param overlayIntifier
	 *            the overlay where the request come from
	 */
	public void synapseGet(String key, String overlayIntifier) {
		for (int i = 0; i < networks.size(); i++) {
			IDHT o = networks.get(i);
			if (!o.getOverlayIntifier().equals(overlayIntifier)) {
				new Thread(new Get(key, o, this)).start();
			}
		}
	}

	/**
	 * InnerClass , define a threaded get request
	 */
	private class Get implements Runnable {
		private String key;
		private AbstractSynapse s;
		private IDHT o;

		public Get(String key, IDHT o, AbstractSynapse s) {
			this.key = key;
			this.s = s;
			this.o = o;
		}

		public void run() {
			// CLEAN TABLE
			int hCleanKey = keyToH(o.keyToH(key) + "|" + o.getOverlayIntifier()); // h(key)|IDENT
			putInCleanTable(hCleanKey, key);
//			putInCleanTable(o.keyToH(key), key);

			// ADD VALUE IN THE CACHE TABLE
			addValue(key, o.get(key));
		}
	}

	/**
	 * Get back the value of the key (in plain text: no hashed)
	 * 
	 * @param key
	 *            construct from h(ovrelayID|h(key))
	 * @return the key in plain text
	 */
	public String getInCleanTable(String key) {
		int hKey = h.SHA1ToInt(key);
		if (Range.inside(hKey, getPredecessor().getId() + 1, getThisNode()
				.getId())) {
			return cleanKeyTable.get(hKey);
		} else {
			return sendRequest(ISynapse.GETCleanKey + "," + key,
					findSuccessor(hKey));
		}
	}

	/**
	 * Memorize in the control network the key in plain text
	 * 
	 * @param hKey
	 *            = h(key)
	 * @param key
	 */
	public void putInCleanTable(int hKey, String key) {
		if (Range.inside(hKey, getPredecessor().getId() + 1, getThisNode()
				.getId())) {
			cleanKeyTable.put(hKey, key);
		} else {
			sendRequest(ISynapse.PUTCleanKey + "," + hKey + "," + key,
					findSuccessor(hKey));
		}
	}

	/**
	 * Initialize the cache for new key in the cache table
	 * 
	 * @param key
	 */
	public void initCacheTable(String key) {
		int hKey = h.SHA1ToInt(key);
		if (Range.inside(hKey, getPredecessor().getId() + 1, getThisNode()
				.getId())) {
			cacheTable.put(key, new Cache());
		} else {
			sendRequest(ISynapse.INITCacheTable + "," + key,
					findSuccessor(hKey));
		}
	}

	/**
	 * 
	 * @param key
	 * @return true if a cache exist for the key, false otherwise
	 */
	public String cacheTableExist(String key) {
		int hKey = h.SHA1ToInt(key);
		if (Range.inside(hKey, getPredecessor().getId() + 1, getThisNode()
				.getId())) {
			return cacheTable.containsKey(key) ? "1" : "0";
		} else {
			return sendRequest(ISynapse.CacheTableExist + "," + key,
					findSuccessor(hKey));
		}
	}

	/**
	 * Remove the cache of the key in the control network
	 * 
	 * @param key
	 */
	public void removeCacheTable(String key) {
		int hKey = h.SHA1ToInt(key);
		if (Range.inside(hKey, getPredecessor().getId() + 1, getThisNode()
				.getId())) {
			cacheTable.remove(key);
		} else {
			sendRequest(ISynapse.REMOVECacheTable + "," + key,
					findSuccessor(hKey));
		}

	}

	/**
	 * Add a value in cache for the key in the control network
	 * 
	 * @param key
	 * @param value
	 */
	public void addValue(String key, String value) {
		int hKey = h.SHA1ToInt(key);
		if (Range.inside(hKey, getPredecessor().getId() + 1, getThisNode()
				.getId())) {
			if (cacheTable.get(key) != null) {
				cacheTable.get(key).addValue(value);
			}
		} else {
			sendRequest(ISynapse.ADDCacheValue + "," + key + "," + value,
					findSuccessor(hKey));
		}
	}

	/**
	 * 
	 * @param key
	 * @return the concatenation of all the cache value of the key
	 */
	public String getValues(String key) {
		int hKey = h.SHA1ToInt(key);
		if (Range.inside(hKey, getPredecessor().getId() + 1, getThisNode()
				.getId())) {
			if (cacheTable.get(key) != null) {
				return cacheTable.get(key).getValues();
			} else {
				return null;
			}
		} else {
			// System.out.println("forward getValues...: " + key);
			return sendRequest(ISynapse.GETCacheValue + "," + key,
					findSuccessor(hKey));
		}
	}

	/**
	 * @see core.protocol.p2p.chord.AbstractChord#sendRequest(String, Node)
	 */
	public String sendRequest(String message, Node destination) {
		String res = "";
		res = transport.sendRequest(getOverlayIntifier() + "," + message,
				destination);
		// ************************* TO CHANGE
		if (res.equals(""))
			res = getThisNode().toString();
		return res;
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
		if (args[0].equals(overlayIdentifier)) {
			int f = Integer.parseInt(args[1]);
			switch (f) {
			// CHORD
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
			case IChord.SETSUCC:
				setSuccessor(new Node(args[2], Integer.parseInt(args[3]),
						Integer.parseInt(args[4])));
				break;
			case IChord.SETPRED:
				setPredecessor(new Node(args[2], Integer.parseInt(args[3]),
						Integer.parseInt(args[4])));
				break;
			// SYNAPSE
			case ISynapse.PUTCleanKey:
				putInCleanTable(Integer.parseInt(args[2]), args[3]);
				break;
			case ISynapse.GETCleanKey:
				result = getInCleanTable(args[2]);
				break;
			case ISynapse.INITCacheTable:
				initCacheTable(args[2]);
				break;
			case ISynapse.CacheTableExist:
				result = cacheTableExist(args[2]);
				break;
			case ISynapse.REMOVECacheTable:
				removeCacheTable(args[2]);
				break;
			case ISynapse.ADDCacheValue:
				addValue(args[2], args[3]);
				break;
			case ISynapse.GETCacheValue:
				result = getValues(args[2]);
				break;
			// ORACLE GET
			case Oracle.GET:
				result = get(args[2]);
				break;
			default:
				break;
			}
		}
		return result;
	}

	@Override
	public String toString() {
		String res = overlayIdentifier + " on " + getThisNode().getIp() + ":"
				+ getThisNode().getPort() + "\n" + super.toString();
		if (!cleanKeyTable.isEmpty()) {
			res += "\tCleanKey Content : ";
			for (Map.Entry<Integer, String> entry : cleanKeyTable.entrySet()) {
				res += "\n\t  [" + entry.getKey() + "] - ";
				res += entry.getValue().toString();
			}
		}
		if (!cacheTable.isEmpty()) {
			res += "\n\n\tCacheTable Content : ";
			for (Map.Entry<String, Cache> entry : cacheTable.entrySet()) {
				res += "\n\t  [" + entry.getKey() + "] - ";
				res += entry.getValue().toString();
			}
		}
		res += "\n\n";
		for (IDHT o : networks) {
			res += "\n\n" + o.toString();
		}
		return res;
	}

	/**
	 * @see core.protocol.p2p.IDHT#getOverlayIntifier()
	 */
	public String getOverlayIntifier() {
		return overlayIdentifier;
	}

	/**
	 * @see core.protocol.p2p.IDHT#getTransport()
	 */
	public ITransport getTransport() {
		return transport;
	}

	/**
	 * 
	 * @return the list of the networks where the synapse is connected
	 */
	public List<IDHT> getNetworks() {
		return networks;
	}

	/**
	 * @param key
	 * @return the hash value of the key
	 */
	public int keyToH(String key) { // A CHANGER!
		try {
			return Integer.parseInt(key);
		} catch (NumberFormatException e) {
			return h.SHA1ToInt(key);
		}
	}
}
