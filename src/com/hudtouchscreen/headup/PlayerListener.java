package com.hudtouchscreen.headup;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import com.hudtouchscreen.hudmessage.ActivityMessage;
import com.hudtouchscreen.hudmessage.ListMessage;
import com.hudtouchscreen.hudmessage.LoopingMessage;
import com.hudtouchscreen.hudmessage.ShuffleMessage;
import com.hudtouchscreen.hudmessage.SongTitleMessage;
import com.hudtouchscreen.hudmessage.TimeMessage;
import com.hudtouchscreen.touchscreenplayer.ServerService;

public class PlayerListener extends Thread {
	/**
	 * Der TCP Socket.
	 */
	private Socket connection;

	/**
	 * Referenz auf den Ausgabestrom.
	 */
	private ObjectInputStream in;

	private OutputStream out;

	/**
	 * Signalisiert ob ein TCP Socket aktiv ist.
	 */
	private boolean socketSet;
	
	private ClientService clientService;

	public PlayerListener(ClientService clientService,ObjectInputStream in, OutputStream out) {
		this.in = in;
		this.out = out;
		this.clientService = clientService;
	}

	/**
	 * Initialisiert die ObjectStreams und speichert den TCP Socket im Thread.
	 * 
	 * @param model
	 *            ClientModel, Das Model das den Spielablauf und
	 *            Serverkommunikation steuert. oder Socket Argumenten.
	 * @throws IOException
	 *             Wird geworfen beim fehlerbehafteten Erstellen der
	 *             ObjectStreams.
	 */
	public void startConnection() {
		final String SERVER_IP = "10.0.2.2";
		final int PORT = 7000;
		
		try {
			InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
			connection = new Socket(SERVER_IP, PORT);
			connection.setSoTimeout(0);

			in = new ObjectInputStream(connection.getInputStream());
			out = connection.getOutputStream();
			out.flush();
			socketSet = true;

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		boolean waiting = true;

		try {

			while (waiting) {
				Object hudMessage;

				hudMessage = in.readObject();
				Message message = null;
				
				boolean trueMessage = false;

				if (hudMessage instanceof SongTitleMessage) {
					
					message = Message.obtain(null, ClientService.MSG_SONGTITLE, 0, 0);				
					message.getData().putParcelable("Songtitle", (SongTitleMessage)hudMessage);
					trueMessage = true;

				} else if (hudMessage instanceof ShuffleMessage) {			
					message = Message.obtain(null, ClientService.MSG_SHUFFLE, 0, 0);
					message.getData().putParcelable("Shuffle", (ShuffleMessage)hudMessage);
					trueMessage = true;

				} else if (hudMessage instanceof LoopingMessage) {
					message = Message.obtain(null, ClientService.MSG_LOOPING, 0, 0);
					message.getData().putParcelable("Looping", (LoopingMessage)hudMessage);
					trueMessage = true;

				} else if (hudMessage instanceof TimeMessage) {
					message = Message.obtain(null, ClientService.MSG_TIME, 0, 0);
					message.getData().putParcelable("Time", (TimeMessage)hudMessage);
					trueMessage = true;

				} else if (hudMessage instanceof ActivityMessage) {
					message = Message.obtain(null, ClientService.MSG_ACTIVITY, 0, 0);
					message.getData().putParcelable("Activity", (ActivityMessage)hudMessage);
					trueMessage = true;
				
				} else if (hudMessage instanceof ListMessage) {
					message = Message.obtain(null, ClientService.MSG_LIST, 0, 0);
					message.getData().putParcelable("List", (ListMessage)hudMessage);
					trueMessage = true;
				}
				
				if(trueMessage) {
					clientService.send(message);
				}
			}

		} catch (ClassNotFoundException e) {
			System.err.println("Unbekanntes Objekt empfangen");
			e.printStackTrace();
		} catch (EOFException e) {
			System.err.println("Ende des Objektstroms");
			e.printStackTrace();

		} catch (SocketException e) {

			System.err.println("Verbindung verloren");
			e.printStackTrace();

		} catch (IOException e) {
			System.err.println("Netzwerkfehler");
			e.printStackTrace();
		} finally {
			closeConnection();
		}

	}

	/**
	 * Hilfsmethode die eine bestehende Verbindung abbaut und deren Ressourcen
	 * freigibt.
	 */
	private void closeConnection() {
		if (socketSet) {
			socketSet = false;
			try {
				out.close();
				in.close();
				connection.close();
			} catch (IOException e) {
				System.out.println("Fehler beim schlie�en des Netzwerkes");
				e.printStackTrace();
			}
		}
	}
	

}
