package it.silence.client;

import it.silence.client.listener.PacketListeningService;
import it.silence.client.packet.Packet;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.Getter;

public class Client implements Runnable {

	public static boolean DEBUG = false;
	
	public final String address;
	public final int port;
	
	@Getter private Socket server = null;
	
	private AtomicBoolean connected = new AtomicBoolean(false);
	
	@Getter private final long reconnectionDelay;
	private final boolean tryOnce;
	private final boolean shouldSleep;
	
	public Client(String address, int port) {
		this(address, port, -1L);
	}
	
	public Client(String address, int port, long reconnectionDelay) {
		this.address = address;
		this.port = port;
		this.reconnectionDelay = reconnectionDelay;
		this.tryOnce = reconnectionDelay < 0L;
		this.shouldSleep = reconnectionDelay > 0L;
	}
	
	public void run() {
		if(!connected.get()) {
			while(server == null) {
				try {
					server = new Socket(address, port);
					connected.set(true);;
					if(DEBUG) System.out.println("Connection established to server " + address + ":" + port);
					return;
				} catch (IOException e) {
					if(tryOnce) {
						if(DEBUG) 
							System.err.println("Couldn't connect to server.");
						return;
					}
					if(DEBUG) System.err.println("Couldn't connect to server, retrying...");
					if(shouldSleep) {
						try {
							Thread.sleep(reconnectionDelay);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
							//return;
						}
					}
				}
			}
		}
	}
	
	public boolean isConnected() {
		return connected.get();	
	}
	
	public boolean canEmit() {
		return !server.isClosed() && 
				server.isConnected() && !server.isOutputShutdown();
	}
	
	public void disconnect() {
		if(connected.get()) {
			connected.set(false);
			if(DEBUG) System.out.println("Disconnecting...");
			emit(new Packet(Packet.DISCONNECT));
			try {
				if(!server.isClosed())
					server.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void emit(Packet packet) {
		if(!server.isClosed() && server.isConnected() && packet != null) {
			try {
				ObjectOutputStream os = new ObjectOutputStream(server.getOutputStream());
				os.writeObject(packet);
				os.flush();
				//os.close();
			} catch (IOException e) {
				System.err.println("Couldn't emit. Server is closed?");
			}
		}
	}
	
	public PacketListeningService createPacketListeningService() {
		return new PacketListeningService(this, 0L);
	}
	
	public PacketListeningService createPacketListeningService(long listeningDelay) {
		return new PacketListeningService(this, listeningDelay);
	}
	
	public static class LocalClient extends Client {
		public LocalClient(int port) {
			super("127.0.0.1", port, -1L);
		}
		
		public LocalClient(int port, long reconnectDelay) {
			super("127.0.0.1", port, reconnectDelay);
		}
	}
 }
