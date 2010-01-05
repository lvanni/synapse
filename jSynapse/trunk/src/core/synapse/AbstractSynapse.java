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

	private String result = "";
	private int nbResponse = 0;

	/* just for the dev */
	public static boolean debugMode = false;
	
	/** key table*/
	private Map<Integer, String> keyTable;
	
	/** cache table */
	private Map<Integer, List<String>> cacheTable;

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
		keyTable = new HashMap<Integer, String>();
		cacheTable = new HashMap<Integer, List<String>>();
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

	/* Clean table Put*/
	public void putInCleanTable(int hKey, String value){
		if(Range.inside(hKey, getPredecessor().getId() + 1, getThisNode().getId())){
			keyTable.put(hKey, value);
		} else {
			forward(ISynapse.PUTClean + "," + hKey + "," + value, closestPrecedingNode(hKey));
		}
	}
	
	/* Cache table Put*/
	public void putInCacheTable(int hKey, String value){
		if(Range.inside(hKey, getPredecessor().getId() + 1, getThisNode().getId())){
			if(!cacheTable.containsKey(hKey)){
				cacheTable.put(hKey, new ArrayList<String>());
			}
			cacheTable.get(hKey).add(value);
		} else {
			forward(ISynapse.PUTCache + "," + hKey + "," + value, closestPrecedingNode(hKey));
		}
	}

	// /////////////////////////////////////////// //
	//                     GET                     //
	// /////////////////////////////////////////// //
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
			
			// CACHE TABLE
			int hChacheKey = keyToH(key);
			putInCacheTable(hChacheKey, key);
			
			// RESULT
			String res = o.get(key);
			s.concatResult(res == null ? "null" : res);  // MULTIGET
			nbResponse++;
		}
	}

	public String get(String key){
		result = "";
		nbResponse = 0;
		for(IOverlay o : networks){
			new Thread(new Get(key, o, this)).start();
		}
		while(true){
			if(nbResponse >= networks.size()){
				break;
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	/* Clean table Get*/
	public String getInCleanTable(String key){
		int hKey;
		hKey = h.SHA1ToInt(key);
		if(Range.inside(hKey, getPredecessor().getId() + 1, getThisNode().getId())){
			return keyTable.get(hKey);
		} else {
			return forward(IChord.GET + "," + hKey, closestPrecedingNode(hKey));
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
			case PUTClean :
				putInCleanTable(Integer.parseInt(args[2]), args[3]);
				break;
			case GETClean :
				result =  getInCleanTable(args[2]);
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

	public synchronized void concatResult(String res){
		if(result.equals("")){
			result = res;
		} else {
			result += "****" + res;
		}
	}
}
