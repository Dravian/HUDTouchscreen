package com.hudtouchscreen.headup;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import Service.AbstractService;
import android.os.Bundle;
import android.os.Message;

import com.hudtouchscreen.headup.PlayerListener;
import com.hudtouchscreen.hudmessage.HudMessage;
import com.hudtouchscreen.hudmessage.LoopingMessage;
import com.hudtouchscreen.hudmessage.ShuffleMessage;
import com.hudtouchscreen.hudmessage.TextMessage;
import com.hudtouchscreen.hudmessage.TimeMessage;

public class ClientService extends AbstractService{

	public static final int MSG_NEWCLIENT = 1;
	public static final int MSG_TEXT = 2;
	public static final int MSG_SHUFFLE = 3;
	public static final int MSG_LOOPING = 4;
	public static final int MSG_TIME = 5;
	public static final int MSG_ACTIVITY = 6;
	public static final int MSG_LIST= 7;
	public static final int MSG_KEYBOARD = 8;
	public static final int MSG_TOUCH = 9;
	
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

	
	@Override
	public void onStartService() {
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
		
		PlayerListener playerListener = new PlayerListener(this, in, out);
		Thread playerListenerThread = new Thread(playerListener);
		playerListenerThread.start();
	}
	
	@Override
	public void onStopService() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReceiveMessage(Message msg) {
	}
	
	
	

}
