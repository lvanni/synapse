package core.synapse;

public interface ISynapse {
	
	public final static int GETCleanKey	 	= 10;
	public final static int PUTCleanKey	 	= 11;
	
	public final static int INITCacheTable	= 12;
	
	public final static int GETTTL			= 20;
	public final static int SETTTL			= 21;
	
	public final static int GETCacheValue	= 30;
	public final static int ADDCacheValue	= 31;
	
	public final static int DEFAULT_TTL		= 3; 
		
	public void invite();
	public void join(String host, int port);

}
