package core;

public class Invitation {
	
	private String networkID;
	private String accessPass;
	
	public Invitation(String networkID, String accessPass) {
		this.networkID = networkID;
		this.accessPass = accessPass;
	}

	public String getNetworkID() {
		return networkID;
	}

	public String getAccessPass() {
		return accessPass;
	}
}
