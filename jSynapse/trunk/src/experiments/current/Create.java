package experiments.current;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import core.ITracker;
import core.protocols.p2p.IDHT;
import core.protocols.p2p.Node;
import core.protocols.p2p.chord.AbstractChord;
import core.tools.InfoConsole;
import experiments.current.node.chord.ChordNode;
import experiments.current.node.kademlia.KadNode;
import experiments.current.synapse.Synapse;
import experiments.current.synapse.plugin.chord.ChordNodePlugin;
import experiments.current.synapse.plugin.kademlia.KadNodePlugin;

/**
 * This a very simple User Interface to allow to create Synapse, node and
 * plugin. And test the DHT functionality
 * 
 * @author laurent.vanni@sophia.inria.fr - logNet team 2010 - INRIA
 *         Sophia-Antipolis - France
 * 
 */
public class Create {

	/**
	 * 
	 */
	private static enum NodeType {
		CHORD, CHORDPLUGIN, KAD, KADPLUGIN, SYNAPSE
	}

	/**
	 * Instantiate a node of the type NodeType
	 * 
	 * @param protocol
	 * @param id
	 * @param t
	 * @param s
	 * @return the node created
	 */
	public static IDHT getNode(String protocol, String id, NodeType t, Synapse s) {
		IDHT overlay = null;
		String ip = InfoConsole.getIp();
		if (protocol.equals("chord")) {
			AbstractChord node = null;
			switch (t) {
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
			overlay = node;
		} else if (protocol.equals("kad")) {
			switch (t) {
			case KAD:
				overlay = new KadNode(id);
				break;
			case KADPLUGIN:
				overlay = new KadNodePlugin(id, s);
				break;
			}
		}

		// CONNECT ON TRACKER
		Node tracker = new Node(ITracker.TRACKER_HOST, 0, ITracker.TRACKER_PORT);
		String trackerResponse = overlay.getTransport()
		.sendRequest(
				ITracker.GETCONNECTION + "," + overlay.getIdentifier(),
				tracker);
		overlay.getTransport().sendRequest(
				ITracker.ADDNODE + "," + overlay.getIdentifier() + ","
				+ overlay.getThisNode(), tracker);
		if (!trackerResponse.equals("null")) {
			Node n = new Node(trackerResponse);
			overlay.join(n.getIp(), n.getPort());
		}
		return overlay;
	}

	/**
	 * Main function to launch the application
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			IDHT overlay = null;
			if (args[0].equals("node")) {
				if(args[1].equals("chord")) {
					overlay = getNode(args[1], args[2], NodeType.CHORD, null);
				} else { 
					overlay = getNode(args[1], args[2], NodeType.KAD, null);
				}
			} else if (args[0].equals("synapse")) {
				Synapse synapse = (Synapse) getNode("chord", "ControlNetwork",
						NodeType.SYNAPSE, null);
				for (int i = 1; i < args.length; i += 3) {
					if (args[i].equals("-a")) {
						if (args[i + 1].equals("chord")) {
							IDHT o = getNode("chord", args[i + 2],
									NodeType.CHORDPLUGIN, synapse);
							synapse.getNetworks().add(o);
						} else if (args[i + 1].equals("kad")) {
							IDHT o = getNode("kad", args[i + 2],
									NodeType.KADPLUGIN, synapse);
							synapse.getNetworks().add(o);
						}
					} 
				}
				overlay = synapse;
			}

			// STAND-BY
			BufferedReader input = new BufferedReader(new InputStreamReader(
					System.in));
			while (true) {
				System.out.println("0) Status");
				System.out.println("1) Publish");
				System.out.println("2) Search");
				System.out.println("3) Quit");
				System.out.print("---> ");
				try {
					int chx = Integer.parseInt(input.readLine().trim());
					String key;
					switch (chx) {
					case 0:
						System.out.println("\n" + overlay + "\n");
						break;
					case 1:
						System.out.print("\nkey = ");
						key = input.readLine();
						System.out.print("value = ");
						String value = input.readLine();
						overlay.put(key, value);
						break;
					case 2:
						System.out.print("\nkey = ");
						key = input.readLine();
						String found = "";
						if (overlay instanceof Synapse) {
							found = ((Synapse) overlay).get(key);
						} else {
							found = overlay.get(key);
						}
						System.out.println("found: " + found);
						break;
					case 3:
						if (overlay instanceof Synapse) {
							for (IDHT o : ((Synapse) overlay).getNetworks()) {
								overlay.getTransport().sendRequest(
										ITracker.REMOVENODE + ","
										+ o.getIdentifier() + ","
										+ o.getThisNode(),
										new Node(ITracker.TRACKER_HOST, 0,
												ITracker.TRACKER_PORT));
							}

						}
						overlay.getTransport().sendRequest(
								ITracker.REMOVENODE + ","
								+ overlay.getIdentifier() + ","
								+ overlay.getThisNode(),
								new Node(ITracker.TRACKER_HOST, 0,
										ITracker.TRACKER_PORT));
						overlay.kill();
					default:
						break;
					}
					System.out.println("\npress Enter to continue...");
					input.readLine();
				} catch (NumberFormatException e) {
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("invalid parameters!\n"
					+ "=> Create node [chord|kad] <IDNetwork>\n"
					+ "=> Create synapse (-a [chord|kad] <IDNetwork>)+");
		}
	}
}
