/***************************************************************************
 *                                                                         *
 *                             Synapse.java                                *
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
package synapse;

import java.rmi.RemoteException;


public interface Synapse {
	/**
	 * find the node in charge of the id
	 * @param id
	 * 				the id to find
	 * @param network
	 * 				the network currently used to search 
	 * @return
	 * 				the node in charge of the id
	 * @throws RemoteException
	 */
	public Synapse find(int id, String network) throws RemoteException;
	
	/**
	 * Send a message with the synapse protocol
	 * @param requestID
	 * 				the Id of the request
	 * @param mess
	 * 				The message of the request (Get or Put)
	 * @param key
	 * 				The key to find if the message is Get. Or the Key associated to the Object to put in the DHT if the message is Put
	 * @param o
	 * 				the Object to put in the DHT
	 * @param sender
	 * 				the sender of this request
	 * @param nbHop
	 * 				the number of hop between networks
	 * @param TTL
	 * 				the TTL decreased on each node
	 * @param MRR
	 * 				the Maximum Replication Rate
	 * @throws RemoteException
	 */
	public void find(String requestID, String mess, String key, Object o, Sender sender, int nbHop, int TTL, int MRR) throws RemoteException;
	
	/**
	 * 
	 * @param n
	 * 			the node to join
	 * @param network
	 * 			the network to join
	 * @return
	 * 			true if the joining is successfully finished, false otherwise
	 * @throws RemoteException
	 */
	public boolean join(Synapse n, String network) throws RemoteException;
	
	/**
	 * 
	 * @param n
	 * 			the node to invite
	 * @param network
	 * 			the network to invite
	 * @return
	 * 			true if the invitation is successfully finished, false otherwise
	 * @throws RemoteException
	 */
	public boolean invitation(Synapse n, String network) throws RemoteException;
	
	/**
	 * 
	 * @param n
	 * 			the node which receive the objects
	 * @param id
	 * 			the id of the node
	 * @param network
	 * @throws RemoteException
	 */
	public void getObjectOnJoin(Synapse n, int id, String network) throws RemoteException;
	
	/**
	 * 
	 * @param key
	 * 			the key of the object
	 * @param s
	 * 			the sender of the request
	 * @param network
	 * 			the network used to get the object
	 * @param nbHop
	 * @throws RemoteException
	 */
	public void getObject(String key, Sender s, String network, int nbHop) throws RemoteException;
	
	/**
	 * 
	 * @param key
	 * 			the key of the object
	 * @param s
	 * 			the sender of the request
	 * @throws RemoteException
	 */
	public void getObject(String key, Sender s) throws RemoteException;
	
	/**
	 * 
	 * @param key
	 * 			the key of the object
	 * @param o
	 * 			the object to put
	 * @param network
	 * 			the network used to put the object
	 * @throws RemoteException
	 */
	public void putObject(String key, Object o, String network) throws RemoteException;
	
	/**
	 * 
	 * @param key
	 * 			the key of the object
	 * @param o
	 * 			the object to put
	 * @param MRR
	 * 			the Maximum Replication Rate
	 * @throws RemoteException
	 */
	public void putObject(String key, Object o, int MRR) throws RemoteException;

	/**
	 * 
	 * @param network
	 * @return
	 * 		the id of the node corresponding to the network
	 * @throws RemoteException
	 */
	public int getId(String network) throws RemoteException;
	
	/**
	 * 
	 * @param network
	 * @return
	 * 			the predecessor of the node in the network
	 * @throws RemoteException
	 */
	public Synapse getPredecessor(String network)throws RemoteException;
	
	/**
	 * 
	 * @param predecessor
	 * @param network
	 * @throws RemoteException
	 */
	public void setPredecessor(Synapse predecessor, String network) throws RemoteException;
	
	/**
	 * 
	 * @param network
	 * @return
	 * 			the successor of the node in the network
	 * @throws RemoteException
	 */
	public Synapse getSuccessor(String network) throws RemoteException;
	
	/**
	 * 
	 * @param successor
	 * @param network
	 * @throws RemoteException
	 */
	public void setSuccessor(Synapse successor, String network) throws RemoteException;
	
	/**
	 * Add a new network to node
	 * @param network
	 * @throws RemoteException
	 */
	public void addNetwork(String network) throws RemoteException;
	
	/**
	 * 
	 * @return
	 * 		One of the networks of the node
	 * @throws RemoteException
	 */
	public String getNetwork() throws RemoteException;

	/**
	 * 
	 * @return
	 * 			false if the node is going to shutdown, true otherwise
	 * @throws RemoteException
	 */
	public boolean isAlive() throws RemoteException;
	
	/**
	 * Ask to the node to disconnect itself and shutdownd
	 * @throws RemoteException
	 */
	public void kill() throws RemoteException;
}