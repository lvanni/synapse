package core.synapse;

public interface ISynapse {
	
	public final static int PUTClean	 = 10;
	public final static int PUTCache	 = 11;
	
	public final static int GETClean	 = 20;
	public final static int GETCache	 = 21;
	
	public void invite();
	public void join(String host, int port);

}
