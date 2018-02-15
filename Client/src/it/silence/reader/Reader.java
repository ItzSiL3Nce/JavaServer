package it.silence.reader;

import it.silence.client.packet.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Scanner;

public final class Reader {

	private Reader() {}
	
	public static Packet readPacket(Socket from) {
		try {
			ObjectInputStream is = new ObjectInputStream(from.getInputStream());
			return (Packet) is.readObject();
		} catch(IOException | ClassNotFoundException e) {
			return null;
		}	
	}
	
	public static String readString(Socket from) {
		try {
			Scanner s = new Scanner(from.getInputStream());
			s.useDelimiter("\\A");
			if(s.hasNext()) {
				String str = s.next();
				s.close();
				return str;
			}
			s.close();
		} catch (IOException e) {
			;
		}
		return null;
	}
}
