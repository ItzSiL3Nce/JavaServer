package it.silence.server.listener;

import it.silence.server.Server;
import it.silence.server.Server.DebugLevel;
import it.silence.server.listener.handler.ConnectionHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class ConnectionListeningService<ServerType extends Server> 
	extends ListeningService<ServerType> implements Runnable {
	
	@Getter private final List<ConnectionHandler<ServerType>> handlers = 
			new ArrayList<ConnectionHandler<ServerType>>();
	
	@Getter private final List<ConnectionHandler<Server>> unsafeHandlers = 
			new ArrayList<ConnectionHandler<Server>>();
	
	
	public ConnectionListeningService(ServerType server, long listeningDelay) {
		super(server, listeningDelay);
	}
	
	public ConnectionListeningService(ServerType server) {
		super(server, 1L);
	}
	
	public void addHandler(ConnectionHandler<ServerType> handler) {
		handlers.add(handler);
	}
	
	public void addUnsafeHandler(ConnectionHandler<Server> unsafeHandler) {
		unsafeHandlers.add(unsafeHandler);
	}
	
	public void removeHandler(ConnectionHandler<ServerType> handler) {
		handlers.remove(handler);
	}

	public void removeUnsafeHandler(ConnectionHandler<Server> unsafeHandler) {
		unsafeHandlers.remove(unsafeHandler);
	}
	
	@Override
	public void run() {
		super.run();
		if(Server.DEBUG == DebugLevel.EVERYTHING) 
			System.err.println("Listening for new connections...");
		ServerSocket ss = server.getServerSocket();
		while(listening.get() && server.isRunning() && !ss.isClosed()) {
			try {
				Socket client = ss.accept();
				server.getClients().add(client);
				if(Server.DEBUG == DebugLevel.INFO || Server.DEBUG == DebugLevel.EVERYTHING) 
					System.out.println("Client " + client.hashCode() + " has connected.");
					for(ConnectionHandler<ServerType> handler: handlers)
						handler.onConnect(server, client);
					for(ConnectionHandler<Server> unsafeHandler: unsafeHandlers)
						unsafeHandler.onConnect(server, client);
				if(shouldSleep)
					Thread.sleep(listeningDelay);
			} catch(SocketException | InterruptedException e) {
				if(Server.DEBUG == DebugLevel.EVERYTHING) 
					System.err.println("Stopping ConnectionLS");
				return;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
