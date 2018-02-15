package it.silence.server.shouldnt_use;

import it.silence.server.Server;
import it.silence.server.listener.ConnectionListeningService;
import it.silence.server.listener.PacketListeningService;
import it.silence.server.listener.handler.ConnectionHandler;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor

/* This is just an utility, but really you shouldn't use it.
 * It is lots of time better to implement your own server
 * without using any helper class/method.
 */
public class ServerWorker<ServerType extends Server> implements Runnable {

	@Getter private final ServerType server;
	@Getter private final List<ConnectionHandler<ServerType>> handlers;
	@Getter private final List<ConnectionHandler<Server>> unsafeHandlers;
	@Getter private final long listeningDelay;
	

	@SuppressWarnings("unchecked")
	public void run() {
		ConnectionListeningService<ServerType> cls = (ConnectionListeningService<ServerType>) server.createConnectionListener(listeningDelay);
		PacketListeningService<ServerType> pls = (PacketListeningService<ServerType>) server.createPacketListener(listeningDelay);
		
		if(handlers != null)
			for(ConnectionHandler<ServerType> handler: handlers)
				cls.addHandler(handler);
		
		if(unsafeHandlers != null)
			for(ConnectionHandler<Server> unsafeHandler: unsafeHandlers)
				cls.addUnsafeHandler(unsafeHandler);
		
		Thread tCls = new Thread(cls);
		Thread tPls = new Thread(pls);
		
		
		tCls.start();
		tPls.start();
		
		try {
			tCls.join();
			tPls.join();
		} catch(InterruptedException e) {
			;
		}
	}
}
