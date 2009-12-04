package core.experiments;

import core.experiments.nodes.ChordNode;
import core.experiments.synapse.Synapse;
import core.experiments.synapse.plugins.ChordNodePlugin;
import core.experiments.tools.ITracker;
import core.experiments.tools.InfoConsole;
import core.protocols.p2p.IOverlay;
import core.protocols.p2p.Node;
import core.protocols.p2p.chord.AbstractChord;

public class Create {

	/** address on the tracker which give the peerSet*/
	private static String TRACKER_HOST = "smart5.inria.fr";
	private static int 	  TRACKER_PORT = 8000;

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
				node = new Synapse(ip, 0, id);
				break;
			}
			new Thread((Runnable) node).start();
			do{
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} while(node.getTransport() == null);

			// CONNECT ON TRACKER
			String trackerResponse = node.getTransport().forward(ITracker.GETCONNECTION + "," + node.getIdentifier(), new Node(TRACKER_HOST, 0, TRACKER_PORT));
			node.getTransport().forward(ITracker.ADDNODE + "," + node.getIdentifier() + "," + node.getThisNode(), new Node(TRACKER_HOST, 0, TRACKER_PORT));
			if(!trackerResponse.equals("null")) {
				Node n = new Node(trackerResponse);
				node.join(n.getIp(), n.getPort());
			}
			overlay = node;
		}
		return overlay;
	}

	public static void main(String[] args){
		try{
			if(args[0].equals("node")){
				getNode(args[1], args[2], NodeType.CHORD, null);
			}
			else if(args[0].equals("synapse")){
				Synapse synapse = (Synapse) getNode("chord", "ControlNetwork", NodeType.SYNAPSE, null);
				for(int i=1 ; i<args.length ; i+=2){
					IOverlay o = getNode(args[i], args[i+1], NodeType.CHORDPLUGIN, synapse);
					synapse.getNetworks().add(o);
				}
			}
		} catch(ArrayIndexOutOfBoundsException e){
			System.out.println("invalid parameters!\n=> Create node [chord|can] <IDNetwork>\n=> Create synapse ([chord|can] <IDNetwork>)+");
		}
	}
}
