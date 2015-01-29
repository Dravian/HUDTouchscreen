package com.hudtouchscreen.touchscreenplayer;

import java.util.HashSet;
import java.util.Set;

import com.hudtouchscreen.hudmessage.ActivityMessage;
import com.hudtouchscreen.hudmessage.HudMessage;
import com.hudtouchscreen.hudmessage.ListMessage;
import com.hudtouchscreen.hudmessage.LoopingMessage;
import com.hudtouchscreen.hudmessage.ShuffleMessage;
import com.hudtouchscreen.hudmessage.TextMessage;
import com.hudtouchscreen.hudmessage.TimeMessage;

import Service.AbstractService;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

public class ServerService extends AbstractService {

	public static final int MSG_NEWCLIENT = 1;
	public static final int MSG_TEXT = 2;
	public static final int MSG_SHUFFLE = 3;
	public static final int MSG_LOOPING = 4;
	public static final int MSG_TIME = 5;
	public static final int MSG_ACTIVITY = 6;
	public static final int MSG_LIST= 7;
	public static final int MSG_KEYBOARD = 8;
	
	private ClientListener clientListener;
	private Set<Client> clients;
	private final int MAX_CLIENTS = 1;
	

	@Override
	public void onStartService() {
		clients = new HashSet<Client>();

		startListener();
	}
	
	private void startListener() {
		clientListener = new ClientListener(this, MAX_CLIENTS);
		Thread clientListenerThread = new Thread(clientListener);
		clientListenerThread.start();
	}

	@Override
	public void onStopService() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReceiveMessage(Message msg) {
		final Bundle bundle = msg.getData();
		bundle.setClassLoader(getClassLoader());
		
		switch (msg.what) {
		case MSG_TEXT:
			TextMessage text = (TextMessage)bundle.getParcelable("Text");
			broadcast(text);
			break;
		case MSG_SHUFFLE:
			ShuffleMessage shuffle = (ShuffleMessage)bundle.getParcelable("Shuffle");
			broadcast(shuffle);
			break;
			
		case MSG_LOOPING:
			LoopingMessage looping = (LoopingMessage)bundle.getParcelable("Looping");
			broadcast(looping);
			break;
			
		case MSG_TIME:		
			TimeMessage time = (TimeMessage)bundle.getParcelable("Time");
			broadcast(time);
			break;
		case MSG_ACTIVITY:
			ActivityMessage activity = (ActivityMessage)bundle.getParcelable("Activity");
			broadcast(activity);
			break;
		case MSG_LIST:
			ListMessage list = (ListMessage)bundle.getParcelable("List");
			broadcast(list);
			break;
		default:
		}
		

	}

	private void broadcast(HudMessage message) {
		for (Client client : clients) {
			client.send(message);
		}
	}

	protected void addClient(Client client) {
		if (clients.size() < MAX_CLIENTS) {
			clients.add(client);
			Message message = Message.obtain(null, MSG_NEWCLIENT,0,0);
			send(message);
		}
	}

}
