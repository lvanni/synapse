package core.experiments.synapse;

import core.experiments.synapse.plugins.ChordNodePlugin;
import core.protocols.p2p.IOverlay;
import core.protocols.p2p.chord.IChord;
import core.synapse.AbstractSynapse;

public class Synapse extends AbstractSynapse{

	public final static int CHORD = 1;
	public final static int CAN = 2;

	// /////////////////////////////////////////// //
	//                 CONSTRUCTOR                 //
	// /////////////////////////////////////////// //
	public Synapse(String ip, int port, String identifier){
		super(ip, port, identifier);
	}

	// /////////////////////////////////////////// //
	//                SYNAPSE ALGO                 //
	// /////////////////////////////////////////// //
	public void join(int protocol, int port, String hostToJoin, int portToJoin, String overlayIntifier) {
		IOverlay overlay = null;
		switch (protocol) {
		case CHORD:
			overlay = new ChordNodePlugin(getThisNode().getIp(), port, this, overlayIntifier);
			break;
		case CAN: // no yet implemented...
			break;
		default : break;
		}
		new Thread((Runnable)overlay).start();
		do{
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while(overlay.getTransport() == null);
		((IChord) overlay).join(hostToJoin, portToJoin);
		getNetworks().add(overlay);
	}
}
