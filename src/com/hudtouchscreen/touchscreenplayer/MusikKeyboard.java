package com.hudtouchscreen.touchscreenplayer;

import com.hudtouchscreen.hudmessage.ActivityMessage;
import com.hudtouchscreen.hudmessage.TextMessage;
import com.touchscreen.touchscreenplayer.R;

import Service.ServiceManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.widget.EditText;

public class MusikKeyboard extends Activity implements OnGestureListener {

	private ServiceManager service;
	private boolean activityOn;

	private EditText keyBoardText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.keyboard);
		activityOn = true;
		keyBoardText = (EditText) findViewById(R.id.editKeyBoard);

		this.service = new ServiceManager(this, ServerService.class,
				new Handler() {

					@Override
					public void handleMessage(Message msg) {
						final Bundle bundle = msg.getData();
						bundle.setClassLoader(getClassLoader());

						switch (msg.what) {
						case ServerService.MSG_NEWCLIENT:
						default:
							super.handleMessage(msg);
							break;
						}
					}
				});

		service.start();

	}

	private void finishTyping() {
		Intent i = new Intent();

		i.putExtra("Keyboard", keyBoardText.getText().toString());

		setResult(200, i);

		sendToService(ServerService.MSG_ACTIVITY);

		activityOn = false;
		service.unbind();
		finish();
	}

	private void sendToService(int type) {
		Message message;
		switch (type) {
		case ServerService.MSG_TEXT:
			message = Message.obtain(null, ServerService.MSG_TEXT, 0, 0);
			TextMessage testMessage = new TextMessage(keyBoardText.getText()
					.toString());
			message.getData().putParcelable("Text", testMessage);
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

	public void click(View view) {
		int id = view.getId();

		if (id == R.id.q) {
			if (keyBoardText.getText().length() == 0) {
				keyBoardText.append("Q");
			} else {
				keyBoardText.append("q");
			}
		} else if (id == R.id.w) {
			if (keyBoardText.getText().length() == 0) {
				keyBoardText.append("W");
			} else {
				keyBoardText.append("w");
			}
		} else if (id == R.id.e) {
			if (keyBoardText.getText().length() == 0) {
				keyBoardText.append("E");
			} else {
				keyBoardText.append("e");
			}
		} else if (id == R.id.r) {
			if (keyBoardText.getText().length() == 0) {
				keyBoardText.append("R");
			} else {
				keyBoardText.append("r");
			}
		} else if (id == R.id.t) {
			if (keyBoardText.getText().length() == 0) {
				keyBoardText.append("T");
			} else {
				keyBoardText.append("t");
			}
		} else if (id == R.id.z) {
			if (keyBoardText.getText().length() == 0) {
				keyBoardText.append("Z");
			} else {
				keyBoardText.append("z");
			}
		} else if (id == R.id.u) {
			if (keyBoardText.getText().length() == 0) {
				keyBoardText.append("U");
			} else {
				keyBoardText.append("u");
			}
		} else if (id == R.id.i) {
			if (keyBoardText.getText().length() == 0) {
				keyBoardText.append("I");
			} else {
				keyBoardText.append("i");
			}
		} else if (id == R.id.o) {
			if (keyBoardText.getText().length() == 0) {
				keyBoardText.append("O");
			} else {
				keyBoardText.append("o");
			}
		} else if (id == R.id.p) {
			if (keyBoardText.getText().length() == 0) {
				keyBoardText.append("P");
			} else {
				keyBoardText.append("p");
			}
		} else if (id == R.id.a) {
			if (keyBoardText.getText().length() == 0) {
				keyBoardText.append("A");
			} else {
				keyBoardText.append("a");
			}
		} else if (id == R.id.s) {
			if (keyBoardText.getText().length() == 0) {
				keyBoardText.append("S");
			} else {
				keyBoardText.append("s");
			}
		} else if (id == R.id.d) {
			if (keyBoardText.getText().length() == 0) {
				keyBoardText.append("D");
			} else {
				keyBoardText.append("d");
			}
		} else if (id == R.id.f) {
			if (keyBoardText.getText().length() == 0) {
				keyBoardText.append("F");
			} else {
				keyBoardText.append("f");
			}
		} else if (id == R.id.g) {
			if (keyBoardText.getText().length() == 0) {
				keyBoardText.append("G");
			} else {
				keyBoardText.append("g");
			}
		} else if (id == R.id.h) {
			if (keyBoardText.getText().length() == 0) {
				keyBoardText.append("H");
			} else {
				keyBoardText.append("h");
			}
		} else if (id == R.id.j) {
			if (keyBoardText.getText().length() == 0) {
				keyBoardText.append("J");
			} else {
				keyBoardText.append("j");
			}
		} else if (id == R.id.k) {
			if (keyBoardText.getText().length() == 0) {
				keyBoardText.append("K");
			} else {
				keyBoardText.append("k");
			}
		} else if (id == R.id.l) {
			if (keyBoardText.getText().length() == 0) {
				keyBoardText.append("L");
			} else {
				keyBoardText.append("l");
			}
		} else if (id == R.id.y) {
			if (keyBoardText.getText().length() == 0) {
				keyBoardText.append("Y");
			} else {
				keyBoardText.append("y");
			}
		} else if (id == R.id.x) {
			if (keyBoardText.getText().length() == 0) {
				keyBoardText.append("X");
			} else {
				keyBoardText.append("x");
			}
		} else if (id == R.id.c) {
			if (keyBoardText.getText().length() == 0) {
				keyBoardText.append("C");
			} else {
				keyBoardText.append("c");
			}
		} else if (id == R.id.v) {
			if (keyBoardText.getText().length() == 0) {
				keyBoardText.append("V");
			} else {
				keyBoardText.append("v");
			}
		} else if (id == R.id.b) {
			if (keyBoardText.getText().length() == 0) {
				keyBoardText.append("B");
			} else {
				keyBoardText.append("b");
			}
		} else if (id == R.id.n) {
			if (keyBoardText.getText().length() == 0) {
				keyBoardText.append("N");
			} else {
				keyBoardText.append("n");
			}
		} else if (id == R.id.m) {
			if (keyBoardText.getText().length() == 0) {
				keyBoardText.append("M");
			} else {
				keyBoardText.append("m");
			}
		} else if (id == R.id.delete) {
			if (keyBoardText.getText().length() > 0) {
				keyBoardText.getText().delete(
						keyBoardText.getText().length() - 1,
						keyBoardText.getText().length());
			}
		} else if (id == R.id.enter) {
			finishTyping();
			return;
		} else {
			return;
		}

		sendToService(ServerService.MSG_TEXT);

	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
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
	}

	private void onSwipeUp() {
	}
	
	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
}
