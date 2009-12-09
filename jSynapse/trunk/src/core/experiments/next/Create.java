package core.experiments.next;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import core.experiments.next.nodes.ChordNode;
import core.experiments.next.synapse.Synapse;
import core.experiments.tools.ITracker;
import core.experiments.tools.InfoConsole;
import core.protocols.p2p.IOverlay;
import core.protocols.p2p.Node;
import core.protocols.p2p.chord.AbstractChord;

public class Create {

	private static enum NodeType{CHORD, CHORDPLUGIN, SYNAPSE}

	public static IOverlay getNode(String protocol, String id, NodeType t, Synapse s){

		// BUILD NODE
		IOverlay overlay = null;
		String ip = InfoConsole.getIp();
		if(protocol.equals("chord")){
			AbstractChord node = null;
			switch(t){
			case CHORD:
				node = new ChordNode(ip, 0, id);
				break;
			case CHORDPLUGIN:
				//				node = new ChordNodePlugin(ip, 0, s, id);
				break;
			case SYNAPSE:
				//				node = new Synapse(ip, 0, id);
				break;
			}
			new Thread((Runnable) node).start();
			do{} while(node.getTransport() == null);
			overlay = node;
		}

		// CONNECT ON TRACKER
		Node tracker = new Node(ITracker.TRACKER_HOST, 0, ITracker.TRACKER_PORT);
		String trackerResponse = overlay.getTransport().forward(ITracker.GETCONNECTION + "," + overlay.getIdentifier(), tracker);
		overlay.getTransport().forward(ITracker.ADDNODE + "," + overlay.getIdentifier() + "," + overlay.getThisNode(), tracker);
		if(!trackerResponse.equals("null")) {
			Node n = new Node(trackerResponse);
			overlay.join(n.getIp(), n.getPort());
		}

		return overlay;
	}

	public static void main(String[] args){
		try{
			IOverlay overlay = null;
			if(args[0].equals("node")){
				overlay = getNode(args[1], args[2], NodeType.CHORD, null);
			} else if(args[0].equals("synapse")){
				Synapse synapse = (Synapse) getNode("chord", "ControlNetwork", NodeType.SYNAPSE, null);
				for(int i=1 ; i<args.length ; i+=3){
					if(args[i].equals("-a")){
						if(args[i+1].equals("chord")){
							IOverlay o = getNode("chord", args[i+2], NodeType.CHORDPLUGIN, synapse);
							synapse.getNetworks().add(o);
						}
					}
				}
				overlay = synapse;
			}

			// STAND-BY
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("the node is successfully created!");
			while(true){
				System.out.println("1) Publish");
				System.out.println("2) Search");
				System.out.println("3) Quit");
				System.out.print("---> ");
				try{
					System.out.println("\n" + overlay + "\n");
					int chx = Integer.parseInt(input.readLine().trim());
					String key;
					switch(chx){
					case 0 :
						System.out.println("\n" + overlay + "\n"); break;
					case 1 :
						System.out.print("\nkey = ");
						key = input.readLine();
						System.out.print("value = ");
						String value = input.readLine();
						overlay.put(key, value);
						break;
					case 2 :
						System.out.print("\nkey = ");
						key = input.readLine();
						System.out.println("found: " + overlay.get(key));
						break;
					case 3:
						overlay.kill();
					default : break;
					}
					System.out.println("\npress Enter to continue...");
					input.readLine();
				} catch (NumberFormatException e) {} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch(ArrayIndexOutOfBoundsException e){
			System.out.println("invalid parameters!\n" +
					"=> Create node [chord|can] <IDNetwork>\n" +
			"=> Create synapse (-a [chord|can] <IDNetwork>)+");
		}
	}
}
