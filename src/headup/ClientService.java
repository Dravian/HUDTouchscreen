package headup;

import service.AbstractService;
import headup.PlayerListener;
import hudmessage.SongtitleMessage;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

public class ClientService extends AbstractService {

	public static final int MSG_NEWCLIENT = 1;
	public static final int MSG_SONGTITLE = 2;
	public static final int MSG_SHUFFLE = 3;
	public static final int MSG_LOOPING = 4;
	public static final int MSG_TIME = 5;
	public static final int MSG_ACTIVITY = 6;
	public static final int MSG_LIST = 7;
	public static final int MSG_KEYBOARD = 8;
	public static final int MSG_TOUCH = 9;
	public static final int MSG_KEYTOUCH = 10;
	public static final int MSG_IP = 11;
	public static final int MSG_CONNECTION = 12;
	public static final int MSG_LOG = 13;
	public static final int MSG_SEEKBARLOG = 14;

	private PlayerListener playerListener;

	@Override
	public void onStartService() {


	}

	public void init(String serverIp) {

		playerListener = new PlayerListener(this, serverIp);
		Thread playerListenerThread = new Thread(playerListener);
		playerListenerThread.start();
	}

	@Override
	public void onStopService() {

	}

	@Override
	public void onReceiveMessage(Message msg) {
		final Bundle bundle = msg.getData();
		bundle.setClassLoader(getClassLoader());

		switch (msg.what) {
		case MSG_IP:
			SongtitleMessage text = (SongtitleMessage) bundle
					.getParcelable("IPText");
			Log.i("ClientService", "Ip sent: " + text.getText());
			init(text.getText());
			break;
		default:
		}
	}

}
