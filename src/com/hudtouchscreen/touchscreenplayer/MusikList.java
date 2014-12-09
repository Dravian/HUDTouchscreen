package com.hudtouchscreen.touchscreenplayer;

import java.util.List;

import com.touchscreen.touchscreenplayer.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_search);
		position = 0;
		list = new String[5];

		gDetector = new GestureDetector(this, this);

		View.OnTouchListener gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return gDetector.onTouchEvent(event);
			}
		};

		title1 = (TextView) findViewById(R.id.title1);
		title1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				finishClick(position);
			}
		});
		title1.setOnTouchListener(gestureListener);

		title2 = (TextView) findViewById(R.id.title2);
		title2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				finishClick(position + 1);
			}
		});
		title2.setOnTouchListener(gestureListener);

		title3 = (TextView) findViewById(R.id.title3);
		title3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				finishClick(position + 2);
			}
		});
		title3.setOnTouchListener(gestureListener);

		title4 = (TextView) findViewById(R.id.title4);
		title4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				finishClick(position + 3);
			}
		});
		title4.setOnTouchListener(gestureListener);

		title5 = (TextView) findViewById(R.id.title5);
		title5.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				finishClick(position + 4);
			}
		});
		title5.setOnTouchListener(gestureListener);

		Intent intent = getIntent();
		listValues = intent.getStringArrayListExtra("Track Names");

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
	}

	public void finishClick(int position) {
		if (listValues.size() > 0) {

			Intent i = new Intent();

			i.putExtra("Song", position);

			setResult(100, i);
			finish();
		}
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
		// TODO Auto-generated method stub

	}

	private void onSwipeDown() {
		if (position >= 5) {
			position = position - 5;

			fillList();
		}
	}

	private void onSwipeUp() {
		if (position < 5) {
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
