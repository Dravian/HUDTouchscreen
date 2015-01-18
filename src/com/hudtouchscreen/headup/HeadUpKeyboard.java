package com.hudtouchscreen.headup;

import com.hudtouchscreen.hudmessage.ActivityMessage;
import com.hudtouchscreen.hudmessage.TextMessage;
import com.touchscreen.touchscreenplayer.R;

import Service.ServiceManager;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.EditText;

public class HeadUpKeyboard extends Activity {
	private ServiceManager service;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.keyboardheadup);

		this.service = new ServiceManager(this, ClientService.class,
				new Handler() {

					EditText keyBoardText = (EditText) findViewById(R.id.editKeyBoard);

					@Override
					public void handleMessage(Message msg) {
						final Bundle bundle = msg.getData();
						bundle.setClassLoader(getClassLoader());

						switch (msg.what) {
						case ClientService.MSG_TEXT:
							TextMessage letterMessage = (TextMessage) bundle
									.getParcelable("Text");

							if (letterMessage.getText() != null) {
								final String letter = (letterMessage).getText();

								runOnUiThread(new Runnable() {

									@Override
									public void run() {
										keyBoardText.setText(letter);
									}
								});
							}
							break;

						case ClientService.MSG_ACTIVITY:
							ActivityMessage activityMessage = (ActivityMessage) bundle
									.getParcelable("Activity");

							if (activityMessage.getActivity() == ActivityMessage.BACK_TO_MAIN) {
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
