package core.synapse;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
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
import core.protocols.transport.socket.SocketImpl;
import core.tools.HashFunction;
import core.tools.Range;

public abstract class AbstractSynapse extends AbstractChord implements ISynapse, Runnable{

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
	private Map<Integer, Cache> cacheTable;

	// /////////////////////////////////////////// //
	//                 CONSTRUCTOR                 //
	// /////////////////////////////////////////// //
	public AbstractSynapse(){}

	public AbstractSynapse(String ip, int port, String identifier) {
		this.identifier = identifier;
		this.h = new HashFunction(identifier);
		int id = h.SHA1ToInt(ip+port+time);
		try {
			this.transport = new SocketImpl(port);
		} catch (IOException e) {
			System.out.println("port already in use: exit(1)");
			System.exit(1);
		} // TRANSPORT CHOICE
		initialise(ip, id, transport.getPort());
		networks = new ArrayList<IOverlay>();
		cleanKeyTable = new HashMap<Integer, String>();
		cacheTable = new HashMap<Integer, Cache>();
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
					putInCleanTable(hKey, key);     // SAVE THE CLEAN KEY
					o.put(key, value);  // MULTIPUT	
				}
			}).start();
		}
	}

	// /////////////////////////////////////////// //
	//                     GET                     //
	// /////////////////////////////////////////// //
	public String get(String key, int ttl){
		// 	INIT CACHE TABLE
		int hKey = h.SHA1ToInt(key);
		if(Range.inside(hKey, getPredecessor().getId() + 1, getThisNode().getId())){
			initCacheTable(hKey, ttl);
		} else {
			forward(ISynapse.INITCacheTable + "," + hKey + "," + ttl, findSuccessor(hKey));
		}
		return get(key); // ========> + cache
	}

	public String get(String key){
		int size = networks.size();
		if(size != 0 ){
			for(int i = 1 ; i < size ; i++){
				IOverlay o = networks.get(i);
				new Thread(new Get(key, o, this)).start();
			}
			// CLEAN TABLE
			int hCleanKey = keyToH(networks.get(0).keyToH(key)+"|"+networks.get(0).getIdentifier()); // h(key)|IDENT
			putInCleanTable(hCleanKey, key);
			String res = networks.get(0).get(key);
			// TTL
			int ttl = Integer.parseInt(getTTL(key));
			setTTL(key, ttl-size);
			return res;
		} else {
			return "null";
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

			// RESULT
			String res = o.get(key);
			addValue(key, res);
		}
	}

	/* GET CLEAN KEY */
	public String getInCleanTable(String key){
		int hKey = h.SHA1ToInt(key);
		if(Range.inside(hKey, getPredecessor().getId() + 1, getThisNode().getId())){
			return cleanKeyTable.get(hKey);
		} else {
			return forward(ISynapse.GETCleanKey + "," + hKey, findSuccessor(hKey));
		}
	}

	/* PUT CLEAN KEY */
	public void putInCleanTable(int hKey, String key){
		if(Range.inside(hKey, getPredecessor().getId() + 1, getThisNode().getId())){
			cleanKeyTable.put(hKey, key);
		} else {
			forward(ISynapse.PUTCleanKey + "," + hKey + "," + key, findSuccessor(hKey));
		}
	}

	/* INIT CACHE TABLE */
	public void initCacheTable(int hKey, int ttl){
		cacheTable.put(hKey, new Cache(ttl));
	}

	/* GET TTL */
	public String getTTL(String key){
		int hKey = h.SHA1ToInt(key);
		if(Range.inside(hKey, getPredecessor().getId() + 1, getThisNode().getId())){
			return cacheTable.get(hKey).getTtl() + "";
		} else {
			return forward(ISynapse.GETTTL + "," + hKey, findSuccessor(hKey));
		}
	}

	/* SET TTL */
	public String setTTL(String key, int ttl){
		int hKey = h.SHA1ToInt(key);
		if(Range.inside(hKey, getPredecessor().getId() + 1, getThisNode().getId())){
			return cacheTable.get(hKey).getTtl() + "";
		} else {
			return forward(ISynapse.GETTTL + "," + hKey, findSuccessor(hKey));
		}
	}

	/* GET CHACHE VALUE */
	public String getValue(String key){
		int hKey = h.SHA1ToInt(key);
		if(Range.inside(hKey, getPredecessor().getId() + 1, getThisNode().getId())){
			return cacheTable.get(hKey).getValue();
		} else {
			return forward(ISynapse.GETCacheValue + "," + hKey, findSuccessor(hKey));
		}
	}

	/* ADD CACHE VALUE */
	public void addValue(String key, String value){
		int hKey = h.SHA1ToInt(key);
		if(Range.inside(hKey, getPredecessor().getId() + 1, getThisNode().getId())){
			cacheTable.get(hKey).addValue(value);
		} else {
			forward(ISynapse.ADDCacheValue + "," + key + "," + value, findSuccessor(hKey));
		}
	}

	// /////////////////////////////////////////// //
	//                  TRANSPORT                  //
	// /////////////////////////////////////////// //
	public String forward(String message, Node destination){
		String res = "";
		res = transport.forward(getIdentifier() + "," + message, destination);
		if(res.equals(""))
			res = getThisNode().toString();
		return res;
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
			case ISynapse.INITCacheTable :
				initCacheTable(Integer.parseInt(args[2]), Integer.parseInt(args[3]));
				break;
			case ISynapse.PUTCleanKey :
				putInCleanTable(Integer.parseInt(args[2]), args[3]);
				break;
			case ISynapse.GETCleanKey :
				result =  getInCleanTable(args[2]);
				break;
			case ISynapse.GETTTL :
				result =  getTTL(args[2]);
				break;
			case ISynapse.SETTTL :
				setTTL(args[2], Integer.parseInt(args[3]));
				break;
			case ISynapse.ADDCacheValue :
				addValue(args[2], args[3]);
				break;
			default: break;
			}
		}
		return result;
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
					}
				} catch (IOException e) {
					continue ACCEPT;
				}
			}
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
			for (Map.Entry<Integer, Cache> entry : cacheTable.entrySet()) {
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
