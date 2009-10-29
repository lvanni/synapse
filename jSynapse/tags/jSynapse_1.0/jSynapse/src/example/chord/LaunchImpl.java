/***************************************************************************
 *                                                                         *
 *                            LaunchImpl.java                              *
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LaunchImpl extends UnicastRemoteObject implements Launch{

	private static final long serialVersionUID = 9007409375131253749L;
	private Node node;
	private String host;
	private int port;

	public LaunchImpl() throws RemoteException {}

	public LaunchImpl(Node node, String host, int port) throws RemoteException {
		this.node = node;
		this.host = host;
		this.port = port;
	}

	/**
	 * @see example.chord.Launch#join(String, int)
	 */
	public boolean join(String host, int port) throws RemoteException {
		try {
			Node chord = (Node) Naming.lookup("rmi://" + host + ":" + port + "/Node");
			String network = chord.getNetwork();
			return node.join(chord, network);
		} catch (Exception e){
			return false;
		}
	}

	/**
	 * @see example.chord.Launch#invitation(String, int)
	 */
	public boolean invitation(String host, int port) throws RemoteException {
		try {
			Node syn = (Node) Naming.lookup("rmi://" + host + ":" + port + "/Node");
			String network = node.getNetwork();
			return node.invitation(syn, network);
		} catch (Exception e){
			return false;
		}
	}

	/**
	 * @see example.chord.Launch#createNetwork()
	 */
	public void createNetwork()throws RemoteException {
		SimpleDateFormat formater = new SimpleDateFormat( "dd/MM/yy_H:mm:ss" );
		String time = formater.format( new Date() ); 
		String network = host+":"+port+"_"+time;
		node.addNetwork(network);
	}

	/**
	 * @see example.chord.Launch#nodeInfomation()
	 */
	public String nodeInfomation() throws RemoteException {
		return node.toString();
	}
	
	/**
	 * @see example.chord.Launch#kill()
	 */
	public void kill() throws RemoteException {
		node.kill();
	}

	public static void main(String[] args) {
		String host = "unknown";
		int port = 0;
		boolean join = false;
		String hostToJoin = "unknown";
		int portToJoin = 0;
		boolean invite = false;
		String hostToSyn = "unknown";
		int portToSyn = 0;

		if (args.length >= 2 && args.length <= 8) {
			host = args[0];
			port = Integer.parseInt(args[1].trim());
			// 1 Option
			if(args.length >= 5){
				if(args[2].equals("-j")){
					join = true;
					hostToJoin = args[3];
					portToJoin = Integer.parseInt(args[4].trim());
				} 
				if(args[2].equals("-s")){
					invite = true;
					hostToSyn = args[3];
					portToSyn = Integer.parseInt(args[4].trim());
				}
				// 2 Options
				if(args.length == 8){
					if(args[5].equals("-j")){
						join = true;
						hostToJoin = args[6];
						portToJoin = Integer.parseInt(args[7].trim());
					} 
					if(args[5].equals("-s")){
						invite = true;
						hostToSyn = args[6];
						portToSyn = Integer.parseInt(args[7].trim());
					}
				}
			}

			// Create a node
			try {
				LocateRegistry.createRegistry(port);
				Node node = null;
				boolean success = true; // if the creation is ok
				// each network is identified by the node who has created this network
				// network = hostname+port+time
				SimpleDateFormat formater = new SimpleDateFormat( "dd/MM/yy_H:mm:ss" );
				String time = formater.format( new Date() ); 
				String network = host+":"+port+"_"+time;
				if(!join && !invite){ // Just a new chord	
					node = new NodeImpl(host, port, network);
					Naming.rebind("rmi://" + host + ":" + port + "/Node", node);

				} else if(join && !invite) { // join
					Node chord = (Node) Naming.lookup("rmi://" + hostToJoin + ":" + portToJoin + "/Node");
					network = chord.getNetwork();
					node = new NodeImpl(host, port, network);
					Naming.rebind("rmi://" + host + ":" + port + "/Node", node);
					success = node.join(chord, network);

				} else if(!join && invite) { // invite
					Node chord = (Node) Naming.lookup("rmi://" + hostToSyn + ":"
							+ portToSyn + "/Node");
					chord.addNetwork(network);
					node = new NodeImpl(host, port, network);
					Naming.rebind("rmi://" + host + ":" + port + "/Node", node);
					success = node.invitation(chord, network);

				} else if(join && invite) { // join && invite
					Node chord = (Node) Naming.lookup("rmi://" + hostToJoin + ":"
							+ portToJoin + "/Node");
					network = chord.getNetwork();
					node = new NodeImpl(host, port, network);
					Naming.rebind("rmi://" + host + ":" + port + "/Node", node);
					success = node.join(chord, network);
					Node syn = (Node) Naming.lookup("rmi://" + hostToSyn + ":"
							+ portToSyn + "/Node");
					syn.addNetwork(network);
					success &= node.invitation(syn, network);
					success &= syn.join(node, network);
				}

				if(!success){
					System.out.println(0); // FAIL!
					System.exit(0);
				} else {
					LaunchImpl l = new LaunchImpl(node, host, port);
					Naming.rebind("rmi://" + host + ":" + port + "/Launch", l);
					System.out.println(1); // SUCCESS
					//					while(node.isAlive()){
					//						Thread.sleep(500);
					//					}
					//					Thread.sleep(1000);
					//					System.exit(0);
				}

				BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
				while(true){
					System.out.println("host: " + host);
					System.out.println("port: " + port);
					System.out.println("status: " + (node.isAlive() ? "alive" : "dead"));
					System.out.println("1) show node information");
					System.out.println("2) kill node");

					System.out.print("--> ");
					int chx = 1;
					try {
						chx = Integer.parseInt(input.readLine().trim());
					} catch (IOException e) {
						e.printStackTrace();
					}
					switch(chx){
					case 1 : System.out.println(node); break;
					case 2 : node.kill(); System.exit(0); break;
					default : break;
					}
				}

			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (NotBoundException e) {
				e.printStackTrace();
			} 
		} else {
			System.err.println("Bad number of arguments: Auto <host> <port> [-j <host> <port>] [-n <host> <port>]");
			return;
		}
	}
}