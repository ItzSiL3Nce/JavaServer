package it.silence.server.listener;

import it.silence.server.Server;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ListeningService<ServerType extends Server> implements Runnable {

	public final ServerType server;
	
	protected AtomicBoolean listening;
	protected final long listeningDelay;
	protected final boolean shouldSleep;
	
	public ListeningService(ServerType server) {
		this(server, 0L);
	}
	
	public ListeningService(ServerType server, long listeningDelay) {
		this.server = server;
		this.listening = new AtomicBoolean(false);
		this.listeningDelay = listeningDelay;
		this.shouldSleep = listeningDelay > 0L;
	}
	
	public boolean isListening() {
		return listening.get();
	}
	
	public void stopListening() {
		listening.set(false);
	}
	
	public void run() {
		listening.set(true);
	}
}
