package core.protocols.transport.http;

import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import core.protocols.p2p.Node;
import core.protocols.transport.ITransport;

public class HttpImpl implements ITransport{
	public HttpImpl(){}

	@Override
	public int getPort() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String sendRequest(String message, Node destination) {
		String response = "";
		try {
		    // Construct data
		    String data = URLEncoder.encode("code", "UTF-8") + "=" + URLEncoder.encode(message, "UTF-8");

		    // Send data
		    URL url = new URL("http://" + destination.getIp() + ":" + destination.getPort() + "/requesthandler");
		    URLConnection conn = url.openConnection();
		    conn.setDoOutput(true);
		    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		    wr.write(data);
		    wr.flush();

		    // Get the response
//		    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//		    String line;
//		    while ((line = rd.readLine()) != null) {
//		    	response += line;
//		    }
		    wr.close();
//		    rd.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return response;
	}

	@Override
	public void stopServer() {
		// TODO Auto-generated method stub
	}
}
