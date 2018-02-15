package it.silence;

import it.silence.html.HtmlFetcher;
import it.silence.server.Server;
import it.silence.server.Server.DebugLevel;
import it.silence.server.http.HttpServer;
import it.silence.server.listener.ConnectionListeningService;
import it.silence.server.listener.PacketListeningService;
import it.silence.server.listener.handler.Handlers;

import java.io.IOException;

public class MainServer {
	
	public static void main(String[] args) throws InterruptedException, IOException {
		Server.DEBUG = DebugLevel.EVERYTHING;
		httpServer(80);
	}
	

	private static void httpServer(int port) throws InterruptedException {
		final HttpServer s = new HttpServer(port);		
		s.run();
		
		ConnectionListeningService<HttpServer> connections = s.createConnectionListener(256L);
		PacketListeningService<HttpServer> packets = s.createPacketListener(256L);
		
		HtmlFetcher html = HtmlFetcher.getFetcher();
		html.error404Content = html.fetchStatic("errors/error404.html");
		
		connections.addHandler(Handlers.HTTP_DEFAULT_HANDLER);
		
		Thread cThread = new Thread(connections);
		cThread.start();
		
		Thread pThread = new Thread(packets);
		pThread.start();
		
		/*JOptionPane.showConfirmDialog(null, "Close this dialog to stop the server.", "Server", JOptionPane.CLOSED_OPTION,
				JOptionPane.PLAIN_MESSAGE);
		connections.stopListening();
		packets.stopListening();
		
		pThread.join();
		
		// Connections thread must be forcibly interrupted
		// Because it calls Socket::accept() which blocks the thread, thus the thread can never join.
		cThread.interrupt();
		s.end();*/
	}
}
