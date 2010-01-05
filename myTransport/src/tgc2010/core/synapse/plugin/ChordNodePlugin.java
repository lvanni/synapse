package tgc2010.core.synapse.plugin;

import tgc2010.core.node.ChordNode;
import tgc2010.core.synapse.Synapse;
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

	public String handleRequest(String code){
		String[] args = code.split(",");
		String result = "";
		if(args[0].equals(overlayIntifier)){
			int f = Integer.parseInt(args[1]);
			switch(f){
			case IChord.GET :
				String cleanKey = synapse.getInCleanTable(args[2]+"|"+overlayIntifier);
				if(cleanKey != null && !cleanKey.equals("null") && !cleanKey.equals("")){
					result = synapse.get(cleanKey);
				} else {
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
