package core.tools;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashFunction{

	private int MaxInt = Range.MAXid;
	private String epsilon;
	
	public HashFunction(String epsilon){
		this.epsilon = epsilon;
	}

	private String convertToHex(byte[] data) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				if ((0 <= halfbyte) && (halfbyte <= 9))
					buf.append((char) ('0' + halfbyte));
				else
					buf.append((char) ('a' + (halfbyte - 10)));
				halfbyte = data[i] & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf.toString();
	}

	public String SHA1ToString(String text) {
		MessageDigest md;
		byte[] sha1hash = new byte[40];
		try {
			md = MessageDigest.getInstance("SHA-1");
			md.update(text.getBytes("iso-8859-1"), 0, text.length());
			sha1hash = md.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return convertToHex(sha1hash);
	}

	public int convertHexToInt(String hex){
		return Integer.valueOf(hex, 16).intValue();
	}

	public int _SHA1ToInt(String text) {
		String sha1 = SHA1ToString(text+epsilon);
		int res = 0;
		for(int i=0 ; i<sha1.length() ; i++){
			int pow = (int) (Math.pow(16, i) % MaxInt);
			switch(sha1.charAt(i)){
			case '0' : break;
			case '1' : res += pow;   break;
			case '2' : res += 2*pow; break;
			case '3' : res += 3*pow; break;
			case '4' : res += 4*pow; break;
			case '5' : res += 5*pow; break;
			case '6' : res += 6*pow; break;
			case '7' : res += 7*pow; break;
			case '8' : res += 8*pow; break;
			case '9' : res += 9*pow; break;
			case 'a' : res += 10*pow; break;
			case 'b' : res += 11*pow; break;
			case 'c' : res += 12*pow; break;
			case 'd' : res += 13*pow; break;
			case 'e' : res += 14*pow; break;
			case 'f' : res += 15*pow; break;
			}
			res %= MaxInt;
		}
		return res;
	}
	
	public int SHA1ToInt(String text){
		try{
			return Integer.parseInt(text);
		} catch(NumberFormatException e){
			return _SHA1ToInt(text);
		}
	}
}
