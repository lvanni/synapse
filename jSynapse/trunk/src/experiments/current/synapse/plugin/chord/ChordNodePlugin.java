package experiments.current.synapse.plugin.chord;

import core.protocols.p2p.chord.IChord;
import experiments.current.node.chord.ChordNode;
import experiments.current.synapse.Synapse;

/**
 * This is an implementation of a chordPlugin node for a synapse
 * 
 * @author laurent.vanni@sophia.inria.fr - logNet team 2010 - INRIA
 *         Sophia-Antipolis - France
 * 
 */
public class ChordNodePlugin extends ChordNode {

	/** the synapse associate with this plugin */
	private Synapse synapse;

	/**
	 * Constructor
	 * 
	 * @param host
	 * @param port
	 * @param synapse
	 * @param overlayIntifier
	 */
	public ChordNodePlugin(String host, int port, Synapse synapse,
			String overlayIntifier) {
		super(host, port, overlayIntifier);
		this.synapse = synapse;
	}

	/**
	 * Constructor
	 * 
	 * @param host
	 * @param port
	 * @param synapse
	 */
	public ChordNodePlugin(String host, int port, Synapse synapse) {
		super(host, port);
		this.synapse = synapse;
	}

	/**
	 * @see core.protocols.transport.IRequestHandler#handleRequest(String)
	 */
	public String handleRequest(String code) {
		String[] args = code.split(",");
		String result = "";
		if (args[0].equals(overlayIntifier)) {
			int f = Integer.parseInt(args[1]);
			switch (f) {
			case IChord.GET:
				String cleanKey = synapse.getInCleanTable(args[2] + "|"
						+ overlayIntifier);
//				String cleanKey = synapse.getInCleanTable(args[2]);
				if (cleanKey != null && !cleanKey.equals("null")
						&& !cleanKey.equals("")) {
//					System.out.println("CleanKey found!\t" + args[2] + " => " + cleanKey);
					if (synapse.cacheTableExist(cleanKey).equals("1")) {
						// THEN SYNAPSE AND USE THE CACHE TABLE
						synapse.synapseGet(cleanKey, overlayIntifier);
//						System.out.println("clean key found: " +  args[2] + " => " + cleanKey);
					}
				} else {
					System.out.println("CleanKey not found!\t" + args[2]);
				}
				// IN ALL CASES CONITNUE TO ROUTE
				result = super.handleRequest(code);
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
