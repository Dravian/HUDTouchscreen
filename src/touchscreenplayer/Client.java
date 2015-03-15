package touchscreenplayer;

import hudmessage.HudMessage;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
	Socket connection;
	ObjectOutputStream comOut;
	InputStream comIn;
	ServerService server;

	public Client(Socket connection, ServerService server) {
		this.connection = connection;
		this.server = server;

		try {
			connection.setSoTimeout(0);
			comOut = new ObjectOutputStream(connection.getOutputStream());
			comOut.flush();
		} catch (IOException e) {
			System.err.println("Couldn't getOutputStream");
			try {
				connection.close();
			} catch (IOException e1) {
				System.err.println("Closing Failed");
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		try {
			comIn = connection.getInputStream();
		} catch (IOException e) {
			try {
				connection.close();
			} catch (IOException e1) {
				System.err.println("Closing Failed");
				e1.printStackTrace();
			}
			System.err.println("Couldn't getInputStream");
			e.printStackTrace();
		}

	}
	
	public synchronized void send(HudMessage message) {
		try {
			comOut.writeObject(message);
			comOut.flush();
		} catch (IOException e) {
			closeConnection();
			e.printStackTrace();
		}
	}

	public void closeConnection() {
		try {
			comOut.close();
			comIn.close();
			connection.close();
			server.removeClient(this);
		} catch (IOException e) {
			System.out.println("Fehler beim schließen des Netzwerkes");
			e.printStackTrace();
		}

	}

}
