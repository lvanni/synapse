/***************************************************************************
 *                                                                         *
 *                               Get.java                                  *
 *                          -------------------                            *
 *   date       : 29.08.2009                                               *
 *   copyright  : (C) 2009 LogNet Team                                     *
 *                INRIA Sophia Antipolis - Méditerranée , France           *
 *                and Vanni Laurent                                        *
 *                Universite de Nice Sophia Antipolis                      *
 *                http://www-sop.inria.fr/teams/lognet/synapse             *
 *                                                                         *
 *   email      : laurent.vanni@gmail.com  (contact)                       *
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
package example;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import synapse.Sender;
import synapse.Synapse;


public class Get extends UnicastRemoteObject implements Sender{

	private static final long serialVersionUID = -2239851482281629378L;

	int nbRes = 0;
	int timeToWait = 1000;

	public Get(String host, String port, String key) throws RemoteException {
		try {
			Synapse chord = (Synapse) Naming.lookup("//" + host + ":" + port + "/Node");
			chord.getObject(key, this);
			waitForResults();
		} catch (Exception e) {
			System.out.println(0);
		} 
	}

	public void nodify(Object o, Synapse n, String floor, int nbHop) throws RemoteException {
		System.out.println(1 + "\t" + n.getId(floor) + "\t" + floor + "\tnbHop=" + nbHop);
		nbRes++;
	}
	
	public void gameOver(Synapse n, String floor) throws RemoteException {
		try {
			System.out.println("Game Over " + "\t" + n.getId(floor) + "\t" + floor);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void waitForResults(){
		try{
			Thread.sleep(timeToWait);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(nbRes == 0){
			System.out.println(0);
		}
	}

	public static void main(String[] args){
		if (args.length >= 3) {
			String host = args[0];
			String port = args[1];
			String key = args[2];
			try {
				Get get = new Get(host, port, key);
				UnicastRemoteObject.unexportObject(get, true);
			} catch (RemoteException e) {
				System.out.println(0);
			}
		} else {
			System.out.println("wrong number of argument: <host> <port> <key>");
		}
	}
}
