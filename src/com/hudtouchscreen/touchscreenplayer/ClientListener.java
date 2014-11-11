package com.hudtouchscreen.touchscreenplayer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientListener extends Thread {
	private boolean waiting;
	private ServerSocket serverSocket;
	private MusicPlayer musicPlayer;

	public ClientListener(int port, MusicPlayer musicPlayer) {
		//super("CLT");
		
		this.musicPlayer = musicPlayer;
		
		try {

			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.err.println("Could not listen on port: " + port);
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
				connection = serverSocket.accept();

				Client client = new Client(connection);
				musicPlayer.addClient(client);

			} catch (IOException e) {
				System.err.println("Couldn't connect with Socket");
				e.printStackTrace();
			}
		}
	}
	

}