package it.silence.server.listener.handler;

import it.silence.client.packet.PacketMessage;
import it.silence.html.HtmlFetcher;
import it.silence.server.Server;
import it.silence.server.Server.DebugLevel;
import it.silence.server.http.HttpRequest;
import it.silence.server.http.HttpServer;

import java.net.Socket;

public final class Handlers {

	private Handlers() {}
	
	private static final HtmlFetcher html = HtmlFetcher.getFetcher();
	
	public static final ConnectionHandler<HttpServer> HTTP_DEFAULT_HANDLER =
		new ConnectionHandler<HttpServer>() {
			public void onConnect(HttpServer server, Socket client) {
				if(server == null || client == null || !server.isRunning() || client.isClosed())
					return;
				HttpRequest req = server.getRequest(client);
				if(req == null || !req.isValid())
					return;
				if(Server.DEBUG == DebugLevel.EVERYTHING)
					System.err.println("Fetching " + req.getRequestedPage() + "...");
				server.sendHeaders(client);
				server.sendContent(client, html.fetch(req));
				server.disconnectClient(client);
			}
		};
		
	public static final ConnectionHandler<Server> HELLO_DEFAULT_HANDLER = 
			new ConnectionHandler<Server>() {
				public void onConnect(Server server, Socket client) {
					if(server == null || client == null || !server.isRunning() || client.isClosed())
						return;
					if(Server.DEBUG == DebugLevel.EVERYTHING)
						System.err.println("Sending hello...");
					server.emit(new PacketMessage("Hello client " + client.hashCode() + "."), client);
				}
			};
}
