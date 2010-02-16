package tracker.core;

/**
 * This data structure represent an invitation
 * 
 * @author laurent.vanni@sophia.inria.fr - logNet team 2010 - INRIA
 *         Sophia-Antipolis - France
 * 
 */
public class Invitation {

	/** The id of the network to join */
	private String networkID;
	/** The password to join */
	private String accessPass;

	/**
	 * Default constructor
	 * 
	 * @param networkID
	 * @param accessPass
	 */
	public Invitation(String networkID, String accessPass) {
		this.networkID = networkID;
		this.accessPass = accessPass;
	}

	/**
	 * 
	 * @return the networkID of the invitation
	 */
	public String getNetworkID() {
		return networkID;
	}

	/**
	 * 
	 * @return the password of the invitation
	 */
	public String getAccessPass() {
		return accessPass;
	}
	
	@Override
	public boolean equals(Object obj) {
		Invitation i = (Invitation) obj;
		return i.getNetworkID().equals(networkID) && i.getAccessPass().equals(accessPass);
	}
}
