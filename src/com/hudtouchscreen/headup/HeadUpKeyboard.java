package com.hudtouchscreen.headup;


import com.hudtouchscreen.hudmessage.ActivityMessage;
import com.touchscreen.touchscreenplayer.R;

import Service.ServiceManager;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
public class HeadUpKeyboard extends Activity{
private ServiceManager service;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.keyboard);
		
		this.service = new ServiceManager(this, ClientService.class,
				new Handler() {
				
					@Override
					public void handleMessage(Message msg) {
						final Bundle bundle = msg.getData();
						bundle.setClassLoader(getClassLoader());
						
						switch (msg.what) {						
							
						case ClientService.MSG_ACTIVITY:
							ActivityMessage activityMessage = (ActivityMessage)bundle.getParcelable("Activity");
							
							if(activityMessage.getActivity() == ActivityMessage.BACK_TO_MAIN) {
								service.unbind();
								finish();
							}
							break;
						default:
						}
					}
				});

		service.start();

	}
}
