package core.protocols.p2p;

import core.protocols.transport.ITransport;

public interface IOverlay{
	public String getIdentifier();
	public int keyToH(String key);
	public ITransport getTransport();
	public Node getThisNode();
	
	public void put(String key, String value);
	public String get(String key);
	public void join(String host, int port);
	public void kill();

	public String doStuff(String code);
}