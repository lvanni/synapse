package blackbox.core.mytansport;

import core.synapse.AbstractSynapse;

public class MyTransport extends AbstractSynapse {

	public MyTransport(String ip, int port){
		super(ip, port, "MyTransport");
	}
}
