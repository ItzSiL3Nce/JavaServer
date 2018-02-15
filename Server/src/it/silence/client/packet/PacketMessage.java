package it.silence.client.packet;

import java.io.Serializable;

public class PacketMessage extends Packet implements Serializable {

	private static final long serialVersionUID = -8302670450165774993L;

	private String message;
	
	public PacketMessage(String message) {
		super(Packet.MESSAGE);
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
}
