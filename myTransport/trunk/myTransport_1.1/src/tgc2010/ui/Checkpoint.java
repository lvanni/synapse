package tgc2010.ui;


public class Checkpoint {

	private String address, zipCode, city, hour, minute, minuteM;

	public Checkpoint(String address, String zipCode, String city , int hour, int minute){
		this.address = address;
		this.zipCode = zipCode;
		this.city = city;
		if(hour < 10){
			this.hour = "0" + hour;
		} else {
			this.hour = "" + hour;
		}
		if(minute < 10) {
			this.minute = "0" + minute;
		} else {
			this.minute = "" + minute;
		}
		if(minute<=20){
			minuteM = "10";
		} else if (minute<=40){
			minuteM = "30";
		} else if (minute<=59){
			minuteM = "50";
		}
	}

	public String toString(){
		String address = this.address.equals("") ? "" : "\n\tAddress: " + this.address;
		String zipCode = this.zipCode.equals("") ? "" : "\n\tZip Code: " + this.zipCode;
		String city = "\n\tCity: " + this.city;
		return hour + "h" + minute + ":" + address + zipCode + city;
	}
//
//	/*
//	 * De-serialization of the toString method
//	 */
//	public static String formatToPrint(String inputStr){
//		 // Compile regular expression
//        Pattern pattern = Pattern.compile("\\*");
//
//        // Replace all occurrences of pattern in input
//        Matcher matcher = pattern.matcher(inputStr);
//        return matcher.replaceAll("\n\t");
//	}

	public String formatToKey(){
		return city+hour+minuteM;
	}

	public String getAddress() {
		return address;
	}

	public String getZipCode() {
		return zipCode;
	}

	public String getCity() {
		return city;
	}

	public String getMinuteM() {
		return minuteM;
	}

	public String getHour() {
		return hour;
	}

	public String getMinute() {
		return minute;
	}

}