package it.silence.server.listener;

import it.silence.client.packet.Packet;
import it.silence.client.packet.PacketMessage;
import it.silence.reader.Reader;
import it.silence.server.Server;
import it.silence.server.Server.DebugLevel;

import java.net.Socket;
import java.util.List;

public class PacketListeningService<ServerType extends Server> 
	extends ListeningService<ServerType> implements Runnable {

	public PacketListeningService(ServerType server, long listeningDelay) {
		super(server, listeningDelay);
	}
	
	public PacketListeningService(ServerType server) {
		super(server, 0L);
	}
	
	@Override
	public void run() {
		super.run();
		Packet p;
		List<Socket> clients; Socket client;
		int i; String s;
		if(Server.DEBUG == DebugLevel.EVERYTHING) 
			System.err.println("Listening for packets...");
		while(listening.get() && server.isRunning()) {
			try {
				clients = server.getClients();
				for(i = clients.size() - 1; i >= 0; --i) {
					client = clients.get(i);
					if(client.isClosed() || !client.isConnected()) {
						server.disconnectClient(client);
						return;
					}
					if((p = Reader.readPacket(client)) != null) {
						switch(p.getPacketType()) {
							case Packet.DISCONNECT:
								server.disconnectClient(client);
								break;
							case Packet.MESSAGE:
								System.out.println("Received message:\n" + ((PacketMessage) p).getMessage());
								break;
							default:
								if(Server.DEBUG == DebugLevel.INFO || Server.DEBUG == DebugLevel.EVERYTHING)
									System.err.println("\nReceived invalid packet:\n" + p + "\n");
								break;
						}
					} else {
						s = Reader.readString(client);
						if(s == null)
							server.disconnectClient(client);
						else System.out.println("Received message:\n" + s + "\n");
					}
				}
				if(shouldSleep)
					Thread.sleep(listeningDelay);
			} catch(InterruptedException e) {
				if(Server.DEBUG == DebugLevel.EVERYTHING)
					System.err.println("Stopping PacketLS");
				return;
			}
		}
		if(Server.DEBUG == DebugLevel.EVERYTHING) 
			System.err.println("Stopping PacketLS");
	}
	
}
