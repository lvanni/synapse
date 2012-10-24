package experiment.networking.old2010.synapse;

import core.protocol.p2p.synapse.AbstractSynapse;

public class Synapse extends AbstractSynapse{

	// /////////////////////////////////////////// //
	//                 CONSTRUCTOR                 //
	// /////////////////////////////////////////// //
	public Synapse(String ip, int port){
		super(ip, port, "synapse");
	}
}
