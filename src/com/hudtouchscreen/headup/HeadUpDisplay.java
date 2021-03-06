package com.hudtouchscreen.headup;

import com.hudtouchscreen.hudmessage.ActivityMessage;
import com.hudtouchscreen.hudmessage.LogMessage;
import com.hudtouchscreen.hudmessage.LoopingMessage;
import com.hudtouchscreen.hudmessage.SeekbarLogMessage;
import com.hudtouchscreen.hudmessage.ShuffleMessage;
import com.hudtouchscreen.hudmessage.SongtitleMessage;
import com.hudtouchscreen.hudmessage.TimeMessage;
import com.hudtouchscreen.hudmessage.TouchMessage;
import com.touchscreen.touchscreenplayer.R;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import Service.ServiceManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

public class HeadUpDisplay extends Activity {

	public TextView startTimeField, endTimeField;
	private double startTime = 0;
	private double endTime = 0;
	private SeekBar seekbar;
	
	private ServiceManager service;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_head_up_display);

		startTimeField = (TextView) findViewById(R.id.startTime);
		endTimeField = (TextView) findViewById(R.id.endTime);
		seekbar = (SeekBar) findViewById(R.id.seekBar1);
		seekbar.setClickable(false);

		
		this.service = new ServiceManager(this, ClientService.class,
				new Handler() {

					@Override
					public void handleMessage(Message msg) {
						final Bundle bundle = msg.getData();
						bundle.setClassLoader(getClassLoader());
						
						switch (msg.what) {
						case ClientService.MSG_SONGTITLE:
							SongtitleMessage songTitleMessage = (SongtitleMessage)bundle.getParcelable("Songtitle");
							final TextView songTitle = (TextView) findViewById(R.id.hud_title);
							final String songTitleText = (songTitleMessage)
									.getText();

							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									songTitle.setText(songTitleText);
								}
							});
							break;
						case ClientService.MSG_SHUFFLE:
							ShuffleMessage shuffleMessage = (ShuffleMessage)bundle.getParcelable("Shuffle");
							final ImageView imgShuffle = (ImageView) findViewById(R.id.shuffleVisible);
							final boolean isShuffled = (shuffleMessage)
									.isShuffled();
							
							runOnUiThread(new Runnable() {

								@Override
								public void run() {

									if (isShuffled) {
										imgShuffle.setVisibility(View.VISIBLE);
									} else {
										imgShuffle.setVisibility(View.INVISIBLE);
									}
								}
							});
							break;
						case ClientService.MSG_LOOPING:
							LoopingMessage loopingMessage = (LoopingMessage)bundle.getParcelable("Looping");
							final ImageView imgLooping = (ImageView) findViewById(R.id.loopingVisible);
							final boolean isLooping = (loopingMessage)
									.isLooping();

							runOnUiThread(new Runnable() {

								@Override
								public void run() {

									if (isLooping) {
										imgLooping.setVisibility(View.VISIBLE);
									} else {
										imgLooping.setVisibility(View.INVISIBLE);
									}
								}
							});
							break;
						case ClientService.MSG_TIME:
							TimeMessage timeMessage = (TimeMessage)bundle.getParcelable("Time");
							final double start = timeMessage.getStartTime();
							final double end = timeMessage.getEndTime();
							final int progress = timeMessage.getProgress();
							
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									updateTime(start, end, progress);
								}
							});
							break;
						case ClientService.MSG_TOUCH:
							TouchMessage touchMessage = (TouchMessage)bundle.getParcelable("Touch");
							final int buttonType = touchMessage.getButtonType();
							final boolean touching = touchMessage.isTouching();
							final ImageView imgTouch = (ImageView) findViewById(R.id.showTouch);
							
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									
									if(!touching) {
										imgTouch.setVisibility(View.INVISIBLE);
									} else {
										switch(buttonType){
										case TouchMessage.PLAYBUTTON:
											imgTouch.setImageResource(R.drawable.play);
											break;
										case TouchMessage.PAUSEBUTTON:
											imgTouch.setImageResource(R.drawable.pause);
											break;	
										case TouchMessage.STOPBUTTON:
											imgTouch.setImageResource(R.drawable.stop);
											break;	
										case TouchMessage.SHUFFLEBUTTON:
											imgTouch.setImageResource(R.drawable.shuffle);
											break;
										case TouchMessage.LOOPINGBUTTON:
											imgTouch.setImageResource(R.drawable.looping);
											break;
										case TouchMessage.STARTBUTTON:
											imgTouch.setImageResource(R.drawable.startoff);
											break;
										default:
											return;
										}
										
										imgTouch.setVisibility(View.VISIBLE);
									}
									
								}
							});
							break;					
						case ClientService.MSG_ACTIVITY:
							ActivityMessage activityMessage = (ActivityMessage)bundle.getParcelable("Activity");
							
							if(activityMessage.getActivity() == ActivityMessage.SWITCH_TO_LIST) {
								
								ArrayList<String> titleList = new ArrayList<String>();
								activityMessage.fillEmptyList(titleList);
								
								switchToList(titleList);
							
							} else if(activityMessage.getActivity() == ActivityMessage.SWITCH_TO_KEYBOARD) {
								switchToKeyboard();
								
							}
							break;
						case ClientService.MSG_LOG:
							LogMessage logMessage = (LogMessage)bundle.getParcelable("Log");
							ImageView startImage = (ImageView)findViewById(R.id.start);
							
							if(logMessage.getLogStatus()) {
								startImage.setImageResource(R.drawable.starton);
							} else {
								startImage.setImageResource(R.drawable.startoff);
							}
							
							break;
						case ClientService.MSG_SEEKBARLOG:
							SeekbarLogMessage seekbarLogMessage = (SeekbarLogMessage)bundle.getParcelable("Seekbar Log");
							
							if(seekbarLogMessage.checkSeekbarLog()) {
								seekbar.setBackgroundResource(R.color.red);
							} else {
								seekbar.setBackgroundResource(Color.TRANSPARENT);
							}
							
							break;
						default:
							super.handleMessage(msg);
						}
					}
				});

		service.start();
		
		
	}

	@SuppressLint("NewApi")
	private void updateTime(double startT, double endT, int progress) {
		endTime = endT;
		startTime = startT;

		seekbar.setMax((int) endTime);

		long endMinutes = TimeUnit.MILLISECONDS.toMinutes((long) endTime);
		long endSeconds = TimeUnit.MILLISECONDS.toSeconds((long) endTime)
				- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
						.toMinutes((long) endTime));
		
		if(endSeconds < 10) {
			endTimeField.setText(String.format(
					" %d:0%d",
					endMinutes,endSeconds));
		} else{
			endTimeField.setText(String.format(
					" %d:%d",
					endMinutes,endSeconds));
		}
		
		long startMinutes = TimeUnit.MILLISECONDS.toMinutes((long) startTime);
		long startSeconds = TimeUnit.MILLISECONDS.toSeconds((long) startTime)
				- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
						.toMinutes((long) startTime));
		
		if(startSeconds < 10) {
			startTimeField.setText(String.format(
					"%d:0%d",startMinutes,startSeconds));
		} else {
			startTimeField.setText(String.format(
					"%d:%d",startMinutes,startSeconds));
		}
		
		seekbar.setProgress(progress);
	}

	private void switchToList(ArrayList<String> titleList) {
		Intent i = new Intent(getApplicationContext(), HeadUpList.class);
		i.putStringArrayListExtra("Track Names", titleList);
		startActivityForResult(i,100);	
	}
	
	private void switchToKeyboard() {
		Intent i = new Intent(getApplicationContext(), HeadUpKeyboard.class);
		startActivity(i);	
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}
	 
	

	@Override
	public void onStop() {
		super.onStop();
	}
	
	@Override
	  protected void onDestroy() {
	    super.onDestroy();
	    
	    try { service.unbind(); }
	    catch (Throwable t) { }

	}

}
