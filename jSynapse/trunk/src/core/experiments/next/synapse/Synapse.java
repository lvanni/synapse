package core.experiments.next.synapse;

import core.synapse.AbstractSynapse;

public class Synapse extends AbstractSynapse{

	// /////////////////////////////////////////// //
	//                 CONSTRUCTOR                 //
	// /////////////////////////////////////////// //
	public Synapse(String ip, int port){
		super(ip, port, "synapse");
	}
}
