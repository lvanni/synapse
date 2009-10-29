/***************************************************************************
 *                                                                         *
 *                              Node.java                                  *
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

import synapse.Synapse;

public interface Node extends Synapse, java.rmi.Remote{
	/**
	 * 
	 * @return The listening port number of the node
	 * @throws RemoteException
	 */
	public int getPort() throws RemoteException;
	
	/**
	 * 
	 * @param network 
	 * 				the id of the network to stabilize 
	 * @throws RemoteException
	 */
	public void stabilize(String network) throws RemoteException;
	
	/**
	 * 
	 * @param n
	 * 			the node to notify
	 * @param network
	 * 			the network to notify
	 * @throws RemoteException
	 */
	public void notify(Node n, String network) throws RemoteException;
}
