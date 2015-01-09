package com.hudtouchscreen.headup;


import java.util.ArrayList;

import com.hudtouchscreen.hudmessage.ActivityMessage;
import com.hudtouchscreen.hudmessage.ListMessage;
import com.hudtouchscreen.hudmessage.LoopingMessage;
import com.hudtouchscreen.hudmessage.ShuffleMessage;
import com.hudtouchscreen.hudmessage.SongTitleMessage;
import com.hudtouchscreen.hudmessage.TimeMessage;
import com.touchscreen.touchscreenplayer.R;

import Service.ServiceManager;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

public class HeadUpList extends Activity{
	
	private ServiceManager service;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_search);
		
		this.service = new ServiceManager(this, ClientService.class,
				new Handler() {
				
					@Override
					public void handleMessage(Message msg) {
						final Bundle bundle = msg.getData();
						bundle.setClassLoader(getClassLoader());
						
						switch (msg.what) {
						case ClientService.MSG_LIST:
							ListMessage listMessage = (ListMessage)bundle.getParcelable("List");
							ArrayList<String> list = new ArrayList<String>();
							listMessage.fillList(list);
							fillList(list);
							break;
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
	
	private void fillList(ArrayList<String> list) {
		TextView title1 = (TextView) findViewById(R.id.title1);
		title1.setText(list.get(0));
		
		TextView title2 = (TextView) findViewById(R.id.title2);
		title2.setText(list.get(1));
		
		TextView title3 = (TextView) findViewById(R.id.title3);
		title3.setText(list.get(2));
		
		TextView title4 = (TextView) findViewById(R.id.title4);
		title4.setText(list.get(3));
		
		TextView title5 = (TextView) findViewById(R.id.title5);
		title5.setText(list.get(4));
	}
}
