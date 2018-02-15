package it.silence.client.listener;

import it.silence.client.Client;
import it.silence.client.packet.Packet;
import it.silence.client.packet.PacketMessage;
import it.silence.reader.Reader;

import java.io.IOException;
import java.net.Socket;

public class PacketListeningService
	extends ListeningService implements Runnable {

	public PacketListeningService(Client client, long listeningDelay) {
		super(client, listeningDelay);
	}
	
	public PacketListeningService(Client client) {
		super(client, 0L);
	}
	
	@Override
	public void run() {
		super.run();
		Packet p;
		String s;
		Socket server = client.getServer();
		if(Client.DEBUG) System.out.println("Listening for packets...");
		while(listening.get() && !server.isClosed()) {
			if((p = Reader.readPacket(server)) != null) {
				switch(p.getPacketType()) {
					case Packet.MESSAGE:
						System.out.println("Received a message:\n" + ((PacketMessage) p).getMessage() + "\n");
						break;
					case Packet.SERVER_CLOSED:
						if(!server.isClosed()) {
							try {
								server.close();
							} catch (IOException e) {
								;
							}
						}
						System.err.println("Server closed.");
						return;
					default:
						break;
				}
			} else if((s = Reader.readString(server)) != null) {
				System.out.println("Received a message:\n" + s + "\n");
			}
		}
		System.err.println("Stopping PacketLS");
	}
	
}
