package core.synapse;

import java.util.ArrayList;
import java.util.List;

public class Cache {

	private List<String> values;
	private int ttl;

	public Cache(int ttl){
		this.ttl = ttl;
		this.values = new ArrayList<String>();
	}

	public synchronized String getValue() {
		if(values.size() != 0){
			String value = values.get(0);
			values.remove(value);
			return value;
		} else {
			return "null";
		}
	}

	public synchronized void addValue(String value) {
		values.add(value);
	}

	public synchronized int getTtl() {
		return ttl;
	}

	public synchronized void setTtl(int ttl) {
		this.ttl = ttl;
	}
	
	public String toString(){
		String res = "{ ";
		for(String value : values){
			res += value + ",";
		}
		return res += " } ttl = " + ttl; 
	}
}
