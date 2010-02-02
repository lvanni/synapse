package tracker.api;

public interface TrackerAPI {
	
	/** Address of tracker */
	public final static String 	TRACKER_HOST  		= "localhost";
	public final static int		TRACKER_PORT  		= 8000;
	
	/** Code for the transport */
	public final static int ADDNODE  		= 0;
	public final static int REMOVENODE 		= 1;
	public final static int GETCONNECTION  	= 2;
	public final static int JOIN			= 3;
}
