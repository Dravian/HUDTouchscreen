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
		final TextView title1 = (TextView) findViewById(R.id.title1);
		final TextView title2 = (TextView) findViewById(R.id.title2);
		final TextView title3 = (TextView) findViewById(R.id.title3);
		final TextView title4 = (TextView) findViewById(R.id.title4);
		final TextView title5 = (TextView) findViewById(R.id.title5);
		
		title1.setClickable(false);
		title2.setClickable(false);
		title3.setClickable(false);
		title4.setClickable(false);
		title5.setClickable(false);
		
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
							
							if(list.size() == 5) {
								final String songTitle1 = list.get(0);
								final String songTitle2 = list.get(1);
								final String songTitle3 = list.get(2);
								final String songTitle4 = list.get(3);
								final String songTitle5 = list.get(4);
								
								runOnUiThread(new Runnable() {

									@Override
									public void run() {
										title1.setText(songTitle1);
										title2.setText(songTitle2);
										title3.setText(songTitle3);
										title4.setText(songTitle4);
										title5.setText(songTitle5);
									}
								});
							}
							
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
}
