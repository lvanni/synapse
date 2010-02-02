package core.synapse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.protocols.p2p.IOverlay;
import core.protocols.p2p.Node;
import core.protocols.p2p.chord.AbstractChord;
import core.protocols.p2p.chord.IChord;
import core.protocols.transport.ITransport;
import core.protocols.transport.socket.request.RequestHandler;
import core.protocols.transport.socket.server.SocketImpl;
import core.tools.HashFunction;
import core.tools.Range;

public abstract class AbstractSynapse extends AbstractChord implements ISynapse{

	/** Collection of networks*/
	protected List<IOverlay> networks;

	private static SimpleDateFormat formater = new SimpleDateFormat( "dd/MM/yy_H:mm:ss" );
	protected static String time = formater.format( new Date() );
	public String identifier;

	/** Hash function */
	protected HashFunction h;

	protected ITransport transport;

	/* just for the dev */
	public static boolean debugMode = false;

	/** cleanKeyTable */
	private Map<Integer, String> cleanKeyTable;

	/** cacheTable */
	private Map<String, Cache> cacheTable;

	// /////////////////////////////////////////// //
	//                 CONSTRUCTOR                 //
	// /////////////////////////////////////////// //
	public AbstractSynapse(){}

	public AbstractSynapse(String ip, int port, String identifier) {
		this.identifier = identifier;
		this.h = new HashFunction(identifier);
		int id = h.SHA1ToInt(ip+port+time);
		networks = new ArrayList<IOverlay>();
		cleanKeyTable = new HashMap<Integer, String>();
		cacheTable = new HashMap<String, Cache>();

		this.transport = new SocketImpl(port, 10, RequestHandler.class.getName(), 10, 1, 50, this);
		((SocketImpl) transport).launchServer();
		initialise(ip, id, transport.getPort());
		checkStable();
	}

	// /////////////////////////////////////////// //
	//              SYNAPSE ALGORITHM              //
	// /////////////////////////////////////////// //
	public void invite() {
		// TODO Auto-generated method stub
	}

	public void join(String host, int port) {
		Node chord = new Node(host,  h.SHA1ToInt(host+port+identifier), port);
		join(chord); // chord join
	}

	public void kill(){
		for(IOverlay o : networks){
			((AbstractChord) o).kill();
		}
		super.kill();
	}

	// /////////////////////////////////////////// //
	//                     PUT                     //
	// /////////////////////////////////////////// //
	@Deprecated /* multiPut */
	public void put(final String key, final String value){
		for(final IOverlay o : networks){
			new Thread(new Runnable() {
				public void run() {
					int hKey = keyToH(o.keyToH(key)+"|"+o.getIdentifier()); // h(key)|IDENT
					putInCleanTable(hKey, key);     						// SAVE THE CLEAN KEY
					o.put(key, value);  									// MULTIPUT	
				}
			}).start();
		}
	}

	// /////////////////////////////////////////// //
	//                     GET                     //
	// /////////////////////////////////////////// //
	public String get(String key){
		// 	INIT CACHE TABLE
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
	
	public void synapseGet(String key, String overlayIntifier){
		for(int i = 0 ; i < networks.size() ; i++){
			IOverlay o = networks.get(i);
			if(!o.getIdentifier().equals(overlayIntifier)){
				System.out.println("find in " + o.getIdentifier());
				new Thread(new Get(key, o, this)).start();
			}
		}
	}

	private class Get implements Runnable{
		private String key;
		private AbstractSynapse s;
		private  IOverlay o;
		public Get(String key, IOverlay o, AbstractSynapse s){
			this.key = key;
			this.s = s;
			this.o = o;
		}
		public void run() {
			// CLEAN TABLE
			int hCleanKey = keyToH(o.keyToH(key)+"|"+o.getIdentifier()); // h(key)|IDENT
			putInCleanTable(hCleanKey, key);

			// ADD VALUE IN THE CACHE TABLE
			addValue(key, o.get(key));
		}
	}

	// GET CLEAN KEY
	public String getInCleanTable(String key){
		int hKey = h.SHA1ToInt(key);
		if(Range.inside(hKey, getPredecessor().getId() + 1, getThisNode().getId())){
			return cleanKeyTable.get(hKey);
		} else {
			return forward(ISynapse.GETCleanKey + "," + key, findSuccessor(hKey));
		}
	}

	// PUT CLEAN KEY
	public void putInCleanTable(int hKey, String key){
		if(Range.inside(hKey, getPredecessor().getId() + 1, getThisNode().getId())){
			cleanKeyTable.put(hKey, key);
		} else {
			forward(ISynapse.PUTCleanKey + "," + hKey + "," + key, findSuccessor(hKey));
		}
	}

	// INIT CACHE TABLE
	public void initCacheTable(String key){
		int hKey = h.SHA1ToInt(key);
		if(Range.inside(hKey, getPredecessor().getId() + 1, getThisNode().getId())){
			cacheTable.put(key, new Cache());
		} else {
			forward(ISynapse.INITCacheTable + "," + key, findSuccessor(hKey));
		}
	}
	
	// CACHE TABLE EXIST 
	public String cacheTableExist(String key){
		int hKey = h.SHA1ToInt(key);
		if(Range.inside(hKey, getPredecessor().getId() + 1, getThisNode().getId())){
			return cacheTable.containsKey(key) ? "1" : "0";
		} else {
			return forward(ISynapse.CacheTableExist + "," + key, findSuccessor(hKey));
		}
	}
	
	// REMOVE CACHE TABLE
	public void removeCacheTable(String key){
		int hKey = h.SHA1ToInt(key);
		if(Range.inside(hKey, getPredecessor().getId() + 1, getThisNode().getId())){
			cacheTable.remove(key);
		} else {
			forward(ISynapse.REMOVECacheTable + "," + key, findSuccessor(hKey));
		}
		
	}

	// ADD CACHE VALUE
	public void addValue(String key, String value){
		int hKey = h.SHA1ToInt(key);
		if(Range.inside(hKey, getPredecessor().getId() + 1, getThisNode().getId())){
			if(cacheTable.get(key) != null){
				cacheTable.get(key).addValue(value);
			}
		} else {
			forward(ISynapse.ADDCacheValue + "," + key + "," + value, findSuccessor(hKey));
		}
	}

	// GET CHACHE VALUES
	public String getValues(String key){
		int hKey = h.SHA1ToInt(key);
		if(Range.inside(hKey, getPredecessor().getId() + 1, getThisNode().getId())){
			System.out.println("getValues!! + " + key);
			if(cacheTable.get(key) != null){
				return cacheTable.get(key).getValues();
			} else {
				return null;
			}
		} else {
			System.out.println("forward getValues...: " + key);
			return forward(ISynapse.GETCacheValue + "," + key, findSuccessor(hKey));
		}
	}
	
	// /////////////////////////////////////////// //
	//                  TRANSPORT                  //
	// /////////////////////////////////////////// //
	public String forward(String message, Node destination){
		String res = "";
		res = transport.sendRequest(getIdentifier() + "," + message, destination);
		if(res.equals(""))
			res = getThisNode().toString();
		return res;
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
		if(args[0].equals(identifier)){
			int f = Integer.parseInt(args[1]);
			switch(f){
			// CHORD
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
			case IChord.SETSUCC :
				setSuccessor(new Node(args[2], Integer.parseInt(args[3]), Integer.parseInt(args[4])));
				break;
			case IChord.SETPRED :
				setPredecessor(new Node(args[2], Integer.parseInt(args[3]), Integer.parseInt(args[4])));
				break;
				// SYNAPSE
			case ISynapse.PUTCleanKey :
				putInCleanTable(Integer.parseInt(args[2]), args[3]);
				break;
			case ISynapse.GETCleanKey :
				result =  getInCleanTable(args[2]);
				break;
			case ISynapse.INITCacheTable :
				initCacheTable(args[2]);
				break;
			case ISynapse.CacheTableExist :
				result = cacheTableExist(args[2]);
				break;
			case ISynapse.REMOVECacheTable :
				removeCacheTable(args[2]);
				break;
			case ISynapse.ADDCacheValue :
				addValue(args[2], args[3]);
				break;
			case ISynapse.GETCacheValue :
				result = getValues(args[2]);
				break;
			default: break;
			}
		}
		return result;
	}

	// /////////////////////////////////////////// //
	//                     OTHER                   //
	// /////////////////////////////////////////// //
	public String toString(){
		String res =  identifier + " on "+ getThisNode().getIp() + ":" + getThisNode().getPort() + "\n" + super.toString();
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
		for(IOverlay o : networks){
			res += "\n\n" + o.toString();
		}
		return res;
	}

	// /////////////////////////////////////////// //
	//              GETTER AND SETTER              //
	// /////////////////////////////////////////// //
	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public ITransport getTransport() {
		return transport;
	}

	public List<IOverlay> getNetworks() {
		return networks;
	}

	public int keyToH(String key){          // A CHANGER!
		return h.SHA1ToInt(key);
	}
}
