/***************************************************************************
 *                                                                         *
 *                             Launch.java                                 *
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

import java.rmi.RemoteException;

public interface Launch  extends java.rmi.Remote {
	
	/**
	 * 
	 * @param host
	 * 			the host address to join
	 * @param port
	 * 			the listening port number of the node to join
	 * @return
	 * 			true if the joining is successfully finished, false otherwise
	 * @throws RemoteException
	 */
	public boolean join(String host, int port) throws RemoteException;
	
	/**
	 * 
	 * @param host
	 * 			the host address to invite
	 * @param port
	 * 			the listening port number of the node to invite
	 * @return
	 * 			true if the join is successfully finished, false otherwise
	 * @throws RemoteException
	 */
	public boolean invitation(String host, int port) throws RemoteException;
	
	/**
	 * Create a new network on this node
	 * @throws RemoteException
	 */
	public void createNetwork()throws RemoteException;
	
	/**
	 * 
	 * @return
	 * 			the current informations about this node
	 * @throws RemoteException
	 */
	public String nodeInfomation() throws RemoteException;

	/**
	 * Ask to the node to disconnect itself and shutdown
	 * @throws RemoteException
	 */
	public void kill() throws RemoteException;
}
