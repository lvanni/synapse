package core.synapse;


public interface ISynapse {
	/** Code for synapse*/
	public final static int OPE    = 3;
	public final static int FIND   = 4; // FIND,<My???>,<id>
	public final static int FOUND  = 5;
	public final static int INVITE = 6;
	public final static int JOIN   = 7;
	
	public void invite();
	public void join(String host, int port);
}
