package it.silence.server.http;

import it.silence.server.Protocol;
import it.silence.server.Server;
import it.silence.server.listener.ConnectionListeningService;
import it.silence.server.listener.PacketListeningService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpServer extends Server {

	public HttpServer(int port) {
		super(port);
	}
	
	public void sendHeaders(Socket client) {
		if(!client.isClosed()) {
			try {
				PrintWriter out = new PrintWriter(client.getOutputStream(), true);
			    out.println("HTTP/1.1 200 OK");
			    out.println("Content-Type: text/html");
			    out.print("\r\n");
			    out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void sendContent(Socket client, String content) {
		if(!client.isClosed()) {
			try {
				PrintWriter out = new PrintWriter(client.getOutputStream(), true);
			    out.print(content);
			    out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	    
	}
	
	private static final String fallbackDefault = "index.html";
	
	public String fallbackLocation = fallbackDefault;
	
	private static final Pattern httpRequest = 
			Pattern.compile("(GET|POST|DELETE|PUT) \\/(.*?) (HTTPS?)\\/([0-9]{1}\\.[0-9]{1})");
	
	public HttpRequest getRequest(Socket client) {
		try {
			if(client == null || client.isClosed() || !isRunning())
				return null;
			String s;
			try {
				BufferedReader r = new BufferedReader(new InputStreamReader(client.getInputStream()));
				s = r.readLine();
			} catch(Exception e) {
				return null;
			}
			if(s == null)
				return null;
			Matcher m = httpRequest.matcher(s);
			if(m.matches()) {
				if(DEBUG == DebugLevel.INFO || DEBUG == DebugLevel.EVERYTHING)
					System.err.println("(HTTPClient) " + s);
				String location = m.group(2).trim();
				if(location.isEmpty())
					return new HttpRequest(client.getInetAddress(), HttpMethod.valueOf(m.group(1)), 
							fallbackLocation, Protocol.valueOf(m.group(3)), m.group(4));
				
				return new HttpRequest(client.getInetAddress(), HttpMethod.valueOf(m.group(1)), 
						location, Protocol.valueOf(m.group(3)), m.group(4));
			}
			if(DEBUG == DebugLevel.EVERYTHING)
				System.err.println(s + ": Client is not using HTTP.");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public ConnectionListeningService<HttpServer> createConnectionListener() {
		return new ConnectionListeningService<HttpServer>(this);
	}
	
	public ConnectionListeningService<HttpServer> createConnectionListener(long listeningDelay) {
		return new ConnectionListeningService<HttpServer>(this, listeningDelay);
	}
	
	public PacketListeningService<HttpServer> createPacketListener() {
		return new PacketListeningService<HttpServer>(this);
	}
	
	public PacketListeningService<HttpServer> createPacketListener(long listeningDelay) {
		return new PacketListeningService<HttpServer>(this, listeningDelay);
	}
	
}
