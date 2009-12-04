package core.experiments.synapse.plugins;

import core.experiments.nodes.ChordNode;
import core.experiments.synapse.Synapse;
import core.protocols.p2p.chord.IChord;

public class ChordNodePlugin extends ChordNode{

	private Synapse synapse;

	public ChordNodePlugin(String host, int port, Synapse synapse, String overlayIntifier) {
		super(host, port, overlayIntifier);
		this.synapse = synapse;
	}

	public String doStuff(String code){
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
					System.out.println(code);
					cleanKey = keys[1];
					synapse.put(cleanKey, args[3]);
				} else {
					super.doStuff(code);
				}
				break;
			case IChord.GET :
				// get back the clean key
				cleanKey = synapse.getCleanKey(args[2]+overlayIntifier);
				if(cleanKey != null && !cleanKey.equals("null") && !cleanKey.equals("")){ // it's a synapse request
					String[] keys = cleanKey.split(":");
					cleanKey = keys[1];
					result = synapse.get(cleanKey);
				} else {
					result = super.doStuff(code);
				}
				break;
			default:
				result = super.doStuff(code);
				break;
			}
		} else {
			// OPERATION
			if(args[0].equals("put")){
				put(args[1], args[2]);
			} else if(args[0].equals("get")){
				return get(args[1]);
			}
		}
		return result;
	}
}
