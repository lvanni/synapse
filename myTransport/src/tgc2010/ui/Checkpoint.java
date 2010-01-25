package tgc2010.ui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	/*
	 * Serialization:
	 * The value sended by socket must be format in one line!
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		String address = this.address.equals("") ? "" : "*Address: " + this.address;
		String zipCode = this.zipCode.equals("") ? "" : "*Zip Code: " + this.zipCode;
		String city = "*City: " + this.city + "*";
		return hour + "h" + minute + ":" + address + zipCode + city;
	}

	/*
	 * De-serialization of the toString method
	 */
	public static String formatToPrint(String inputStr){
		 // Compile regular expression
        Pattern pattern = Pattern.compile("\\*");

        // Replace all occurrences of pattern in input
        Matcher matcher = pattern.matcher(inputStr);
        return matcher.replaceAll("\n\t");
	}

	public String formatToKey(){
		return city+hour+minuteM;
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
