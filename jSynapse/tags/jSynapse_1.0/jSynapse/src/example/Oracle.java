/***************************************************************************
 *                                                                         *
 *                              Oracle.java                                *
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

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import example.chord.Launch;

public class Oracle {

	public static void main(String[] args){
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
		String host = "unknown";
		int port = 0;
		boolean join = false;
		String hostToJoin = "unknown";
		int portToJoin = 0;
		boolean invite = false;
		String hostToSyn = "unknown";
		int portToSyn = 0;
		boolean create = false;
		boolean kill = false;
		boolean print = false;

		try {
			if (args.length >= 3 && args.length <= 5) {
				host = args[0];
				port = Integer.parseInt(args[1].trim());
 
				if(args.length >= 3){
					if(args[2].equals("-j")){ // JOIN
						join = true;
						hostToJoin = args[3];
						portToJoin = Integer.parseInt(args[4].trim());
					} 
					if(args[2].equals("-i")){ // INVITE
						invite = true;
						hostToSyn = args[3];
						portToSyn = Integer.parseInt(args[4].trim());
					}
					if(args[2].equals("-c")){ // CREATE NETWORK
						create = true;;
					}
					if(args[2].equals("-k")){ // KILL
						kill = true;
					} 
					if(args[2].equals("-p")){ // PRINT
						print = true;
					}
					
					Launch launch = (Launch) Naming.lookup("rmi://" + host + ":" + port + "/Launch");
					if(join){
						launch.join(hostToJoin, portToJoin);
					} else if(invite){
						launch.invitation(hostToSyn, portToSyn);
					} else if(create){
						launch.createNetwork();
					} else if(kill){
						launch.kill();
					} else if(print){
						System.out.println(launch.nodeInfomation());
					}
				}
			} else {
				System.err.println("Bad number of arguments: Auto <host> <port> [-j <host> <port>] " +
						"[-i <host> <port>] [-p] [-k]");
				return;
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

	}
}
