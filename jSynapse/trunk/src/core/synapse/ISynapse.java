package core.synapse;

public interface ISynapse {
	
	public final static int PUTCleanKey	 	= 10;
	public final static int GETCleanKey	 	= 11;
		
	public final static int INITCacheTable	= 12;
	public final static int CacheTableExist	= 13;
	public final static int REMOVECacheTable= 14;
	
	public final static int ADDCacheValue	= 30;
	public final static int GETCacheValue	= 31;
	
	public void invite();
	public void join(String host, int port);

}
