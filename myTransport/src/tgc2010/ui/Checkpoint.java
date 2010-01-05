package tgc2010.ui;

public class Checkpoint {
	
	private String location, hour, minute, minuteM;
	
	public Checkpoint(String location, int hour, int minute){
		this.location = location;
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
		System.out.println("minuteM = " + minuteM);
	}
	
	public String toString(){
		return ". " + hour + "h" + minute + "\t\t" + location;
	}
	
	public String formatTokey(){
		return location+hour+minuteM;
	}
	
	public String getMinuteM() {
		return minuteM;
	}
	
	public String getLocation() {
		return location;
	}

	public String getHour() {
		return hour;
	}

	public String getMinute() {
		return minute;
	}

}
