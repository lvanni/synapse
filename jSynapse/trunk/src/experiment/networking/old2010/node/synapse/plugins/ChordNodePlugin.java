package experiment.networking.old2010.node.synapse.plugins;

import core.protocol.p2p.chord.IChord;
import experiment.networking.old2010.node.ChordNode;
import experiment.networking.old2010.node.synapse.Synapse;

public class ChordNodePlugin extends ChordNode{

	private Synapse synapse;

	public ChordNodePlugin(String host, int port, Synapse synapse, String overlayIntifier) {
		super(host, port, overlayIntifier);
		this.synapse = synapse;
	}

	public ChordNodePlugin(String host, int port, Synapse synapse) {
		super(host, port);
		this.synapse = synapse;
	}

	public String handleRequest(String code){
		String[] args = code.split(",");
		String result = "";
		if(args[0].equals(overlayIntifier)){
			int f = Integer.parseInt(args[1]);
			switch(f){
			case IChord.PUT :
				// get back the clean key
				String cleanKey = synapse.getInCleanTable(args[2]+overlayIntifier);
				if(cleanKey != null && !cleanKey.equals("null") && !cleanKey.equals("")){ // it's a synapse request
					String[] keys = cleanKey.split(":");
					cleanKey = keys[1];
//					System.out.println("PUT: clean key found: " + cleanKey + " \nsynapse routing begin...");
					synapse.put(cleanKey, args[3]);
				} else {
//					System.out.println("PUT: unknown key, no synapse routing...");
					super.handleRequest(code);
				}
				break;
			case IChord.GET :
				// get back the clean key
				cleanKey = synapse.getInCleanTable(args[2]+overlayIntifier);
				if(cleanKey != null && !cleanKey.equals("null") && !cleanKey.equals("")){ // it's a synapse request
					String[] keys = cleanKey.split(":");
					cleanKey = keys[1];
//					System.out.println("GET: clean key found: " + cleanKey + " \nsynapse routing begin...");
					result = synapse.get(cleanKey);
				} else {
//					System.out.println("GET: unknown key, no synapse routing...");
					result = super.handleRequest(code);
				}
				break;
			default:
				result = super.handleRequest(code);
				break;
			}
		} else {
			result = super.handleRequest(code);
		}
		return result;
	}
}
