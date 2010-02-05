package tgc2010.ui;

/**
 * This class represent a checkpoint in a roadTrip
 * 
 * @author laurent.vanni@sophia.inria.fr - logNet team 2010 - INRIA
 *         Sophia-Antipolis - France
 * 
 */
public class Checkpoint {

	/** parameters of a checkpoint */
	private String address, zipCode, city, hour, minute, minuteM;

	/**
	 * Default constructor
	 * 
	 * @param address
	 * @param zipCode
	 * @param city
	 * @param hour
	 * @param minute
	 */
	public Checkpoint(String address, String zipCode, String city, int hour,
			int minute) {
		this.address = address;
		this.zipCode = zipCode;
		this.city = city;
		if (hour < 10) {
			this.hour = "0" + hour;
		} else {
			this.hour = "" + hour;
		}
		if (minute < 10) {
			this.minute = "0" + minute;
		} else {
			this.minute = "" + minute;
		}
		if (minute <= 20) {
			minuteM = "10";
		} else if (minute <= 40) {
			minuteM = "30";
		} else if (minute <= 59) {
			minuteM = "50";
		}
	}

	@Override
	public String toString() {
		String address = this.address.equals("") ? "" : "\n\tAddress: "
				+ this.address;
		String zipCode = this.zipCode.equals("") ? "" : "\n\tZip Code: "
				+ this.zipCode;
		String city = "\n\tCity: " + this.city;
		return hour + "h" + minute + ":" + address + zipCode + city;
	}

	/**
	 * Format a checkpoint into a key for a DHT
	 * 
	 * @return the key formated
	 */
	public String formatToKey() {
		return city + hour + minuteM;
	}

	/**
	 * 
	 * @return the address of the checkpoint
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * 
	 * @return the zip code of the checkpoint
	 */
	public String getZipCode() {
		return zipCode;
	}

	/**
	 * 
	 * @return the city of the checkpoint
	 */
	public String getCity() {
		return city;
	}

	/**
	 * 
	 * @return the time of the checkpoint (rounded minute)
	 */
	public String getMinuteM() {
		return minuteM;
	}

	/**
	 * 
	 * @return the time of the checkpoint (hour)
	 */
	public String getHour() {
		return hour;
	}

	/**
	 * 
	 * @return the time of the checkpoint (real minute)
	 */
	public String getMinute() {
		return minute;
	}

}
