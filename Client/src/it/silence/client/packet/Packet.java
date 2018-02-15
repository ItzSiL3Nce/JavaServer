package it.silence.client.packet;

import java.io.Serializable;

public class Packet implements Serializable {

	private static final long serialVersionUID = 2432015386668559634L;

	private byte packetType;
	
	public Packet(byte packetType) {
		this.packetType = packetType;
	}
	
	public byte getPacketType() {
		return packetType;
	}
	
	public void setPacketType(byte packetType) {
		this.packetType = packetType;
	}
	
	public static final byte DISCONNECT = 1;
	public static final byte MESSAGE = 2;
	public static final byte SERVER_CLOSED = 3;
}
