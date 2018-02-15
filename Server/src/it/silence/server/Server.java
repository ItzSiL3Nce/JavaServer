package it.silence.server;

import it.silence.client.packet.Packet;
import it.silence.server.listener.ConnectionListeningService;
import it.silence.server.listener.PacketListeningService;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.Getter;

public class Server implements Runnable {
	
	private AtomicBoolean running = new AtomicBoolean(false);
	
	@Getter private final int port;
	
	@Getter private ServerSocket serverSocket;
	@Getter private final List<Socket> clients = Collections.synchronizedList(new ArrayList<Socket>());
	
	public static enum DebugLevel {
		NONE,
		INFO,
		EVERYTHING
	}
	
	public static DebugLevel DEBUG = DebugLevel.NONE;
	
	public Server(int port) {
		this.port = port;
	}
	
	public boolean isRunning() {
		return running.get();
	}
	
	public void run() {
		if(!running.get()) {
			try {
				serverSocket = new ServerSocket(port);
				running.set(true);
				if(DEBUG == DebugLevel.INFO || DEBUG == DebugLevel.EVERYTHING) 
					System.err.println("Server running...");
			} catch(IOException e) {
				e.printStackTrace();
				running.set(false);
			}
		}
	}
	
	public void end() {
		try {
			broadcast(new Packet(Packet.SERVER_CLOSED));
			for(Socket client: clients) {
				if(!client.isClosed())
					client.close();
			}
			clients.clear();
			serverSocket.close();
			running.set(false);
			if(DEBUG == DebugLevel.INFO || DEBUG == DebugLevel.EVERYTHING) 
				System.err.println("Server closed!");
		} catch (IOException e) {
			e.printStackTrace();
			if(DEBUG == DebugLevel.EVERYTHING) 
				System.err.println("Error while closing the server!");
		}
	}
	
	public ConnectionListeningService<? extends Server> createConnectionListener() {
		return new ConnectionListeningService<Server>(this, 0L);
	}
	
	public ConnectionListeningService<? extends Server> createConnectionListener(long listeningDelay) {
		return new ConnectionListeningService<Server>(this, listeningDelay);
	}
	
	public PacketListeningService<? extends Server> createPacketListener() {
		return new PacketListeningService<Server>(this, 0L);
	}
	
	public PacketListeningService<? extends Server> createPacketListener(long listeningDelay) {
		return new PacketListeningService<Server>(this, listeningDelay);
	}
	
	public synchronized void disconnectClient(Socket client) {
		if(!client.isClosed()) {
			try {
				client.close();
				if(DEBUG == DebugLevel.INFO || DEBUG == DebugLevel.EVERYTHING) 
					System.err.println("Client " + client.hashCode() + " has disconnected.");
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		if(clients.contains(client))
			clients.remove(client);
	}

	/**
	 * @deprecated Use emit(Packet, Socket) instead. This version doesn't work properly and shouldn't be used at all.
     * @see emit(Packet, Socket)
     * 
	 * @param message
	 * @param to
	 */
	@Deprecated
	public void emit(String message, Socket to) {
		if(message == null || to == null || !running.get() || to.isClosed())
			return;
		try {
			PrintWriter out = new PrintWriter(to.getOutputStream(), true);
			out.println(message);
			out.flush();
			if(DEBUG == DebugLevel.EVERYTHING)
				System.err.println("Sent a message (No Packet) to " + to.hashCode());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void emit(Packet packet, Socket to) {
		if(to == null || packet == null || !running.get() || to.isClosed())
			return;
		try {
			ObjectOutputStream os = new ObjectOutputStream(to.getOutputStream());
			os.writeObject(packet);
			os.flush();
			if(DEBUG == DebugLevel.EVERYTHING)
				System.err.println("Sent a packet to " + to.hashCode());
		} catch (IOException e) {
			if(DEBUG == DebugLevel.INFO || DEBUG == DebugLevel.EVERYTHING)
				System.err.println("Couldn't emit to " + to.hashCode() + ".");
		}
	}
	
	/**
	 * @deprecated See emit(String, Socket), use broadcast(Packet) instead.
	 * @see emit(String, Socket)
	 * 
	 * @param message
	 */
	@Deprecated
	public void broadcast(String message) {
		if(!running.get())
			return;
		for(Socket client: clients)
			emit(message, client);
	}
	
	public void broadcast(Packet packet) {
		if(packet == null || !running.get())
			return;
		for(Socket client: clients)
			emit(packet, client);
	}
}
