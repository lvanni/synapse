package core.synapse;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This class represent the cache of a request in the control network
 * 
 * @author laurent.vanni@sophia.inria.fr - logNet team 2010 - INRIA
 *         Sophia-Antipolis - France
 * 
 */
public class Cache {

	/** The set values of the cache */
	private Set<String> values;

	/**
	 * Default constructor
	 */
	public Cache() {
		this.values = new HashSet<String>();
	}

	/**
	 * 
	 * @return All the values of the cache (concatenate with "****")
	 */
	public synchronized String getValues() {
		String res = "";
		Iterator<String> i = values.iterator();
		while (i.hasNext()) {
			String s = i.next();
			if (s == null || !s.equals("null")) {
				if (i.hasNext()) {
					res += s + "****";
				} else {
					res += s;
				}
			}
		}
		return res;
	}

	/**
	 * Add a value in the cache
	 * 
	 * @param value
	 */
	public synchronized void addValue(String value) {
		values.add(value);
	}

	@Override
	public String toString() {
		String res = "{ ";
		for (String value : values) {
			res += value + ",";
		}
		return res += " }";
	}
}
