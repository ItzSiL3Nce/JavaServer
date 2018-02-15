package it.silence.client.listener;

import it.silence.client.Client;

import java.util.concurrent.atomic.AtomicBoolean;

import lombok.Getter;

public abstract class ListeningService implements Runnable {

	public final Client client;
	
	protected AtomicBoolean listening;
	@Getter protected final long listeningDelay;
	protected final boolean shouldSleep;
	
	public ListeningService(Client client) {
		this(client, 0L);
	}
	
	public ListeningService(Client client, long listeningDelay) {
		this.client = client;
		this.listening = new AtomicBoolean(false);
		if(listeningDelay < 0L)
			listeningDelay = 0L;
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
