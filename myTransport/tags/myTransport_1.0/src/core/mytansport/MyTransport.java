package core.mytansport;

import core.synapse.AbstractSynapse;

public class MyTransport extends AbstractSynapse{

//	public static enum OverlayID{MYCONCERT, MYFOOT, MYJCHORD1, MYJCHORD2}

	// /////////////////////////////////////////// //
	//                 CONSTRUCTOR                 //
	// /////////////////////////////////////////// //
	public MyTransport(String ip, int port){
		super(ip, port, "MyTransport");
	}

	// /////////////////////////////////////////// //
	//                SYNAPSE ALGO                 //
	// /////////////////////////////////////////// //
//	public void join(OverlayID o, int port, String hostToJoin, int portToJoin) {
//		IOverlay overlay = null;
//		switch (o) {
//		case MYCONCERT:
//			overlay = new MyConcert(getThisNode().getIp(), port, this);
//			break;
//		case MYFOOT:
//			overlay = new MyFoot(getThisNode().getIp(), port, this);
//			break;
//		default : break;
//		}
//		System.out.print("\nStarting a new " + o + " service... ");
//		new Thread((Runnable)overlay).start();
//		do{
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		} while(overlay.getTransport() == null);
//		System.out.println("ok!");
//		((IChord) overlay).join(hostToJoin, portToJoin);
//		getNetworks().add(overlay);
//	}
}
