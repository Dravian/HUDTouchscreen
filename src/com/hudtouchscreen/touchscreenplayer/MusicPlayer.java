package com.hudtouchscreen.touchscreenplayer;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.hudtouchscreen.message.LoopingMessage;
import com.hudtouchscreen.message.Message;
import com.hudtouchscreen.message.TimeMessage;
import com.hudtouchscreen.message.ShuffleMessage;
import com.hudtouchscreen.message.SongTitleMessage;
import com.touchscreen.touchscreenplayer.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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
	private List<String> trackNames; // Playable Track Titles
	private AssetManager assets;
	private File path; // directory where music is loaded from on SD Card
	private Music track; // currently loaded track
	private Button btnPlay;
	private Random random;
	private boolean shuffle; // is shuffle mode on?
	private boolean looping;
	private boolean isTuning; // is user currently tuning, if so
								// automatically start playing the next track
	private int currentTrack; // index of current track selected
	private int type; // 0 for loading from assets, 1 for loading from SD card

	private Set<Client> clients;
	private GestureDetector gDetector;

	private ClientListener clientListener;

	public TextView startTimeField, endTimeField;
	private double startTime = 0;
	private double finalTime = 0;
	private Handler updateTime = new Handler();;
	private SeekBar seekbar;
	private boolean newTrack;
	private static boolean startingNewActivity;

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
		setContentView(R.layout.activity_player);

		gDetector = new GestureDetector(this);
		initialize(0);

		clients = new HashSet<Client>();
		final int PORT = 8000;

		clientListener = new ClientListener(PORT, this);
		Thread clientListenerThread = new Thread(clientListener);
		clientListenerThread.start();

	}

	/**
	 * Starts the MusicPlayer
	 * 
	 * @param type
	 */
	private void initialize(int type) {
		btnPlay = (Button) findViewById(R.id.btnPlay);
		btnPlay.setBackgroundResource(R.drawable.play);
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

		startTimeField = (TextView) findViewById(R.id.startTime);
		endTimeField = (TextView) findViewById(R.id.endTime);
		seekbar = (SeekBar) findViewById(R.id.seekBar1);
		seekbar.setOnSeekBarChangeListener(this);

		addTracks(getTracks());

		loadTrack();
	}

	/**
	 * Adds a new Client the Server
	 * 
	 * @param client
	 */
	protected void addClient(Client client) {

		clients.add(client);
		client.send(new SongTitleMessage(getTrackName()));
		client.send(new ShuffleMessage(shuffle));
		client.send(new LoopingMessage(track.isLooping()));
		client.send(new TimeMessage(startTime, finalTime));
	}

	/**
	 * Broadcasts a message to all Clients
	 * 
	 * @param message
	 */
	private void broadcast(Message message) {
		for (Client client : clients) {
			client.send(message);
		}
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
					btnPlay.setBackgroundResource(R.drawable.play);
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
	 * Starts a command depending on the button a user clicked
	 * 
	 * @param view
	 */
	public void click(View view) {

		if (trackNames.size() > 0) {
			int id = view.getId();
			switch (id) {
			case R.id.btnPlay:
				synchronized (this) {
					if (isTuning) {
						isTuning = false;
						btnPlay.setBackgroundResource(R.drawable.play);
						track.pause();
						updateTime.removeCallbacks(UpdateSongTime);

					} else {
						isTuning = true;
						btnPlay.setBackgroundResource(R.drawable.pause);
						playTrack();
					}
				}
				return;
			case R.id.btnStop:
				synchronized (this) {
					isTuning = false;
					btnPlay.setBackgroundResource(R.drawable.play);
					track.stop();
					updateTime.removeCallbacks(UpdateSongTime);
					setTime();
				}
				return;
			case R.id.btnShuffle:
				synchronized (this) {
					Button btnShuffle = (Button) findViewById(R.id.btnShuffle);
					// switchToList();

					if (shuffle) {
						shuffle = false;
						btnShuffle.setBackgroundResource(R.drawable.shuffleoff);
					} else {
						shuffle = true;
						btnShuffle.setBackgroundResource(R.drawable.shuffleon);
					}

					broadcast(new ShuffleMessage(shuffle));

				}
				return;
			case R.id.btnLooping:
				synchronized (this) {
					Button btnLooping = (Button) findViewById(R.id.btnLooping);

					if (looping) {
						looping = false;
						btnLooping.setBackgroundResource(R.drawable.loopingoff);

					} else {
						looping = true;
						btnLooping.setBackgroundResource(R.drawable.loopingon);
					}

					track.setLooping(looping);
					broadcast(new LoopingMessage(looping));

				}
			default:
				return;
			}
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

		broadcast(new SongTitleMessage(getTrackName()));

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
		finalTime = track.getFinalTime();
		startTime = track.getStartTime();

		if (newTrack) {
			seekbar.setMax((int) finalTime);
			newTrack = false;
		}

		endTimeField.setText(String.format(
				" %dmin %dsec",
				TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
				TimeUnit.MILLISECONDS.toSeconds((long) finalTime)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
								.toMinutes((long) finalTime))));
		startTimeField.setText(String.format(
				"%dmin %dsec",
				TimeUnit.MILLISECONDS.toMinutes((long) startTime),
				TimeUnit.MILLISECONDS.toSeconds((long) startTime)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
								.toMinutes((long) startTime))));
		seekbar.setProgress((int) startTime);

		broadcast(new TimeMessage(startTime, finalTime));
	}

	/*
	private void switchToList() {
		startingNewActivity = true;
		Intent i = new Intent(getApplicationContext(), ListView.class);
		startActivityForResult(i, 100);
	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		startingNewActivity = false;

		if (resultCode == 100) {
			data.getExtras();
			// Storing result in a variable called myvar
			// get("website") 'website' is the key value result data
			int song = data.getIntExtra("Song", -1);

			if (song != -1) {

			}

		}

	}*/

	private Runnable UpdateSongTime = new Runnable() {
		public void run() {
			startTime = track.getStartTime();
			startTimeField.setText(String.format(
					"%dmin %dsec",
					TimeUnit.MILLISECONDS.toMinutes((long) startTime),
					TimeUnit.MILLISECONDS.toSeconds((long) startTime)
							- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
									.toMinutes((long) startTime))));
			seekbar.setProgress((int) startTime);

			broadcast(new TimeMessage(startTime, finalTime));
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

	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent start, MotionEvent finish,
			float xVelocity, float yVelocity) {
		if (start.getRawX() < finish.getRawX()) {
			setTrack(0);
			loadTrack();
			playTrack();
		} else if (start.getRawX() > finish.getRawX()) {
			setTrack(1);
			loadTrack();
			playTrack();
		}
		return true;
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

	@Override
	protected void onStop() {
		for (Client client : clients) {
			client.closeConnection();
		}
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