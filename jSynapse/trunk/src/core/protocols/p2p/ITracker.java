package core.protocols.p2p;

public interface ITracker {
	/** Code for the transport*/
	public final static int ADDNODE  		= 0;
	public final static int REMOVENODE 		= 1;
	public final static int GETCONNECTION  	= 2;
}
