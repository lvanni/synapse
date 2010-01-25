package tgc2010.ui.geoloc;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.swt.browser.Browser;

public class GeoLoc {
	public static void search(Browser browser, String address, String zipcode, String city) {
		String scheme = "http";
		String authority = "maps.google.fr";
		String path = "/maps";
		String query = "f=q&hl=fr&q=" + address+ " , " + zipcode + " " + city ;
		try {
			URI uri = new URI(scheme, authority, path, query, null);
			browser.setUrl(uri.toASCIIString());
		} catch (URISyntaxException ex) {
			ex.printStackTrace();
		}
	}
}
