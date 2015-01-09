package com.hudtouchscreen.touchscreenplayer;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import com.hudtouchscreen.hudmessage.ActivityMessage;
import com.hudtouchscreen.hudmessage.LoopingMessage;
import com.hudtouchscreen.hudmessage.ShuffleMessage;
import com.hudtouchscreen.hudmessage.SongTitleMessage;
import com.hudtouchscreen.hudmessage.TimeMessage;
import com.touchscreen.touchscreenplayer.R;

import Service.ServiceManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.view.GestureDetector.OnGestureListener;

/**
 * MusicPlayer is responsible for controlling the Music played and also acts as
 * a Server for other Clients
 * 
 * @author daniel
 * 
 */
@SuppressLint("NewApi")
public class MusicPlayer extends Activity implements OnGestureListener,
		OnSeekBarChangeListener {
	private WakeLock wakeLock;
	private static final String[] EXTENSIONS = { ".mp3", ".mid", ".wav",
			".ogg", ".mp4" }; // Playable Extensions
	private ArrayList<String> trackNames; // Playable Track Titles
	private AssetManager assets;
	private File path; // directory where music is loaded from on SD Card
	private Music track; // currently loaded track
	private ImageView playImage;
	private ImageView stopImage;
	private ImageView shuffleImage;
	private ImageView loopingImage;
	private ImageView startImage;
	private Random random;
	private boolean shuffle; // is shuffle mode on?
	private boolean looping;
	private boolean isTuning; // is user currently tuning, if so
								// automatically start playing the next track
	private int currentTrack; // index of current track selected
	private int type; // 0 for loading from assets, 1 for loading from SD card

	private GestureDetector gDetector;

	public TextView startTimeField, endTimeField;
	private double startTime = 0;
	private double endTime = 0;
	private Handler updateTime = new Handler();;
	private SeekBar seekbar;
	private boolean newTrack;
	private static boolean startingNewActivity;

	private ServiceManager service;

	@SuppressLint("HandlerLeak")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK,
				"Lexiconda");
		setContentView(R.layout.touchscreen);

		gDetector = new GestureDetector(this);
		initialize(0);

		this.service = new ServiceManager(this, ServerService.class,
				new Handler() {

					@Override
					public void handleMessage(Message msg) {

						switch (msg.what) {
						case ServerService.MSG_NEWCLIENT:
							sendToService(ServerService.MSG_SONGTITLE);

							sendToService(ServerService.MSG_SHUFFLE);

							sendToService(ServerService.MSG_LOOPING);

							sendToService(ServerService.MSG_TIME);
							break;

						default:
							super.handleMessage(msg);
						}
					}
				});

		service.start();

	}

	private synchronized void sendToService(int what) {
		Message message;
		switch (what) {
		case ServerService.MSG_REGISTER_CLIENT:
			message = Message.obtain(null, ServerService.MSG_REGISTER_CLIENT,
					0, 0);
			break;
		case ServerService.MSG_SONGTITLE:
			message = Message.obtain(null, ServerService.MSG_SONGTITLE, 0, 0);
			SongTitleMessage songTitle = new SongTitleMessage(new String(
					getTrackName()));
			message.getData().putParcelable("Songtitle", songTitle);

			break;
		case ServerService.MSG_SHUFFLE:
			message = Message.obtain(null, ServerService.MSG_SHUFFLE, 0, 0);
			ShuffleMessage shuffleMessage = new ShuffleMessage(shuffle);
			message.getData().putParcelable("Shuffle", shuffleMessage);

			break;
		case ServerService.MSG_LOOPING:
			message = Message.obtain(null, ServerService.MSG_LOOPING, 0, 0);
			LoopingMessage loopingMessage = new LoopingMessage(looping);
			message.getData().putParcelable("Looping", loopingMessage);

			break;
		case ServerService.MSG_TIME:
			message = Message.obtain(null, ServerService.MSG_TIME, 0, 0);
			TimeMessage timeMessage = new TimeMessage(new Double(startTime), new Double(endTime));
			message.getData().putParcelable("Time", timeMessage);
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

	/**
	 * Starts the MusicPlayer
	 * 
	 * @param type
	 */
	private void initialize(int type) {
		playImage = (ImageView) findViewById(R.id.play);
		playImage.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				synchronized (this) {
					if (isTuning) {
						isTuning = false;
						playImage.setImageResource(R.drawable.play);
						track.pause();
						updateTime.removeCallbacks(UpdateSongTime);

					} else {
						isTuning = true;
						playImage.setImageResource(R.drawable.pause);
						playTrack();
					}
					

				}
			}
		});

		stopImage = (ImageView) findViewById(R.id.stop);
		stopImage.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				synchronized (this) {
					isTuning = false;
					playImage.setImageResource(R.drawable.play);
					track.stop();
					updateTime.removeCallbacks(UpdateSongTime);
					setTime();
				}
			}
		});

		shuffleImage = (ImageView) findViewById(R.id.shuffle);
		shuffleImage.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				synchronized (this) {
					if (shuffle) {
						shuffle = false;
						shuffleImage.setBackgroundColor(Color.rgb(255, 255, 255));
					} else {
						shuffle = true;
						shuffleImage.setBackgroundColor(Color.rgb(100, 100, 50));
					}

					sendToService(ServerService.MSG_SHUFFLE);
				}
			}
		});

		startImage = (ImageView) findViewById(R.id.start);
		startImage.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
			}
		});

		loopingImage = (ImageView) findViewById(R.id.looping);
		loopingImage.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				synchronized (this) {

					if (looping) {
						looping = false;
						loopingImage.setBackgroundColor(Color.rgb(255, 255, 255));
					} else {
						looping = true;
						loopingImage.setBackgroundColor(Color.rgb(100, 100, 50));
					}

					track.setLooping(looping);
					sendToService(ServerService.MSG_LOOPING);

				}
			}
		});

		trackNames = new ArrayList<String>();
		assets = getAssets();
		currentTrack = 0;
		shuffle = false;
		looping = false;
		isTuning = false;
		random = new Random();
		this.type = type;
		newTrack = false;
		startingNewActivity = false;

		// startTimeField = (TextView) findViewById(R.id.startTime);
		// endTimeField = (TextView) findViewById(R.id.endTime);
		seekbar = (SeekBar) findViewById(R.id.seekBar1);
		seekbar.setOnSeekBarChangeListener(this);

		addTracks(getTracks());

		loadTrack();

	}

	@Override
	public void onResume() {
		super.onResume();
		wakeLock.acquire();
	}

	@Override
	public void onPause() {

		super.onPause();
		if (!startingNewActivity) {
			wakeLock.release();
			if (track != null) {
				if (track.isPlaying()) {
					track.pause();
					isTuning = false;

					playImage.setBackgroundResource(R.drawable.play);
				}
				if (isFinishing()) {
					track.dispose();
					finish();
				}
			} else {
				if (isFinishing()) {
					finish();
				}
			}
		}
	}

	/**
	 * Generate a String Array that represents all of the files found
	 * 
	 * @return
	 */
	private String[] getTracks() {
		if (type == 0) {
			try {
				String[] temp = getAssets().list("");
				return temp;
			} catch (IOException e) {
				e.printStackTrace();
				Toast.makeText(getBaseContext(), e.getMessage(),
						Toast.LENGTH_LONG).show();
			}
		} else if (type == 1) {
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)
					|| Environment.getExternalStorageState().equals(
							Environment.MEDIA_MOUNTED_READ_ONLY)) {
				path = Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
				String[] temp = path.list();
				return temp;
			} else {
				Toast.makeText(getBaseContext(),
						"SD Card is either mounted elsewhere or is unusable",
						Toast.LENGTH_LONG).show();
			}
		}
		return null;
	}

	/**
	 * Adds the playable files to the trackNames List
	 * 
	 * @param temp
	 */
	private void addTracks(String[] temp) {
		if (temp != null) {
			for (int i = 0; i < temp.length; i++) {
				// Only accept files that have one of the extensions in the
				// EXTENSIONS array
				if (trackChecker(temp[i])) {
					trackNames.add(temp[i]);
				}
			}
			Toast.makeText(
					getBaseContext(),
					"Loaded " + Integer.toString(trackNames.size()) + " Tracks",
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Checks to make sure that the track to be loaded has a correct extension
	 * 
	 * @param trackToTest
	 * @return
	 */
	private boolean trackChecker(String trackToTest) {
		for (int j = 0; j < EXTENSIONS.length; j++) {
			if (trackToTest.contains(EXTENSIONS[j])) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Loads the track by calling loadMusic
	 */
	private void loadTrack() {
		if (track != null) {
			track.dispose();

		}
		if (trackNames.size() > 0) {
			track = loadMusic(type);

			track.setLooping(looping);

			TextView songTitle = (TextView) findViewById(R.id.touch_title);

			songTitle.setText(getTrackName());
			newTrack = true;
		}

	}

	/**
	 * loads a Music instance using either a built in asset or an external
	 * 
	 * @param type
	 * @return
	 */
	private Music loadMusic(int type) {
		switch (type) {
		case 0:
			try {
				AssetFileDescriptor assetDescriptor = assets.openFd(trackNames
						.get(currentTrack));
				return new Music(assetDescriptor, this);
			} catch (IOException e) {
				e.printStackTrace();
				Toast.makeText(getBaseContext(),
						"Error Loading " + trackNames.get(currentTrack),
						Toast.LENGTH_LONG).show();
			}
			return null;
		case 1:
			try {
				FileInputStream fis = new FileInputStream(new File(path,
						trackNames.get(currentTrack)));
				FileDescriptor fileDescriptor = fis.getFD();
				return new Music(fileDescriptor, this);
			} catch (IOException e) {
				e.printStackTrace();
				Toast.makeText(getBaseContext(),
						"Error Loading " + trackNames.get(currentTrack),
						Toast.LENGTH_LONG).show();
			}
			return null;
		default:
			return null;
		}
	}


	/**
	 * Sets the track that is played next
	 * 
	 * @param direction
	 */
	private void setTrack(int direction) {

		if (shuffle) {
			int temp = random.nextInt(trackNames.size());

			while (trackNames.size() != 1) {
				if (temp != currentTrack) {
					currentTrack = temp;
					break;
				}
				temp++;
				if (temp > trackNames.size() - 1) {
					temp = 0;
				}

			}
		} else {
			if (direction == 0) {
				currentTrack--;
				if (currentTrack < 0) {
					currentTrack = trackNames.size() - 1;
				}
			} else if (direction == 1) {
				currentTrack++;
				if (currentTrack > trackNames.size() - 1) {
					currentTrack = 0;
				}
			}
		}

		sendToService(ServerService.MSG_SONGTITLE);
	}

	/**
	 * Plays the Track
	 */
	@SuppressLint("NewApi")
	private void playTrack() {
		if (isTuning && track != null) {

			track.play();

			setTime();
			updateTime.postDelayed(UpdateSongTime, 100);
		}

	}

	private void setTime() {
		endTime = track.getFinalTime();
		startTime = track.getStartTime();

		if (newTrack) {
			seekbar.setMax((int) endTime);
			newTrack = false;
		}

		/*
		 * endTimeField.setText(String.format( " %dmin %dsec",
		 * TimeUnit.MILLISECONDS.toMinutes((long) endTime),
		 * TimeUnit.MILLISECONDS.toSeconds((long) endTime) -
		 * TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS .toMinutes((long)
		 * endTime)))); startTimeField.setText(String.format( "%dmin %dsec",
		 * TimeUnit.MILLISECONDS.toMinutes((long) startTime),
		 * TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
		 * TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS .toMinutes((long)
		 * startTime))));
		 */
		seekbar.setProgress((int) startTime);

		sendToService(ServerService.MSG_TIME);
	}

	private Runnable UpdateSongTime = new Runnable() {
		public void run() {
			startTime = track.getStartTime();
			/*
			 * startTimeField.setText(String.format( "%dmin %dsec",
			 * TimeUnit.MILLISECONDS.toMinutes((long) startTime),
			 * TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
			 * TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
			 * .toMinutes((long) startTime))));
			 */
			seekbar.setProgress((int) startTime);
			sendToService(ServerService.MSG_TIME);

			updateTime.postDelayed(this, 100);

		}
	};

	/**
	 * Gets the name of the track
	 * 
	 * @return
	 */
	protected String getTrackName() {
		return trackNames.get(currentTrack).substring(0,
				trackNames.get(currentTrack).length() - 4);
	}

	protected void nextTrack() {
		setTrack(1);
		loadTrack();
		playTrack();
	}

	private void switchTrack(int position) {
		currentTrack = position;
		loadTrack();
		playTrack();
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return true;
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

	public void onSwipeRight() {
		setTrack(0);
		loadTrack();
		playTrack();
	}

	public void onSwipeLeft() {
		setTrack(1);
		loadTrack();
		playTrack();
	}

	public void onSwipeUp() {

	}

	public void onSwipeDown() {
		switchToList();
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
	
	private void switchToList() {
		startingNewActivity = true;
		Intent i = new Intent(getApplicationContext(), MusikList.class);
		i.putStringArrayListExtra("Track Names", trackNames);

		Message message = Message.obtain(null, ServerService.MSG_ACTIVITY, 0, 0);
		
		ActivityMessage activityMessage = new ActivityMessage(ActivityMessage.SWITCH_TO_LIST);
		message.getData().putParcelable("Activity", activityMessage);
		
		try {
			service.send(message);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		startActivityForResult(i, 100);
	
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		startingNewActivity = false;

		if (resultCode == 100) {
			data.getExtras();
			int position = data.getIntExtra("Song", -1);

			if (position != -1) {
				switchTrack(position);
			}

		}

		sendToService(ServerService.MSG_REGISTER_CLIENT);
		sendToService(ServerService.MSG_SONGTITLE);
		sendToService(ServerService.MSG_SHUFFLE);
		sendToService(ServerService.MSG_LOOPING);
		sendToService(ServerService.MSG_TIME);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		int progress = seekbar.getProgress();

		track.seek(progress);
		// seekbar.setSecondaryProgress(progress);
	}

}