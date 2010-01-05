package core;

public interface ITracker {
	
	/** Address of tracker */
	public final static String HOST  		= "smart5.inria.fr";
	public final static int PORT  			= 8000;
	
	/** Code for the transport */
	public final static int ADDNODE  		= 0;
	public final static int REMOVENODE 		= 1;
	public final static int GETCONNECTION  	= 2;
}
