package experiment.networking.current.node.synapse.plugin.chord;

import core.protocol.p2p.chord.IChord;
import core.protocol.transport.ITransport;
import experiment.networking.current.node.chord.ChordNode;
import experiment.networking.current.node.synapse.Synapse;

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
	 * 
	 * @param host
	 * @param port
	 * @param synapse
	 * @param overlayIntifier
	 */
	public ChordNodePlugin(String host, int port, Synapse synapse,
			String overlayIntifier) {
		this(host, port, synapse, overlayIntifier, null);
	}
	
	/**
	 * Constructor
	 * 
	 * @param host
	 * @param port
	 * @param synapse
	 * @param overlayIntifier
	 */
	public ChordNodePlugin(String host, int port, Synapse synapse,
			String overlayIntifier, ITransport transport) {
		super(host, port, overlayIntifier, transport);
		this.synapse = synapse;
	}
	
	/**
	 * @see core.protocol.transport.IRequestHandler#handleRequest(String)
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
