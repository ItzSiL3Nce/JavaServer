package it.silence.server.listener.handler;

import it.silence.server.Server;

import java.net.Socket;

public interface ConnectionHandler<ServerType extends Server> {
	public void onConnect(ServerType server, Socket client);
}
