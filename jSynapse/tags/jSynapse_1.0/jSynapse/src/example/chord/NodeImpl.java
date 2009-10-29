/***************************************************************************
 *                                                                         *
 *                             NodeImpl.java                               *
 *                          -------------------                            *
 *   date       : 29.08.2009                                               *
 *   copyright  : (C) 2009 LogNet Team                                     *
 *                INRIA Sophia Antipolis - Méditerranée , France           *
 *                and Vanni Laurent                                        *
 *                Universite de Nice Sophia Antipolis                      *
 *                http://www-sop.inria.fr/teams/lognet/synapse             *
 *   email      :                                                          *
 *                laurent.vanni@gmail.com  (contact)                       *
 *                Luigi.Liquori@sophia.inria.fr                            *
 *                Cedric.Tedeschi@sophia.inria.fr                          *
 *                                                                         *
 ***************************************************************************/

/***************************************************************************
 *  This file is part of jSynapse.                                         *
 *                                                                         *
 *  jSynapse is free software: you can redistribute it and/or modify       *
 *  it under the terms of the GNU General Public License as published by   *
 *  the Free Software Foundation, either version 3 of the License, or      *
 *  (at your option) any later version.                                    *
 *                                                                         *
 *  jSynapse is distributed in the hope that it will be useful,            *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of         *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the          * 
 *  GNU General Public License for more details.                           *
 *                                                                         *
 *  You should have received a copy of the GNU General Public License      *
 *  along with jSynapse.  If not, see <http://www.gnu.org/licenses/>.      *
 ***************************************************************************/
package example.chord;

import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import synapse.Sender;
import synapse.Synapse;
import example.chord.tools.SimpleSHA1;

public class NodeImpl extends UnicastRemoteObject implements Node {

	private static final long serialVersionUID = 1613424494460703307L;

	private String host;
	private int port = 0;

	// Chord
	public static int SPACESIZE = 8;
	private Map<String, Integer> ids;
	private static int MAXid = (int) Math.pow(2, SPACESIZE - 1) - 1;
	private static int MINid = 0;

	private Map<String, Synapse> predecessors;
	private Map<String, Synapse> successors;

	// Synapse
	private int TTL = 100;
	/** Hands */
	private Map<String, Synapse[]> hands;
	/** index of the next finger */
	private int indice = 0;

	/** hash Table */
	private Map<String, Map<String, Object>> tables;

	/** Game Over strategy*/ 
	private int maxRequestIDs = 100;
	private List<String> requestIDs = new ArrayList<String>();

	/** a synapse use several network */
	private List<String> networks = new ArrayList<String>(); 

	/** Boolean for the Stay Stable thread */
	private boolean alive = true;


	//
	// -- CONSTRUCTORS -----------------------------------------------
	//
	public NodeImpl() throws RemoteException {}

	public NodeImpl(String host, int port, String network) throws RemoteException {
		this.host = host;
		this.port = port;
		ids = new HashMap<String, Integer>();
		try {
			int id = SimpleSHA1.SHA1ToInt(host+port+network, MAXid);
			ids.put(network, id);
			this.networks.add(network);
			successors = new HashMap<String, Synapse>();
			successors.put(network, this);
			predecessors = new HashMap<String, Synapse>();
			predecessors.put(network, this);
			tables = new HashMap<String, Map<String, Object>>();
			Map<String, Object> table = new HashMap<String, Object>();
			tables.put(network, table);
			hands = new HashMap<String, Synapse[]>();
			Synapse[] routingtable =  new Synapse[SPACESIZE];
			for (int i = 1; i < SPACESIZE; i++)
				routingtable[i] = this;
			hands.put(network, routingtable);
			checkStable(network);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	//
	// -- PRIVATE METHODS -----------------------------------------------------
	//
	private static boolean range(int id, int begin, int end) {
		return (begin < end && begin <= id && id <= end)
		|| (begin > end && ((begin <= id && id <= MAXid) || (MINid <= id && id <= end)))
		|| ((begin == end) && (id == begin));
	}
	
	private void checkStable(final String network) {
		new Thread(new Runnable() {
			public void run() {
				while (alive) {
					try {
						for(final String network : networks){
							new Thread(new Runnable(){
								public void run() {
									try {
										stabilize(network);
										fixRoutingTable(network);
									} catch (RemoteException e) {
										e.printStackTrace();
									}
								};
							}).start();
						}
						if(requestIDs.size() > maxRequestIDs)
							requestIDs.clear();
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	/**
	 * called periodically. refreshes finger table entry n.
	 */
	private void fixRoutingTable(String network) throws RemoteException {
		indice++;
		if (indice > SPACESIZE - 1)
			indice = 1;
		Synapse[] routingtable = null;
		try {
			routingtable = hands.get(network);
			int id = getId(network);
			routingtable[indice] = find((id + (int) Math.pow(2, indice - 1))
					% (int) Math.pow(2, SPACESIZE - 1), network);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch(Exception e){}
	}

	private Synapse closestPrecedingNode(int id, String network) throws RemoteException, Exception {
		Synapse[] routingtable = hands.get(network);
		int this_id = getId(network);
		for (int i = SPACESIZE - 1; i > 0; i--) {
			if (routingtable[i].isAlive()
					&& range(routingtable[i].getId(network), this_id + 1, id - 1))
				return routingtable[i];
		}
		return getSuccessor(network);
	}

	//
	// -- INNER CLASS ---------------------------------------------------------
	//
	private class FindMessage implements Runnable{
		private String requestID;
		private String mess;
		private String key;
		private Object o;
		private Sender sender;
		private int nbHop;
		private int TTL;
		private int MRR;
		private String network;

		public FindMessage(String requestID, String mess, String key, Object o, Sender sender, int nbHop, int TTL, int MRR, String network){
			this.requestID = requestID;
			this.mess = mess;
			this.key = key;
			this.o = o;
			this.sender = sender;
			this.nbHop = nbHop;
			this.TTL = TTL;
			this.MRR = MRR;
			this.network = network;
		}
		public void run() {
			try{
				int this_id = getId(network);
				int keySHA1 = SimpleSHA1.SHA1ToInt(key+network, MAXid);
//				System.out.println("\nsearch="+keySHA1+"\tTTL="+TTL);
				if (range(keySHA1, this_id + 1, getSuccessor(network).getId(network))) {
					if(mess.equals("GET")){
						getSuccessor(network).getObject(key, sender, network, nbHop);
					} else if (mess.equals("PUT")){
						getSuccessor(network).putObject(key, o, network);
					}
				} else {
					Synapse x = closestPrecedingNode(keySHA1, network);
					x.find(requestID, mess, key, o, sender, nbHop, TTL-1, MRR);
				}
			} catch (Exception e) {

			}
		}
	}

	//
	// -- PUBLIC METHODS -----------------------------------------------------
	//
	/**
	 * @see synapse.Synapse#getObjectOnJoin(Synapse, int, String)
	 */
	public void getObjectOnJoin(Synapse n, int id, String network) throws RemoteException {
		Object[] keys = tables.get(network).keySet().toArray();
		for(Object key : keys){
			try {
				int keySHA1 = SimpleSHA1.SHA1ToInt(key+network, MAXid);
				if(keySHA1 <= id){
					n.putObject((String) key, tables.get(network).get(key), network);
					tables.get(network).remove(key);
				}
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @see synapse.Synapse#getObject(String, Sender, String, int)
	 */
	public void getObject(String key, Sender s, String network, int nbHop) throws RemoteException{
		Object o = tables.get(network).get(key);
		if(o != null)
			try {
				s.nodify(o, this, network, nbHop);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
	}

	/**
	 * @see synapse.Synapse#getObject(String, Sender)
	 */
	public void getObject(String key, Sender s) throws RemoteException{
		String network = networks.get(0);
		SimpleDateFormat formater = new SimpleDateFormat( "dd/MM/yy_H:mm:ss" );
		String time = formater.format( new Date() ); 
		String requestID = network+key+time;
		find(requestID, "GET", key, null, s, 0, TTL, 0);
	}

	/**
	 * @see synapse.Synapse#putObject(String, Object, String)
	 */
	public void putObject(String key, Object o, String network) throws RemoteException {
		Map<String, Object> table = tables.get(network);
		table.put(key, o);
	}

	/**
	 * @see synapse.Synapse#putObject(String, Object, int)
	 */
	public void putObject(String key, Object o, int MRR) throws RemoteException{
		String network = networks.get(0);
		SimpleDateFormat formater = new SimpleDateFormat( "dd/MM/yy_H:mm:ss" );
		String time = formater.format( new Date() ); 
		String requestID = network+key+time;
		find(requestID, "PUT", key, o, null, 0, TTL, MRR);
	}

	/**
	 * @see synapse.Synapse#find(String, String, String, Object, Sender, int, int, int)
	 */
	public void find(String requestID, String mess, String key, Object o, Sender sender, int nbHop, int TTL, int MRR) throws RemoteException {
		if(!requestIDs.contains(requestID)){
			requestIDs.add(requestID);
			int newMRR = MRR/networks.size();
			if(newMRR == 0 && mess.equals("PUT")){ // No replication
				String network = networks.get(networks.size()-1); // GOOD DEAL
				try {
					int keySHA1 = SimpleSHA1.SHA1ToInt(key+network, MAXid);
					find(keySHA1, network).putObject(key, o, network);
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				for(int i=0 ; i<MRR%networks.size() ; i++){
					network = networks.get(i);
					new Thread(new FindMessage(requestID, mess, key, o, sender, nbHop, TTL, 0, network)).start();
				}
			} else {
				for(int i=0 ; i<networks.size() ; i++){
					String network = networks.get(i);
					if(TTL >= 0){
						try{
							if(i==0) {
								new Thread(new FindMessage(requestID, mess, key, o, sender, nbHop, TTL, newMRR+MRR%networks.size(), network)).start();
							} else {
								new Thread(new FindMessage(requestID, mess, key, o, sender, nbHop, TTL, newMRR, network)).start();
							}
						} catch(Exception e){
							e.printStackTrace();
						}
						nbHop++;
					}
				}
			}
		} else {
			System.out.println("GameOver");
		}
	}

	/**
	 * @see synapse.Synapse#find(int, String)
	 */
	public Synapse find(int id, String network) throws RemoteException {
		try{
			int this_id = getId(network);
			if (range(id, this_id + 1, getSuccessor(network).getId(network))) {
				return getSuccessor(network);
			} else {
				Synapse x = closestPrecedingNode(id, network);
				return x.find(id, network);
			}
		} catch(Exception e) {
			return this;
		}
	}

	/**
	 * @see synapse.Synapse#join(Synapse, String)
	 */
	public boolean join(Synapse n, String network) throws RemoteException {
		try{
			if(!networks.contains(network)){
				addNetwork(network);
			}
			int id = getId(network);
			predecessors.put(network, null);
			Synapse successor = n.find(id, network);
			if(successor.getId(network) != id &&
					successor.getPredecessor(network).getId(network) != id &&
					range(id, successor.getPredecessor(network).getId(network)+2, successor.getId(network)-2)){
				successor.getObjectOnJoin(this, id, network);
				successors.put(network, successor);
				return true;
			} else { // The id already exist = no join.
				System.out.println("successor = " + successor.getId(network));
				System.out.println("predecessor = " + successor.getPredecessor(network).getId(network));
				System.out.println("id = " + id);
				return false;
			}
		} catch (Exception e) { // no join if problem
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @see synapse.Synapse#invitation(Synapse, String)
	 */
	public boolean invitation(Synapse n, String network) throws RemoteException{
		// IF GOOD DEAL
		return n.join(this, network);
	}

	/**
	 * @see example.chord.Node#stabilize(String)
	 */
	public void stabilize(String network) throws RemoteException {
		Synapse x = getSuccessor(network).getPredecessor(network);
		int id = getId(network);
		if (x!= null && range(x.getId(network), id + 1, getSuccessor(network).getId(network) - 1)) {
			successors.put(network, x);
		}
		((Node) successors.get(network)).notify(this, network);
	}

	/**
	 * @see example.chord.Node#notify(Node, String)
	 */
	public void notify(Node n, String network) throws RemoteException {
		int this_id = getId(network);
		if ((getPredecessor(network) == null)
				|| (range(n.getId(network), getPredecessor(network).getId(network) + 1, this_id - 1))) {
			predecessors.put(network, n);
		}
	}

	/**
	 * @see synapse.Synapse#kill()
	 */
	public void kill() throws RemoteException {
		alive = false;
		try {
			for(String network : networks){
				Synapse predecessor = getPredecessor(network);
				Synapse successor = getSuccessor(network);
				predecessor.setSuccessor(successor, network);
				successor.setPredecessor(predecessor, network);
				Map<String, Object> table = tables.get(network);
				for (String key : table.keySet()) {
					successor.putObject(key, table.get(key), network);
					//					successor.addObject(key, table.get(key));
				}
			}
			Thread.sleep(1000); // wait for stabilization
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public String toString() {
		try{
			String res = "";
			for(String network : hands.keySet()){
				int id = getId(network);
				res += "network: " + network + "\n";
				res += "<NODE: " + id + ", PRED: "	
				+ (getPredecessor(network) == null ? getPredecessor(network) : getPredecessor(network).getId(network))
				+ ", SUCC: "
				+ (getSuccessor(network) == null ? getSuccessor(network) : getSuccessor(network).getId(network)) + "> ";
				res += "\n\tFingers Table: ";
				Synapse[] routingtable = hands.get(network);
				try{
					if (routingtable[1] != null) {
						res += "[";
						for (int i = 1; i < SPACESIZE - 1; i++) {
							res += routingtable[i].getId(network) + ", ";
						}
						res += routingtable[SPACESIZE - 1].getId(network) + "]";
					} else {
						res += "null";
					}
				} catch(Exception e) {
					res += "null";
				}
				Map<String, Object> table = tables.get(network);
				if (!table.isEmpty()) {
					res += "\n\tData Content : ";
					res += "\n\tkey\t\thash\t\tdata";
					for (Map.Entry<String, Object> entry : table.entrySet()) {
						res += "\n\t" + entry.getKey();
						try {
							res += "\t\t" + SimpleSHA1.SHA1ToInt(entry.getKey()+network, MAXid);
						} catch (NoSuchAlgorithmException e) {
							e.printStackTrace();
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						res += "\t\t" + entry.getValue().toString();
					}
				}
				res += "\n\n";
			}
			return res;
		} catch (RemoteException e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * @see synapse.Synapse#addNetwork(String)
	 */
	public void addNetwork(String network) throws RemoteException {
		if(!networks.contains(network)){
			try {
				int id = SimpleSHA1.SHA1ToInt(host+port+network, MAXid);
				ids.put(network, id);
				networks.add(network);
				successors.put(network, this);
				predecessors.put(network, this);
				Synapse[] routingtable =  new Synapse[SPACESIZE];
				for (int i = 1; i < SPACESIZE; i++)
					routingtable[i] = this;
				hands.put(network, routingtable);
				Map<String, Object> table = new HashMap<String, Object>();
				tables.put(network, table);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @see synapse.Synapse#getNetwork()
	 */
	public String getNetwork() throws RemoteException {
		Random rand = new Random(); // here you can program your best strategy
		return networks.get(rand.nextInt(networks.size()));
	}

	//
	// -- GETTER and SETTER -----------------------------------------------------
	//
	public Synapse[] getroutingtable(int network) {
		return hands.get(network);
	}

	public void setroutingtable(Synapse[] routingtable, String network) {
		hands.put(network, routingtable);
	}

	public int getId(String network) throws RemoteException {
		return ids.get(network);
	}

	public int getPort() throws RemoteException {
		return port;
	}

	public boolean isAlive() throws RemoteException {
		return alive;
	}

	public Synapse getPredecessor(String network) throws RemoteException {
		try{
			predecessors.get(network).isAlive();
			return predecessors.get(network);
		} catch(Exception e){
			return this;
		}
	}

	public void setPredecessor(Synapse predecessor, String network) throws RemoteException {
		predecessors.put(network, predecessor);
	}

	public Synapse getSuccessor(String network) throws RemoteException {
		try{
			successors.get(network).isAlive();
			return successors.get(network);
		} catch(Exception e){
			return this;
		}
	}

	public void setSuccessor(Synapse successor, String network) throws RemoteException {
		successors.put(network, successor);
	}
}