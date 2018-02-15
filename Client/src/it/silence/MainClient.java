package it.silence;

import it.silence.client.Client;
import it.silence.client.Client.LocalClient;
import it.silence.client.packet.PacketMessage;

import java.io.IOException;

public class MainClient {

	public static void main(String[] args) throws InterruptedException, IOException {

		Client.DEBUG = true;
		
		LocalClient c = new LocalClient(80, 1024);
		c.run();

		Thread t = new Thread(c.createPacketListeningService());
		t.start();
		
		while(c.isConnected() && c.canEmit()) {
			c.emit(new PacketMessage("Hello from Client ;D"));
			Thread.sleep(1500);
		}
		
		//c.disconnect();
		
		/*while(true) {
			try {
				BufferedReader r = new BufferedReader(new InputStreamReader(s.getInputStream()));
				String message = r.readLine();
				if(message.equalsIgnoreCase("/close")) {
					System.out.println("Received \"close\" command. Disconnecting.");
					s.close();
					return;
				}
				System.out.println(message);
			} catch(IOException e) {
				System.err.println("Error reading input from server.");
				s.close();
				return;
			}
		}*/
	}
}
