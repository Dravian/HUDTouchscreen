package com.hudtouchscreen.headup;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import com.hudtouchscreen.hudmessage.ActivityMessage;
import com.hudtouchscreen.hudmessage.ConnectionMessage;
import com.hudtouchscreen.hudmessage.KeyBoardMessage;
import com.hudtouchscreen.hudmessage.KeyTouchMessage;
import com.hudtouchscreen.hudmessage.ListMessage;
import com.hudtouchscreen.hudmessage.LoopingMessage;
import com.hudtouchscreen.hudmessage.ShuffleMessage;
import com.hudtouchscreen.hudmessage.SongtitleMessage;
import com.hudtouchscreen.hudmessage.TimeMessage;
import com.hudtouchscreen.hudmessage.TouchMessage;
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

	private boolean run;

	private ClientService clientService;
	
	private String serverIp;
	

	public PlayerListener(ClientService clientService, String serverIp) {
		this.clientService = clientService;
		this.serverIp = serverIp;
		run = false;
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
	protected void startConnection() {
		// final String SERVER_IP = "10.0.2.2";
		final int PORT = 7007;
		
		try {
			connection = new Socket(serverIp, PORT);
			connection.setSoTimeout(0);

			in = new ObjectInputStream(connection.getInputStream());
			out = connection.getOutputStream();
			out.flush();
			run = true;
			send(true);
		} catch (UnknownHostException e) {	
			Log.i("PlayerListener", "UnknownHostException");
			send(false);
			e.printStackTrace();		
		} catch(ConnectException e) {
			send(false);
			e.printStackTrace();
		}
		catch (IOException e) {
			send(false);
			e.printStackTrace();
		}
	}
	
	private void send(boolean success) {
		Message message;
		message = Message.obtain(null, ClientService.MSG_CONNECTION, 0,
				0);
		
		if(success) {
			Log.i("PlayerListener", "success");
			ConnectionMessage connection = new ConnectionMessage(true);
			message.getData().putParcelable("Connection", connection);
		} else {
			Log.i("PlayerListener", "nosuccess");
			ConnectionMessage connection = new ConnectionMessage(false);
			message.getData().putParcelable("Connection", connection);
		}
		
		clientService.send(message);
	}

	@Override
	public void run() {
		startConnection();
		
		try {
			
			while (run) {
				Object hudMessage;

				hudMessage = in.readObject();
				Message message = null;

				boolean trueMessage = false;

				if (hudMessage instanceof SongtitleMessage) {

					message = Message.obtain(null, ClientService.MSG_SONGTITLE,
							0, 0);
					message.getData().putParcelable("Songtitle",
							(SongtitleMessage) hudMessage);
					trueMessage = true;

				} else if (hudMessage instanceof ShuffleMessage) {
					message = Message.obtain(null, ClientService.MSG_SHUFFLE,
							0, 0);
					message.getData().putParcelable("Shuffle",
							(ShuffleMessage) hudMessage);
					trueMessage = true;

				} else if (hudMessage instanceof LoopingMessage) {
					message = Message.obtain(null, ClientService.MSG_LOOPING,
							0, 0);
					message.getData().putParcelable("Looping",
							(LoopingMessage) hudMessage);
					trueMessage = true;

				} else if (hudMessage instanceof TimeMessage) {
					message = Message
							.obtain(null, ClientService.MSG_TIME, 0, 0);
					message.getData().putParcelable("Time",
							(TimeMessage) hudMessage);
					trueMessage = true;

				} else if (hudMessage instanceof ActivityMessage) {
					message = Message.obtain(null, ClientService.MSG_ACTIVITY,
							0, 0);
					message.getData().putParcelable("Activity",
							(ActivityMessage) hudMessage);
					trueMessage = true;

				} else if (hudMessage instanceof ListMessage) {
					message = Message
							.obtain(null, ClientService.MSG_LIST, 0, 0);
					message.getData().putParcelable("List",
							(ListMessage) hudMessage);
					trueMessage = true;

				} else if (hudMessage instanceof TouchMessage) {
					message = Message.obtain(null, ClientService.MSG_TOUCH, 0,
							0);
					message.getData().putParcelable("Touch",
							(TouchMessage) hudMessage);
					trueMessage = true;

				} else if (hudMessage instanceof KeyTouchMessage) {
					message = Message.obtain(null, ClientService.MSG_KEYTOUCH,
							0, 0);
					message.getData().putParcelable("KeyTouch",
							(KeyTouchMessage) hudMessage);
					trueMessage = true;

				} else if (hudMessage instanceof KeyBoardMessage) {
					message = Message.obtain(null, ClientService.MSG_KEYBOARD,
							0, 0);
					message.getData().putParcelable("Keyboard",
							(KeyBoardMessage) hudMessage);
					trueMessage = true;

				}

				if (trueMessage) {
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
		if (run) {
			run = false;

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
