/***************************************************************************
 *                                                                         *
 *                                Put.java                                 *
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

import java.rmi.Naming;

import synapse.Synapse;
import example.data.Sentence;

public class Put {
	public static void main(String[] args){
		if (args.length >= 3) {
			try {
				Synapse chord = (Synapse) Naming.lookup("//" + args[0] + ":" + args[1] + "/Node");
				String key = args[2];
				Sentence s = new Sentence(args[3]);
				int ttl = Integer.parseInt(args[4].trim());
				chord.putObject(key, s, ttl);
				System.out.println("done.");
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(0);
			}
		} else {
			System.out.println("wrong number of argument: <host> <port> <key> <message> <mrr>");
		}
	}
}
