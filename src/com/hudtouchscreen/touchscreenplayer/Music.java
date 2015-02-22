package com.hudtouchscreen.touchscreenplayer;

import java.io.FileDescriptor;
import java.io.IOException;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public class Music implements OnCompletionListener {
	private MediaPlayer mediaPlayer;
	private boolean isPrepared;
	private MusicPlayer musicPlayer;

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
			stop();
			 
			mediaPlayer.setOnCompletionListener(this);
		} catch (Exception ex) {
			throw new RuntimeException("Couldn't load music, uh oh!");
		}
	}

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

	public void onCompletion(MediaPlayer mediaPlayer) {
		synchronized (this) {
			isPrepared = false;
			musicPlayer.nextTrack();
		}
	}

	public void play() {
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

	public void stop() {
		if (isPrepared) {
			mediaPlayer.seekTo(0);
			mediaPlayer.pause();
		}
	}

	public void pause() {
		if (isPrepared) {
			mediaPlayer.pause();
		}
	}

	public boolean isPlaying() {
		return mediaPlayer.isPlaying();
	}

	public boolean isLooping() {
		return mediaPlayer.isLooping();
	}

	public void setLooping(boolean isLooping) {
		mediaPlayer.setLooping(isLooping);
	}

	public void setVolume(float volumeLeft, float volumeRight) {
		mediaPlayer.setVolume(volumeLeft, volumeRight);
	}

	public void dispose() {
		if (mediaPlayer.isPlaying()) {
			stop();
		}
		mediaPlayer.release();
	}

	protected double getStartTime() {
		return mediaPlayer.getCurrentPosition();
	}

	protected double getFinalTime() {
		return mediaPlayer.getDuration();
	}

	protected void seek(int progress) {
		mediaPlayer.seekTo(progress);
	}
}
