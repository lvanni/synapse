package core.experiments.next;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import core.ITracker;
import core.experiments.next.node.ChordNode;
import core.experiments.next.synapse.Synapse;
import core.experiments.next.synapse.plugin.ChordNodePlugin;
import core.experiments.tools.InfoConsole;
import core.protocols.p2p.IOverlay;
import core.protocols.p2p.Node;
import core.protocols.p2p.chord.AbstractChord;

public class Create {

	private static enum NodeType{CHORD, CHORDPLUGIN, SYNAPSE}

	public static IOverlay getNode(String protocol, String id, NodeType t, Synapse s){
		IOverlay overlay = null;
		String ip = InfoConsole.getIp();
		if(protocol.equals("chord")){
			AbstractChord node = null;
			switch(t){
			case CHORD:
				node = new ChordNode(ip, 0, id);
				break;
			case CHORDPLUGIN:
				node = new ChordNodePlugin(ip, 0, s, id);
				break;
			case SYNAPSE:
				node = new Synapse(ip, 0);
				break;
			}
//			new Thread((Runnable) node).start();
			Thread.yield();
			overlay = node;
		}

		// CONNECT ON TRACKER
		Node tracker = new Node(ITracker.TRACKER_HOST, 0, ITracker.TRACKER_PORT);
		String trackerResponse = overlay.getTransport().sendRequest(ITracker.GETCONNECTION + "," + overlay.getIdentifier(), tracker);
		overlay.getTransport().sendRequest(ITracker.ADDNODE + "," + overlay.getIdentifier() + "," + overlay.getThisNode(), tracker);
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
			while(true){
				System.out.println("1) Publish");
				System.out.println("2) Search");
				System.out.println("3) Quit");
				System.out.print("---> ");
				try{
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
						String found = "";
						if(overlay instanceof Synapse){
							found = ((Synapse)overlay).get(key, 3); // 3 = TTL
						} else {
							found = overlay.get(key);
						}
						System.out.println("found: " + found);
						break;
					case 3:
						if(overlay instanceof Synapse){
							for(IOverlay o : ((Synapse) overlay).getNetworks()){
								overlay.getTransport().sendRequest(ITracker.REMOVENODE + "," + o.getIdentifier() + "," + o.getThisNode(), new Node(ITracker.TRACKER_HOST, 0, ITracker.TRACKER_PORT));
							}
							
						}
						overlay.getTransport().sendRequest(ITracker.REMOVENODE + "," + overlay.getIdentifier() + "," + overlay.getThisNode(), new Node(ITracker.TRACKER_HOST, 0, ITracker.TRACKER_PORT));
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
