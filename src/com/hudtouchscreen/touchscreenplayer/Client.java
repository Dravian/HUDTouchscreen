package com.hudtouchscreen.touchscreenplayer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.hudtouchscreen.message.LoopingMessage;
import com.hudtouchscreen.message.Message;
import com.hudtouchscreen.message.ShuffleMessage;
import com.hudtouchscreen.message.SongTitleMessage;

public class Client {
	Socket connection;
	ObjectOutputStream comOut;
	InputStream comIn;

	public Client(Socket connection) {
		this.connection = connection;

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
	
	public synchronized void send(Message message) {
		try {
			comOut.writeObject(message);
			comOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void closeConnection() {
		try {
			comOut.close();
			comIn.close();
			connection.close();
		} catch (IOException e) {
			System.out.println("Fehler beim schließen des Netzwerkes");
			e.printStackTrace();
		}

	}

}
