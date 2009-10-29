package core.tools;

import core.protocols.p2p.chord.AbstractChord;

public class Range {
	
	public static int MAXid = (int) Math.pow(2, AbstractChord.SPACESIZE - 1) - 1;
	public static int MINid = 0;
	
	public static boolean inside(int id, int begin, int end) {
		return (begin < end && begin <= id && id <= end)
		|| (begin > end && ((begin <= id && id <= MAXid) || (MINid <= id && id <= end)))
		|| ((begin == end) && (id == begin));
	}
}

