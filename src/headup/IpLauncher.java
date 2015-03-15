package headup;

import service.ServiceManager;
import hudmessage.ConnectionMessage;
import hudmessage.SongtitleMessage;

import com.touchscreen.touchscreenplayer.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class IpLauncher extends Activity {
	private EditText ipText;
	private ServiceManager service;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.hudlauncher);
		ipText = (EditText) findViewById(R.id.ipAddress);

		this.service = new ServiceManager(this, ClientService.class,
				new Handler() {

					@Override
					public void handleMessage(Message msg) {
						final Bundle bundle = msg.getData();
						bundle.setClassLoader(getClassLoader());
						
						switch (msg.what) {
						case ClientService.MSG_CONNECTION:
							ConnectionMessage connected = (ConnectionMessage)bundle.getParcelable("Connection");
							
							if(connected.isConnected() ){
								Intent i = new Intent(getApplicationContext(), HeadUpPlayer.class);
								startActivity(i);	
								
							} else {
								Toast.makeText(getBaseContext(), "Connection Error " , Toast.LENGTH_LONG).show();
							}
							
							break;
						}
					}
		});
		
		service.start();
	}

	public void click(View view) {
		Message message = Message.obtain(null, ClientService.MSG_IP, 0,
				0);
		
			SongtitleMessage connection = new SongtitleMessage(ipText.getText().toString());
			message.getData().putParcelable("IPText", connection);
		
		try {
			service.send(message);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	  protected void onDestroy() {
	    super.onDestroy();
	    
	    try { service.unbind(); }
	    catch (Throwable t) { }

	}
}
