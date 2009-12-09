package core.experiments.next.synapse.plugins;


import core.experiments.next.nodes.ChordNode;
import core.experiments.next.synapse.Synapse;
import core.protocols.p2p.chord.IChord;

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

	public String doStuff(String code){
		System.out.println("\tdoStuff: " + code);
		String[] args = code.split(",");
		String result = "";
		if(args[0].equals(overlayIntifier)){
			int f = Integer.parseInt(args[1]);
			switch(f){
			case IChord.PUT :
				// get back the clean key
				String cleanKey = synapse.getCleanKey(args[2]+overlayIntifier);
				if(cleanKey != null && !cleanKey.equals("null") && !cleanKey.equals("")){ // it's a synapse request
					String[] keys = cleanKey.split(":");
					cleanKey = keys[1];
//					System.out.println("PUT: clean key found: " + cleanKey + " \nsynapse routing begin...");
					synapse.put(cleanKey, args[3]);
				} else {
//					System.out.println("PUT: unknown key, no synapse routing...");
					super.doStuff(code);
				}
				break;
			case IChord.GET :
				// get back the clean key
				cleanKey = synapse.getCleanKey(args[2]+overlayIntifier);
				if(cleanKey != null && !cleanKey.equals("null") && !cleanKey.equals("")){ // it's a synapse request
					String[] keys = cleanKey.split(":");
					cleanKey = keys[1];
//					System.out.println("GET: clean key found: " + cleanKey + " \nsynapse routing begin...");
					result = synapse.get(cleanKey);
				} else {
//					System.out.println("GET: unknown key, no synapse routing...");
					result = super.doStuff(code);
				}
				break;
			default:
				result = super.doStuff(code);
				break;
			}
		} else {
			result = super.doStuff(code);
		}
		return result;
	}
}
