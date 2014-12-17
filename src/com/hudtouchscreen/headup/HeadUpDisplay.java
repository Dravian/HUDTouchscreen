package com.hudtouchscreen.headup;

import com.hudtouchscreen.hudmessage.LoopingMessage;
import com.hudtouchscreen.hudmessage.HudMessage;
import com.hudtouchscreen.hudmessage.ShuffleMessage;
import com.hudtouchscreen.hudmessage.SongTitleMessage;
import com.hudtouchscreen.hudmessage.TimeMessage;
import com.touchscreen.touchscreenplayer.R;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class HeadUpDisplay extends Activity {

	/**
	 * Der TCP Socket.
	 */
	private Socket connection;

	/**
	 * Referenz auf den Ausgabestrom.
	 */
	private ObjectInputStream in;

	private OutputStream out;

	/**
	 * Signalisiert ob ein TCP Socket aktiv ist.
	 */
	private boolean socketSet;

	private final String SERVER_IP = "10.0.2.2";

	private final int PORT = 7000;
	
	public TextView startTimeField, endTimeField;
	private double startTime = 0;
	private double finalTime = 0;
	private Handler myHandler = new Handler();;
	private SeekBar seekbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_head_up_display);

		startTimeField = (TextView) findViewById(R.id.startTime);
		endTimeField = (TextView) findViewById(R.id.endTime);
		seekbar = (SeekBar) findViewById(R.id.seekBar1);
		seekbar.setClickable(false);
		
		// PACKETS TO PORT 7000 GET REDIRECTED TO THE SERVER EMULATOR'S PORT
		// 8000
		Thread playerListener = new Thread(new PlayerListener());
		playerListener.start();
	}

	/**
	 * Initialisiert die ObjectStreams und speichert den TCP Socket im Thread.
	 * 
	 * @param model
	 *            ClientModel, Das Model das den Spielablauf und
	 *            Serverkommunikation steuert. oder Socket Argumenten.
	 * @throws IOException
	 *             Wird geworfen beim fehlerbehafteten Erstellen der
	 *             ObjectStreams.
	 */
	public void startConnection() {
		try {
			InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
			connection = new Socket(SERVER_IP, PORT);
			connection.setSoTimeout(0);

			in = new ObjectInputStream(connection.getInputStream());
			out = connection.getOutputStream();
			out.flush();
			socketSet = true;

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public class PlayerListener implements Runnable {
		private boolean waiting;

		@Override
		public void run() {
			startConnection();
			// final TextView songTitle = (TextView)
			// findViewById(R.id.hud_title);
			waiting = true;

			try {

				while (waiting) {
					Object message;

					message = in.readObject();

					if (message instanceof HudMessage) {
						
						if (message instanceof SongTitleMessage) {
							final TextView songTitle = (TextView) findViewById(R.id.hud_title);
							final String songTitleText = ((SongTitleMessage) message)
									.getSongTitle();

							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									songTitle.setText(songTitleText);
								}
							});

						} else if (message instanceof ShuffleMessage) {
							final ImageView imgView = (ImageView) findViewById(R.id.shuffleVisible);
							final boolean isShuffled = ((ShuffleMessage) message)
									.isShuffled();

							runOnUiThread(new Runnable() {

								@Override
								public void run() {

									if (isShuffled) {
										imgView.setVisibility(View.VISIBLE);
									} else {
										imgView.setVisibility(View.INVISIBLE);
									}
								}
							});
						
						} else if (message instanceof LoopingMessage) {
							final ImageView imgView = (ImageView) findViewById(R.id.loopingVisible);
							final boolean isLooping = ((LoopingMessage) message)
									.isLooping();

							runOnUiThread(new Runnable() {

								@Override
								public void run() {

									if (isLooping) {
										imgView.setVisibility(View.VISIBLE);
									} else {
										imgView.setVisibility(View.INVISIBLE);
									}
								}
							});
						
						} else if(message instanceof TimeMessage) {
							final double start = ((TimeMessage)message).getStartTime();
							final double end = ((TimeMessage)message).getEndTime();
							
							runOnUiThread(new Runnable() {

								@Override
								public void run() {

									updateTime(start,end);
								}
							});
						}
					}

				}
			} catch (ClassNotFoundException e) {
				System.err.println("Unbekanntes Objekt empfangen");
				e.printStackTrace();
			} catch (EOFException e) {
				System.err.println("Ende des Objektstroms");
				e.printStackTrace();

			} catch (SocketException e) {

				System.err.println("Verbindung verloren");
				e.printStackTrace();

			} catch (IOException e) {
				System.err.println("Netzwerkfehler");
				e.printStackTrace();
			} finally {
				closeConnection();
			}
		}
	}
	
	@SuppressLint("NewApi")
	private void updateTime(double startT, double finalT) {
		finalTime = finalT;
		startTime = startT;

		seekbar.setMax((int) finalTime);

		endTimeField.setText(String.format(
				"%dmin %dsec",
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
	}
	
	

	/*
	 * public synchronized void receiveMessage(SongTitleMessage msg) { if (msg
	 * != null) { if (msg.getSongTitle() != null) {
	 * switchSong(msg.getSongTitle()); } else { throw new
	 * IllegalArgumentException("Leerer String"); } } else { throw new
	 * IllegalArgumentException("Argument ist null"); } }
	 */

	/**
	 * Hilfsmethode die eine bestehende Verbindung abbaut und deren Ressourcen
	 * freigibt.
	 */
	public void closeConnection() {
		if (socketSet) {
			socketSet = false;
			try {
				out.close();
				in.close();
				connection.close();
			} catch (IOException e) {
				System.out.println("Fehler beim schlie�en des Netzwerkes");
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onStop() {
		closeConnection();
		super.onStop();
	}

}
