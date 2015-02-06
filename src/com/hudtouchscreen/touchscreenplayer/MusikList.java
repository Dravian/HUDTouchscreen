package com.hudtouchscreen.touchscreenplayer;

import java.util.ArrayList;
import java.util.List;

import com.hudtouchscreen.hudmessage.ActivityMessage;
import com.hudtouchscreen.hudmessage.ListMessage;
import com.touchscreen.touchscreenplayer.R;

import Service.ServiceManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MusikList extends Activity implements OnGestureListener {
	private List<String> listValues;
	private String[] list;
	private int position;
	private TextView title1;
	private TextView title2;
	private TextView title3;
	private TextView title4;
	private TextView title5;
	private GestureDetector gDetector;

	private ServiceManager service;
	private boolean activityOn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_search);
		position = 0;
		list = new String[5];
		activityOn = true;
		
		Intent intent = getIntent();
		listValues = intent.getStringArrayListExtra("Track Names");

		gDetector = new GestureDetector(this, this);

		this.service = new ServiceManager(this, ServerService.class,
				new Handler() {

					@Override
					public void handleMessage(Message msg) {
						final Bundle bundle = msg.getData();
						bundle.setClassLoader(getClassLoader());

						switch (msg.what) {
						case ServerService.MSG_NEWCLIENT:
							sendToService(ServerService.MSG_LIST);
						default:
							super.handleMessage(msg);

						}
					}
				});	
	
		
		service.start();
		
		View.OnTouchListener gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return gDetector.onTouchEvent(event);
			}
		};

		title1 = (TextView) findViewById(R.id.title1);
		title1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (list[0] != "") {
					finishClick(position);
				}
			}
		});
		title1.setOnTouchListener(gestureListener);

		title2 = (TextView) findViewById(R.id.title2);
		title2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (list[1] != "") {
					finishClick(position + 1);
				}
			}
		});
		title2.setOnTouchListener(gestureListener);

		title3 = (TextView) findViewById(R.id.title3);
		title3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (list[2] != "") {
					finishClick(position + 2);
				}
			}
		});
		title3.setOnTouchListener(gestureListener);

		title4 = (TextView) findViewById(R.id.title4);
		title4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (list[3] != "") {
					finishClick(position + 3);
				}
			}
		});
		title4.setOnTouchListener(gestureListener);

		title5 = (TextView) findViewById(R.id.title5);
		title5.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (list[4] != "") {
					finishClick(position + 4);
				}
			}
		});
		title5.setOnTouchListener(gestureListener);
		
		
		fillList();
	}

	private void fillList() {
		for (int i = 0; i < 5; i++) {
			if (listValues.size() > position + i) {
				list[i] = listValues.get(position + i);

			} else {
				list[i] = "";
			}

			if (i == 0) {
				title1.setText(list[i]);
			} else if (i == 1) {
				title2.setText(list[i]);
			} else if (i == 2) {
				title3.setText(list[i]);
			} else if (i == 3) {
				title4.setText(list[i]);
			} else {
				title5.setText(list[i]);
			}

		}
		
		sendToService(ServerService.MSG_LIST);

	}

	public void finishClick(int position) {
		if (listValues.size() > 0) {

			Intent i = new Intent();

			i.putExtra("Song", position);

			setResult(100, i);

			sendToService(ServerService.MSG_ACTIVITY);

			activityOn = false;
			service.unbind();
			finish();
		}
	}

	private void sendToService(int type) {
		Message message;
		switch (type) {
		case ServerService.MSG_LIST:
			message = Message.obtain(null, ServerService.MSG_LIST, 0, 0);
			ArrayList<String> titels = new ArrayList<String>();
			for (int i = 0; i < list.length; i++) {
				titels.add(list[i]);
			}

			ListMessage listMessage = new ListMessage(titels);
			message.getData().putParcelable("List", listMessage);
			break;
		case ServerService.MSG_ACTIVITY:
			message = Message.obtain(null, ServerService.MSG_ACTIVITY, 0, 0);

			ActivityMessage activityMessage = new ActivityMessage(
					ActivityMessage.BACK_TO_MAIN);
			message.getData().putParcelable("Activity", activityMessage);
			break;
		default:
			return;
		}

		try {
			service.send(message);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent start, MotionEvent finish,
			float xVelocity, float yVelocity) {

		final int SWIPE_THRESHOLD = 100;
		final int SWIPE_VELOCITY_THRESHOLD = 100;

		try {
			float diffY = finish.getY() - start.getY();
			float diffX = finish.getX() - start.getX();

			if (Math.abs(diffX) > Math.abs(diffY)) {
				if (Math.abs(diffX) > SWIPE_THRESHOLD
						&& Math.abs(xVelocity) > SWIPE_VELOCITY_THRESHOLD) {
					if (diffX > 0) {
						onSwipeRight();
					} else {
						onSwipeLeft();
					}
				}
			} else {
				if (Math.abs(diffY) > SWIPE_THRESHOLD
						&& Math.abs(yVelocity) > SWIPE_VELOCITY_THRESHOLD) {
					if (diffY > 0) {
						onSwipeDown();
					} else {
						onSwipeUp();
					}
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return true;
	}

	private void onSwipeLeft() {
		// TODO Auto-generated method stub

	}

	private void onSwipeRight() {
		sendToService(ServerService.MSG_ACTIVITY);
		activityOn = false;
		service.unbind();
		Intent i = new Intent();
		setResult(1, i);
		finish();

	}

	private void onSwipeDown() {
		if (position >= 5) {
			position = position - 5;

			fillList();
			
		}
	}

	private void onSwipeUp() {
		if (position < 5 && position >= 0 && listValues.size() > 5) {
			position = position + 5;

			fillList();
		}
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent me) {
		return gDetector.onTouchEvent(me);
	}
}
