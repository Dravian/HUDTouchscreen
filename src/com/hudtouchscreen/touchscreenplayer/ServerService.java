package com.hudtouchscreen.touchscreenplayer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.hudtouchscreen.hudmessage.FinalTimeMessage;
import com.hudtouchscreen.hudmessage.HudMessage;
import com.hudtouchscreen.hudmessage.LoopingMessage;
import com.hudtouchscreen.hudmessage.ShuffleMessage;
import com.hudtouchscreen.hudmessage.SongTitleMessage;
import com.hudtouchscreen.hudmessage.StartTimeMessage;
import com.hudtouchscreen.hudmessage.TimeMessage;
import com.hudtouchscreen.parcelable.ParcelableString;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.Messenger;
import android.util.Log;

public class ServerService extends AbstractService {

	public static final int MSG_NEWCLIENT = 1;
	public static final int MSG_SONGTITLE = 2;
	public static final int MSG_SHUFFLE = 3;
	public static final int MSG_LOOPING = 4;
	public static final int MSG_STARTTIME = 5;
	public static final int MSG_FINALTIME = 6;
	
	private static final int PORT = 8000;
	private ClientListener clientListener;
	private Set<Client> clients;
	private static final int MAX_CLIENTS = 1;

	@Override
	public void onStartService() {
		clients = new HashSet<Client>();

		startListener();
	}
	
	private void startListener() {
		clientListener = new ClientListener(PORT, this, MAX_CLIENTS);
		Thread clientListenerThread = new Thread(clientListener);
		clientListenerThread.start();
	}

	@Override
	public void onStopService() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReceiveMessage(Message msg) {
		switch (msg.what) {
		case MSG_SONGTITLE:

			//if (msg.obj instanceof ParcelableString) {
				//ParcelableString test = (ParcelableString)(msg.getData().getParcelable("SongTitle"));
				//broadcast(new SongTitleMessage("shit"));
			
			final Bundle bundle = msg.getData();
			bundle.setClassLoader(getClassLoader());
			
			ParcelableString test = (ParcelableString)bundle.getParcelable("Songtitle");
			broadcast(new SongTitleMessage(test.getSongTitle()));
			
			//broadcast(new SongTitleMessage("Test"));
			
		/*	if (msg.obj instanceof SongTitleMessage) {
				broadcast((HudMessage )msg.obj);
			}*/
			break;
		/*case MSG_SHUFFLE:

			if (msg.obj instanceof Boolean) {
				broadcast(new ShuffleMessage((boolean) msg.obj));
			}
			break;
		case MSG_LOOPING:

			if (msg.obj instanceof Boolean) {
				broadcast(new LoopingMessage((boolean) msg.obj));
			}
			break;
		case MSG_STARTTIME:

			if (msg.obj instanceof Double) {
				broadcast(new StartTimeMessage((double) msg.obj));
			}
			break;
		case MSG_FINALTIME:

			if (msg.obj instanceof Double) {
				broadcast(new FinalTimeMessage((double) msg.obj));
			}
			break;*/
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
		}
	}

}
