package core.protocol.transport.http;

import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import core.protocol.p2p.Node;
import core.protocol.transport.ITransport;

/**
 * This class represent the http transport layer
 * 
 * @author laurent.vanni@sophia.inria.fr - logNet team 2010 - INRIA
 *         Sophia-Antipolis - France
 * 
 */
public class HttpImpl implements ITransport {

	/**
	 * Default contructor
	 */
	public HttpImpl() {
	}

	/**
	 * @see core.protocol.transport.ITransport#getPort()
	 */
	public int getPort() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @see core.protocol.transport.ITransport#sendRequest(String, Node)
	 */
	public String sendRequest(String message, Node destination) {
		String response = "";
		try {
			// Construct data
			String data = URLEncoder.encode("code", "UTF-8") + "="
					+ URLEncoder.encode(message, "UTF-8");

			// Send data
			URL url = new URL("http://" + destination.getIp() + ":"
					+ destination.getPort() + "/requesthandler");
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(conn
					.getOutputStream());
			wr.write(data);
			wr.flush();

			wr.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return response;
	}

	/**
	 * @see core.protocol.transport.ITransport#stopServer()
	 */
	public void stopServer() {
		// TODO Auto-generated method stub
	}
}
