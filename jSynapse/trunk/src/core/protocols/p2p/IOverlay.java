package core.protocols.p2p;

import core.protocols.transport.ITransport;

public interface IOverlay {
	public String getIdentifier();
	public int keyToH(String key);
	public ITransport getTransport();
	
	public void put(String key, String value);
	public String get(String key);
}