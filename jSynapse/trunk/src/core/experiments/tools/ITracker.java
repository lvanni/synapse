package core.experiments.tools;

public interface ITracker {
	/** address on the tracker which give the peerSet*/
	public final static String 	  TRACKER_HOST = "smart5.inria.fr";
	public final static int 	  TRACKER_PORT = 8000;

	/** Code for the transport*/
	public final static int ADDNODE  		= 0;
	public final static int REMOVENODE 		= 1;
	public final static int GETCONNECTION  	= 2;
}
