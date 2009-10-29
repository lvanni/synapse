/***************************************************************************
 *                                                                         *
 *                             Client.java                                 *
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
package example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import synapse.Sender;
import synapse.Synapse;

import example.data.Sentence;


public class Client extends UnicastRemoteObject implements Sender {

	private static final long serialVersionUID = 1954583944323330909L;

	private Synapse chord;
	private Sentence sentence = null;

	public Client(String host, int port) throws RemoteException{
		try {
			chord = (Synapse) Naming.lookup("//" + host + ":" + port + "/Node");
			System.out.println("the client is connected.\n");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
	}
	
	public void nodify(Object o, Synapse n, String network, int nbHop) throws RemoteException {
		this.sentence = (Sentence) o;
		try {
			System.out.println(o + " found by " + n.getId(network) + " on network " + network + " nbHop=" + nbHop);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public void gameOver(Synapse n, String network) throws RemoteException {
		try {
			System.out.println("Game Over on " + n.getId(network) + " on floor " + network);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public void start(){
		try {
			BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
			while (true) {
				System.out.println("\n-------------------");
				System.out.println("1) put an object");
				System.out.println("2) get an object");
				System.out.println("0) exit");
				System.out.print("---> ");
				int chx = Integer.parseInt(stdIn.readLine().trim());
				switch (chx) {
				case 1:
					System.out.print("key = ");
					String key = stdIn.readLine();
					System.out.print("message = ");
					Sentence s = new Sentence(stdIn.readLine());
					System.out.print("TTL = ");
					int TTL = Integer.parseInt(stdIn.readLine().trim());
					chord.putObject(key, s, TTL);
					System.out.println("Objet envoyé [key="+key+"].");
					break;
				case 2:
					System.out.print("key = ");
					String keytoget = stdIn.readLine();
					System.out.println("searching...");
					chord.getObject(keytoget, this);
					int ttl = 50;
					while(ttl != 0){
						Thread.sleep(200);
						if(sentence != null)
							break;
						ttl--;
					}
					System.out.println();
					if(ttl > 0){
						System.out.println("search succefully finished");
					} else {
						System.out.println("search fail!");
					}
					sentence=null;
					break;
				case 0:
					System.out.println("=> Client exit.");
					System.exit(0);
					break;
				default:
					/* nothing to do */
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ConnectException e) {
			System.err.println("Chord unreachable : " + e.getMessage());
			System.err.println("\nSystem exit");
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		String head =  "/***************************************************************************\n" +
		   " *                                                                         *\n" +
		   " *   date       : 29.08.2009                                               *\n" +
		   " *   copyright  : (C) 2009 LogNet Team                                     *\n" +
		   " *                INRIA Sophia Antipolis - Méditerranée , France           *\n" +
		   " *                and Vanni Laurent                                        *\n" +
		   " *                Universite de Nice Sophia Antipolis                      *\n" +
		   " *                http://www-sop.inria.fr/teams/lognet/synapse             *\n" +
		   " *                                                                         *\n" +
		   " *   email      : laurent.vanni@gmail.com  (contact)                       *\n" +
		   " *                Luigi.Liquori@sophia.inria.fr                            *\n" +
		   " *                Cedric.Tedeschi@sophia.inria.fr                          *\n" +
		   " *                                                                         *\n" +
		   " ***************************************************************************/\n\n" +
			
		   "/***************************************************************************\n" +
		   " *  jSynapse is free software: you can redistribute it and/or modify       *\n" +
		   " *  it under the terms of the GNU General Public License as published by   *\n" +
		   " *  the Free Software Foundation, either version 3 of the License, or      *\n" +
		   " *  (at your option) any later version.                                    *\n" +
		   " *                                                                         *\n" +
		   " *  jSynapse is distributed in the hope that it will be useful,            *\n" +
		   " *  but WITHOUT ANY WARRANTY; without even the implied warranty of         *\n" +
		   " *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the          *\n" +
		   " *  GNU General Public License for more details.                           *\n" +
		   " *                                                                         *\n" +
		   " *  You should have received a copy of the GNU General Public License      *\n" +
		   " *  along with jSynapse.  If not, see <http://www.gnu.org/licenses/>.      *\n" +
		   " ***************************************************************************/\n";
		System.out.println(head);
		
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Connection to a chord:");
		System.out.print("- hostname of a node = ");
		try {
			String host = stdIn.readLine();
			System.out.print("- port of this node = ");
			int port = Integer.parseInt(stdIn.readLine().trim());
			Client client = new Client(host, port);
			client.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}