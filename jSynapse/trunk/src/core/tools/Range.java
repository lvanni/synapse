package core.tools;

import core.protocol.p2p.chord.AbstractChord;

/**
 * Tool class
 * 
 * @author laurent.vanni@sophia.inria.fr - logNet team 2010 - INRIA
 *         Sophia-Antipolis - France
 * 
 */
public class Range {
	
	public static int MAXid = (int) Math.pow(2, AbstractChord.SPACESIZE - 1) - 1;
	public static int MINid = 0;
	
	/**
	 * 
	 * @param id
	 * @param begin
	 * @param end
	 * @return true id is in [begin, end], false otherwise
	 */
	public static boolean inside(int id, int begin, int end) {
		return (begin < end && begin <= id && id <= end)
		|| (begin > end && ((begin <= id && id <= MAXid) || (MINid <= id && id <= end)))
		|| ((begin == end) && (id == begin));
	}
}

