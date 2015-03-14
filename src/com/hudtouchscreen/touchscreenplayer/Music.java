package com.hudtouchscreen.touchscreenplayer;

import java.io.FileDescriptor;
import java.io.IOException;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;

/**
 * A Music Object for MusicPlayer
 * 
 * @author daniel
 */
public class Music implements OnCompletionListener{
	private MediaPlayer mediaPlayer;
	private boolean isPrepared;
	private MusicPlayer musicPlayer;
	

	/**
	 * Is initiated when Music is loaded from assets
	 * 
	 * @param assetDescriptor
	 * @param musicPlayer
	 */
	public Music(AssetFileDescriptor assetDescriptor, MusicPlayer musicPlayer) {
		mediaPlayer = new MediaPlayer();
		this.musicPlayer = musicPlayer;
		isPrepared = false;

		try {
			mediaPlayer.setDataSource(assetDescriptor.getFileDescriptor(),
					assetDescriptor.getStartOffset(),
					assetDescriptor.getLength());

			mediaPlayer.prepare();
			isPrepared = true;
			play();
		
			mediaPlayer.setOnCompletionListener(this);
			
		} catch (Exception ex) {
			throw new RuntimeException("Couldn't load music, uh oh!");
		}
	}

	/**
	 * Is initiated when Music is loaded from assets
	 * 
	 * @param fileDescriptor
	 * @param musicPlayer
	 */
	public Music(FileDescriptor fileDescriptor, MusicPlayer musicPlayer) {
		mediaPlayer = new MediaPlayer();
		this.musicPlayer = musicPlayer;
		isPrepared = false;

		try {
			mediaPlayer.setDataSource(fileDescriptor);

			mediaPlayer.prepare();
			isPrepared = true;
			play();
			stop();

			mediaPlayer.setOnCompletionListener(this);
			
		} catch (Exception ex) {
			throw new RuntimeException("Couldn't load music, uh oh!");
		}
	}
	
	/**
	 * Is called when a Song has ended playing
	 */
	public void onCompletion(MediaPlayer mediaPlayer) {
		synchronized (this) {
			isPrepared = false;
			musicPlayer.nextTrack();
		}
	}

	/**
	 * Is called when a song is being started
	 */
	protected void play() {
		if (mediaPlayer.isPlaying()) {
			return;
		}
		try {
			synchronized (this) {
				if (!isPrepared) {
					mediaPlayer.prepare();
					isPrepared = true;
				}
				mediaPlayer.start();
			}
		} catch (IllegalStateException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Is called a song is stopped
	 */
	protected void stop() {
		if (isPrepared) {
			mediaPlayer.seekTo(0);
			mediaPlayer.pause();
		}
	}

	/**
	 * Is called when a song is paused
	 */
	protected void pause() {
		if (isPrepared) {
			mediaPlayer.pause();
		}
	}

	/**
	 * Checks if the current Song is playing
	 * 
	 * @return
	 */
	protected boolean isPlaying() {
		return mediaPlayer.isPlaying();
	}

	/**
	 * Checks if looping is activated
	 * 
	 * @return
	 */
	protected boolean isLooping() {
		return mediaPlayer.isLooping();
	}

	/**
	 * Activates or unactivates looping
	 * 
	 * @param isLooping
	 */
	protected void setLooping(boolean isLooping) {
		mediaPlayer.setLooping(isLooping);
	}

	/**
	 * Sets the Music volume
	 * 
	 * @param volumeLeft
	 * @param volumeRight
	 */
	protected void setVolume(float volumeLeft, float volumeRight) {
		mediaPlayer.setVolume(volumeLeft, volumeRight);
	}

	/**
	 * Disposes current titel
	 */
	protected void dispose() {
		if (mediaPlayer.isPlaying()) {
			stop();
		}
		mediaPlayer.release();
	}

	/**
	 * Returns the songs current time
	 * 
	 * @return
	 */
	protected double getCurrentTime() {
		try{
			return mediaPlayer.getCurrentPosition();
		} catch (IllegalStateException ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	/**
	 * Returns the songs ending time
	 * 
	 * @return
	 */
	protected double getFinalTime() {
		if (isPrepared) {
			return mediaPlayer.getDuration();
		}
		return 0;
	}

	/**
	 * Starts the song at a certain time
	 * 
	 * @param progress
	 */
	protected void seek(int progress) {
		mediaPlayer.seekTo(progress);
	}
}
