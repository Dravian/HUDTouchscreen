package com.hudtouchscreen.touchscreenplayer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientListener extends Thread {
	private boolean waiting;
	private ServerSocket serverSocket;
	private ServerService server;
	private int maxClients;
	private int numberOfClients;

	public ClientListener(ServerService serverService, int maxClients) {
		final int PORT = 8000;
		
		this.server = serverService;
		this.maxClients = maxClients;
		numberOfClients = 0;

		if (maxClients < 0) {
			maxClients = 0;
		}

		try {

			serverSocket = new ServerSocket(PORT);
		} catch (IOException e) {
			System.err.println("Could not listen on port: " + PORT);
			e.printStackTrace();
			System.exit(0);
		}
		waiting = true;

	}

	@Override
	public void run() {
		while (waiting) {
			Socket connection;
			try {

				if (numberOfClients < maxClients) {
					connection = serverSocket.accept();

					Client client = new Client(connection);
					server.addClient(client);
					numberOfClients++;
					
				} else {
					break;
				}

			} catch (IOException e) {
				System.err.println("Couldn't connect with Socket");
				e.printStackTrace();
			}
		}
	}

}