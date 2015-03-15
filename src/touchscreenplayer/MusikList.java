package touchscreenplayer;

import hudmessage.ActivityMessage;
import hudmessage.ListMessage;
import hudmessage.TouchMessage;

import java.util.ArrayList;
import java.util.List;

import service.ServiceManager;

import com.touchscreen.touchscreenplayer.R;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.GestureDetector.OnGestureListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MusikList extends Activity {
	private List<String> listValues;
	private String[] list;
	private int position;
	private TextView title1;
	private TextView title2;
	private TextView title3;
	private TextView title4;
	private TextView title5;
	private GestureDetector gDetector;
	private TouchListener touchListener;
	private final Handler CHECK_STATE = new Handler();
	private boolean logging;
	private ServiceManager service;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_search);
		position = 0;
		list = new String[5];

		Intent intent = getIntent();
		listValues = intent.getStringArrayListExtra("Track Names");

		gDetector = new GestureDetector(this, new GestureListener());
		touchListener = new TouchListener();
		

		this.service = new ServiceManager(this, ServerService.class,
				new Handler() {

					@Override
					public void handleMessage(Message msg) {
						final Bundle bundle = msg.getData();
						bundle.setClassLoader(getClassLoader());

						switch (msg.what) {
						case ServerService.MSG_NEWCLIENT:

							sendToService(ServerService.MSG_NEWCLIENT);
							sendToService(ServerService.MSG_LIST);
						default:
							super.handleMessage(msg);

						}
					}
				});

		service.start();

		title1 = (TextView) findViewById(R.id.title1);
		title2 = (TextView) findViewById(R.id.title2);
		title3 = (TextView) findViewById(R.id.title3);
		title4 = (TextView) findViewById(R.id.title4);
		title5 = (TextView) findViewById(R.id.title5);

		fillList();

		if (UserLogger.getState() == UserLogger.State.OFF) {
			logging = false;
		} else if (UserLogger.getState() == UserLogger.State.IDLE) {
			logging = false;
			CHECK_STATE.postDelayed(stateStatus, 100);
		} else {
			logging = true;
			CHECK_STATE.postDelayed(stateStatus, 100);
		}
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

	private Runnable stateStatus = new Runnable() {
		public void run() {
			UserLogger.State taskState = UserLogger.getState();

			switch (taskState) {
			case OFF:
				if (logging) {
					onSwipeRight();
					logging = false;
				}
				return;
			case IDLE:
				if (logging) {
					onSwipeRight();
					logging = false;
				}
				;
				return;
			default:
				logging = true;
				break;
			}
			CHECK_STATE.postDelayed(this, 100);
		}

	};

	public void finishClick(int position) {
		if (listValues.size() > 0) {

			UserLogger.logAction(UserLogger.UserView.LIST,
					UserLogger.Action.CLICK_ITEM, listValues.get(position),
					position);

			Intent i = new Intent();

			i.putExtra("Song", position);

			setResult(100, i);

			sendToService(ServerService.MSG_ACTIVITY);

			service.unbind();
			finish();
		}
	}

	private void sendToService(int type) {
		Message message;
		switch (type) {
		case ServerService.MSG_LIST:
			message = Message.obtain(null, ServerService.MSG_LIST, 0, 0);

			ArrayList<String> titleList = new ArrayList<String>();

			titleList.add(list[0]);
			titleList.add(list[1]);
			titleList.add(list[2]);
			titleList.add(list[3]);
			titleList.add(list[4]);

			ListMessage listMessage = new ListMessage(titleList);
			message.getData().putParcelable("List", listMessage);

			break;
		case ServerService.MSG_ACTIVITY:
			message = Message.obtain(null, ServerService.MSG_ACTIVITY, 0, 0);

			ActivityMessage activityMessage = new ActivityMessage(
					ActivityMessage.BACK_TO_MAIN, null);
			message.getData().putParcelable("Activity", activityMessage);
			break;
		case ServerService.MSG_NEWCLIENT:
			message = Message.obtain(null, ServerService.MSG_ACTIVITY, 0, 0);

			ArrayList<String> titles = new ArrayList<String>();
			titles.add(list[0]);
			titles.add(list[1]);
			titles.add(list[2]);
			titles.add(list[3]);
			titles.add(list[4]);

			ActivityMessage newClientMessage = new ActivityMessage(
					ActivityMessage.SWITCH_TO_LIST, titles);
			message.getData().putParcelable("Activity", newClientMessage);
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
	public boolean onTouchEvent(MotionEvent me) {

		return touchListener.onTouch(me);
	}

	@Override
	protected void onStop() {
		sendToService(ServerService.MSG_ACTIVITY);
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		sendToService(ServerService.MSG_ACTIVITY);
		super.onDestroy();

		try {
			service.unbind();
		} catch (Throwable t) {
		}

	}

	private void onSwipeLeft() {
		UserLogger.logAction(UserLogger.UserView.LIST,
				UserLogger.Action.SWIPE_LEFT, "", -1);

	}

	private void onSwipeRight() {
		UserLogger.logAction(UserLogger.UserView.LIST,
				UserLogger.Action.SWIPE_RIGHT, "", -1);

		sendToService(ServerService.MSG_ACTIVITY);
		service.unbind();
		Intent i = new Intent();
		setResult(1, i);
		finish();

	}

	private void onSwipeDown() {
		UserLogger.logAction(UserLogger.UserView.LIST,
				UserLogger.Action.SWIPE_DOWN, "", position);

		if (position >= 5) {
			position = position - 5;

			fillList();

		}

	}

	private void onSwipeUp() {
		UserLogger.logAction(UserLogger.UserView.LIST,
				UserLogger.Action.SWIPE_UP, "", position);

		if (listValues.size() >= 5 + position) {
			position = position + 5;

			fillList();
		}
	}
	
	boolean longPress = false;

	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		longPress = true;
		return super.onKeyLongPress(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {

		if (longPress && keyCode == KeyEvent.KEYCODE_BACK) {
			longPress = false;
			UserLogger.logAction(UserLogger.UserView.PLAYER, UserLogger.Action.KEY_BACK, "",-1);
			UserLogger.stopLog();
			CHECK_STATE.postDelayed(stateStatus, 100);
			return super.onKeyUp(keyCode, event);
		}
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		event.startTracking();
		return true;
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		if (!hasFocus) {
			windowCloseHandler.postDelayed(windowCloserRunnable, 0);
		}
	}

	private void toggleRecents() {
		Intent closeRecents = new Intent(
				"com.android.systemui.recent.action.TOGGLE_RECENTS");
		closeRecents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		ComponentName recents = new ComponentName("com.android.systemui",
				"com.android.systemui.recent.RecentsActivity");
		closeRecents.setComponent(recents);
		this.startActivity(closeRecents);
	}

	private Handler windowCloseHandler = new Handler();
	private Runnable windowCloserRunnable = new Runnable() {

		@Override
		public void run() {
			ActivityManager am = (ActivityManager) getApplicationContext()
					.getSystemService(Context.ACTIVITY_SERVICE);
			ComponentName cn = am.getRunningTasks(1).get(0).topActivity;

			if (cn != null
					&& cn.getClassName().equals(
							"com.android.systemui.recent.RecentsActivity")) {
				toggleRecents();
			}
		}
	};

	protected class TouchListener {
		private final Handler handler = new Handler();
		private float mDownX = 0;
		private float mDownY = 0;
		private Rect rect1;
		private Rect rect2;
		private Rect rect3;
		private Rect rect4;
		private Rect rect5;
		private boolean ignoreFling = false;
		private int buttonType = -1;

		LinearLayout layout = (LinearLayout) findViewById(R.id.listview);
		ViewTreeObserver vto = layout.getViewTreeObserver();

		{
			layout.post(new Runnable() {

				@Override
				public void run() {
					final ViewTreeObserver vto = layout.getViewTreeObserver();

					vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
						@Override
						public void onGlobalLayout() {

							drawRect();
							vto.removeGlobalOnLayoutListener(this);

						}
					});
				}

			});
		}

		private void drawRect() {

			int[] coord1 = new int[2];
			title1.getLocationOnScreen(coord1);
			int coord1X = coord1[0];
			int coord1Y = coord1[1];

			int[] coord2 = new int[2];
			title2.getLocationOnScreen(coord2);
			int coord2X = coord2[0];
			int coord2Y = coord2[1];

			int[] coord3 = new int[2];
			title3.getLocationOnScreen(coord3);
			int coord3X = coord3[0];
			int coord3Y = coord3[1];

			int[] coord4 = new int[2];
			title4.getLocationOnScreen(coord4);
			int coord4X = coord4[0];
			int coord4Y = coord4[1];

			int[] coord5 = new int[2];
			title5.getLocationOnScreen(coord5);
			int coord5X = coord5[0];
			int coord5Y = coord5[1];

			rect1 = new Rect(coord1X, coord1Y, coord1X + title1.getWidth(),
					coord1Y + title1.getHeight());

			rect2 = new Rect(coord2X, coord2Y, coord2X + title1.getWidth(),
					coord2Y + title2.getHeight());

			rect3 = new Rect(coord3X, coord3Y, coord3X + title3.getWidth(),
					coord3Y + title4.getHeight());

			rect4 = new Rect(coord4X, coord4Y, coord4X + title4.getWidth(),
					coord4Y + title4.getHeight());

			rect5 = new Rect(coord5X, coord5Y, coord5X + title5.getWidth(),
					coord5Y + title5.getHeight());

		}

		Runnable mLongPressed = new Runnable() {
			public void run() {
				ignoreFling = true;
				buttonOnTouch(true);
			}
		};

		public boolean onTouch(MotionEvent event) {

			if (!ignoreFling && gDetector.onTouchEvent(event)) {
				handler.removeCallbacks(mLongPressed);
				buttonOnTouch(false);
				return false;

			} else {

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:

					mDownX = event.getX();
					mDownY = event.getY();

					if (rect1.contains((int) event.getX(), (int) event.getY())) {
						buttonType = TouchMessage.LIST1;

					} else if (rect2.contains((int) event.getX(),
							(int) event.getY())) {
						buttonType = TouchMessage.LIST2;

					} else if (rect3.contains((int) event.getX(),
							(int) event.getY())) {
						buttonType = TouchMessage.LIST3;

					} else if (rect4.contains((int) event.getX(),
							(int) event.getY())) {
						buttonType = TouchMessage.LIST4;

					} else if (rect5.contains((int) event.getX(),
							(int) event.getY())) {
						buttonType = TouchMessage.LIST5;

					} else {
						buttonType = -1;
					}
					handler.postDelayed(mLongPressed, 200);
					return true;

				case MotionEvent.ACTION_UP:
					handler.removeCallbacks(mLongPressed);
					buttonOnTouch(false);

					if (rect1.contains((int) event.getX(), (int) event.getY())) {
						buttonType = TouchMessage.LIST1;
						click();

					} else if (rect2.contains((int) event.getX(),
							(int) event.getY())) {
						buttonType = TouchMessage.LIST2;
						click();

					} else if (rect3.contains((int) event.getX(),
							(int) event.getY())) {
						buttonType = TouchMessage.LIST3;
						click();

					} else if (rect4.contains((int) event.getX(),
							(int) event.getY())) {
						buttonType = TouchMessage.LIST4;
						click();

					} else if (rect5.contains((int) event.getX(),
							(int) event.getY())) {
						buttonType = TouchMessage.LIST5;
						click();
					}
					
					ignoreFling = false;
					buttonType = -1;
					return false;

				case MotionEvent.ACTION_CANCEL:
					handler.removeCallbacks(mLongPressed);

					buttonOnTouch(false);
					buttonType = -1;
					ignoreFling = false;
					return false;

				case MotionEvent.ACTION_MOVE:
					int SCROLL_THRESHOLD = 10;

					if ((Math.abs(mDownX - event.getX()) > SCROLL_THRESHOLD || Math
							.abs(mDownY - event.getY()) > SCROLL_THRESHOLD)) {

						if (rect1.contains((int) event.getX(),
								(int) event.getY())) {

							if (buttonType != TouchMessage.LIST1) {
								handler.removeCallbacks(mLongPressed);
								buttonOnTouch(false);
								handler.postDelayed(mLongPressed, 200);
							}
							buttonType = TouchMessage.LIST1;

						} else if (rect2.contains((int) event.getX(),
								(int) event.getY())) {

							if (buttonType != TouchMessage.LIST2) {
								handler.removeCallbacks(mLongPressed);
								buttonOnTouch(false);
								handler.postDelayed(mLongPressed, 200);
							}

							buttonType = TouchMessage.LIST2;

						} else if (rect3.contains((int) event.getX(),
								(int) event.getY())) {

							if (buttonType != TouchMessage.LIST3) {
								handler.removeCallbacks(mLongPressed);
								buttonOnTouch(false);
								handler.postDelayed(mLongPressed, 200);
							}

							buttonType = TouchMessage.LIST3;

						} else if (rect4.contains((int) event.getX(),
								(int) event.getY())) {

							if (buttonType != TouchMessage.LIST4) {
								handler.removeCallbacks(mLongPressed);
								buttonOnTouch(false);
								handler.postDelayed(mLongPressed, 200);
							}

							buttonType = TouchMessage.LIST4;

						} else if (rect5.contains((int) event.getX(),
								(int) event.getY())) {

							if (buttonType != TouchMessage.LIST5) {
								handler.removeCallbacks(mLongPressed);
								buttonOnTouch(false);
								handler.postDelayed(mLongPressed, 200);
							}

							buttonType = TouchMessage.LIST5;

						} else {
							if (buttonType != -1) {
								handler.removeCallbacks(mLongPressed);
								buttonOnTouch(false);
								buttonType = -1;
							}
						}

						return true;
					}
				}

			}
			return false;
		}

		private void click() {

			if (buttonType == TouchMessage.LIST1) {
				if (list[0] != "") {
					title1.playSoundEffect(android.view.SoundEffectConstants.CLICK);
					finishClick(position);
				}

			} else if (buttonType == TouchMessage.LIST2) {
				title2.playSoundEffect(android.view.SoundEffectConstants.CLICK);
				if (list[1] != "") {
					finishClick(position + 1);
				}
			} else if (buttonType == TouchMessage.LIST3) {
				title3.playSoundEffect(android.view.SoundEffectConstants.CLICK);
				if (list[2] != "") {
					finishClick(position + 2);
				}

			} else if (buttonType == TouchMessage.LIST4) {
				title4.playSoundEffect(android.view.SoundEffectConstants.CLICK);
				if (list[3] != "") {
					finishClick(position + 3);
				}

			} else if (buttonType == TouchMessage.LIST5) {
				title5.playSoundEffect(android.view.SoundEffectConstants.CLICK);
				if (list[4] != "") {
					finishClick(position + 4);
				}
			}
		}

		private void buttonOnTouch(boolean touching) {
			TextView view;

			switch (buttonType) {
			case TouchMessage.LIST1:
				view = title1;
				break;
			case TouchMessage.LIST2:
				view = title2;
				break;
			case TouchMessage.LIST3:
				view = title3;
				break;
			case TouchMessage.LIST4:
				view = title4;
				break;
			case TouchMessage.LIST5:
				view = title5;
				break;
			default:
				view = null;
			}

			if (touching && view != null) {

				view.setBackgroundResource(R.color.red);
			} else if (view != null) {
				view.setBackgroundResource(R.color.black);
			}

			Message message = Message.obtain(null, ServerService.MSG_TOUCH, 0,
					0);

			TouchMessage touchMessage = new TouchMessage(buttonType, touching);

			message.getData().putParcelable("Touch", touchMessage);

			try {
				service.send(message);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

	}

	public class GestureListener implements OnGestureListener {

		@Override
		public boolean onDown(MotionEvent e) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean onFling(MotionEvent start, MotionEvent finish,
				float xVelocity, float yVelocity) {

			final int SWIPE_THRESHOLD = 100;
			final int SWIPE_VELOCITY_THRESHOLD = 120;

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

		@Override
		public void onLongPress(MotionEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {

		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// TODO Auto-generated method stub

			return false;

		}

	}
}
