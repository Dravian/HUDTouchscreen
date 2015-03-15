package headup;

import hudmessage.ActivityMessage;
import hudmessage.ListMessage;
import hudmessage.TouchMessage;

import java.util.ArrayList;

import service.ServiceManager;

import com.touchscreen.touchscreenplayer.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

public class HeadUpList extends Activity {

	private ServiceManager service;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_search_headup);
		final TextView title1 = (TextView) findViewById(R.id.title1);
		final TextView title2 = (TextView) findViewById(R.id.title2);
		final TextView title3 = (TextView) findViewById(R.id.title3);
		final TextView title4 = (TextView) findViewById(R.id.title4);
		final TextView title5 = (TextView) findViewById(R.id.title5);

		Intent intent = getIntent();
		final ArrayList<String> listValues = intent
				.getStringArrayListExtra("Track Names");

		/*
		 * runOnUiThread(new Runnable() {
		 * 
		 * @Override public void run() { title1.setText(listValues.get(0));
		 * title2.setText(listValues.get(1)); title3.setText(listValues.get(2));
		 * title4.setText(listValues.get(3)); title5.setText(listValues.get(4));
		 * } });
		 */

		title1.setText(listValues.get(0));
		title2.setText(listValues.get(1));
		title3.setText(listValues.get(2));
		title4.setText(listValues.get(3));
		title5.setText(listValues.get(4));

		this.service = new ServiceManager(this, ClientService.class,
				new Handler() {

					@Override
					public void handleMessage(Message msg) {
						final Bundle bundle = msg.getData();
						bundle.setClassLoader(getClassLoader());

						switch (msg.what) {
						case ClientService.MSG_LIST:

							ListMessage listMessage = (ListMessage) bundle
									.getParcelable("List");
							final ArrayList<String> titleList = new ArrayList<String>();
							listMessage.fillList(titleList);

							final String songTitle1 = titleList.get(0);
							final String songTitle2 = titleList.get(1);
							final String songTitle3 = titleList.get(2);
							final String songTitle4 = titleList.get(3);
							final String songTitle5 = titleList.get(4);

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

							break;
						case ClientService.MSG_TOUCH:
							TouchMessage touchMessage = (TouchMessage) bundle
									.getParcelable("Touch");
							final int buttonType = touchMessage.getButtonType();
							final boolean touching = touchMessage.isTouching();

							runOnUiThread(new Runnable() {

								@Override
								public void run() {

									switch (buttonType) {
									case TouchMessage.LIST1:
										if (touching) {
											title1.setBackgroundResource(R.color.red);
										} else {
											title1.setBackgroundResource(R.color.black);
										}
										break;
									case TouchMessage.LIST2:
										if (touching) {
											title2.setBackgroundResource(R.color.red);
										} else {
											title2.setBackgroundResource(R.color.black);
										}
										break;
									case TouchMessage.LIST3:
										if (touching) {
											title3.setBackgroundResource(R.color.red);
										} else {
											title3.setBackgroundResource(R.color.black);
										}
										break;
									case TouchMessage.LIST4:
										if (touching) {
											title4.setBackgroundResource(R.color.red);
										} else {
											title4.setBackgroundResource(R.color.black);
										}
										break;
									case TouchMessage.LIST5:
										if (touching) {
											title5.setBackgroundResource(R.color.red);
										} else {
											title5.setBackgroundResource(R.color.black);
										}
										break;
									}

								}
							});
							break;
						case ClientService.MSG_ACTIVITY:
							ActivityMessage activityMessage = (ActivityMessage) bundle
									.getParcelable("Activity");

							if (activityMessage.getActivity() == ActivityMessage.BACK_TO_MAIN) {						
								finish();
							}
							break;
						default:
						}
					}
				});

		service.start();

	}
	
	@Override
	  protected void onDestroy() {
	    super.onDestroy();
	    
	    try { service.unbind(); }
	    catch (Throwable t) { }

	}
}
