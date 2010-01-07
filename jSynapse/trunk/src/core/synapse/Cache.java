package core.synapse;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Cache {

	private Set<String> values;
	public Cache(){
		this.values = new HashSet<String>();
	}

	public synchronized String getValues() {
		String res = "";
		Iterator<String> i = values.iterator();
		while(i.hasNext()){
			String s = i.next();
			if(s == null || !s.equals("null")){
				if(i.hasNext()){
					res += s + "****";
				} else {
					res += s;
				}
			}
		}
		return res;
	}


	public synchronized void addValue(String value) {
		values.add(value);
	}

	public String toString(){
		String res = "{ ";
		for(String value : values){
			res += value + ",";
		}
		return res += " }"; 
	}
}
