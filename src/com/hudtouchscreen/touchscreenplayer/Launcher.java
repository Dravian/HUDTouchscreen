package com.hudtouchscreen.touchscreenplayer;

import java.util.LinkedList;
import java.util.List;

import com.touchscreen.touchscreenplayer.R;

import Service.ServiceManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

public class Launcher extends Activity {
	private List<UserLogger.State> tasks;
	private ServiceManager service;
	private TextView taskOrder;
	private StringBuilder taskNames;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.touchscreenlauncher);
		taskOrder = (TextView) findViewById(R.id.taskorder);
		tasks = new LinkedList<UserLogger.State>();
		taskNames = new StringBuilder();

		this.service = new ServiceManager(this, ServerService.class,
				new Handler() {

					@Override
					public void handleMessage(Message msg) {

						switch (msg.what) {
						case ServerService.MSG_NEWCLIENT:
							break;

						default:
							super.handleMessage(msg);
						}
					}
				});

		service.start();

	}

	public void click(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.task1:
			tasks.add(UserLogger.State.LIST_SEARCH);
			view.setBackgroundResource(R.color.brown);
			view.setClickable(false);
			taskOrder.setText(taskOrder.getText().toString() + "Task1  ");
			taskNames.append("1");

			break;
		case R.id.task2:
			tasks.add(UserLogger.State.KEYBOARD);
			view.setBackgroundResource(R.color.brown);
			view.setClickable(false);
			taskOrder.setText(taskOrder.getText().toString() + "Task2  ");
			taskNames.append("2");

			break;
		case R.id.task3:
			tasks.add(UserLogger.State.SEEKBAR);
			view.setBackgroundResource(R.color.brown);
			view.setClickable(false);
			taskOrder.setText(taskOrder.getText().toString() + "Task3  ");
			taskNames.append("3");
			break;
		case R.id.task4:
			tasks.add(UserLogger.State.PAUSE_BACK_PLAY);
			view.setBackgroundResource(R.color.brown);
			view.setClickable(false);
			taskOrder.setText(taskOrder.getText().toString() + "Task4  ");
			taskNames.append("4");
			break;
		case R.id.task5:
			tasks.add(UserLogger.State.LOOPING_FORWARD_SHUFFLE);
			view.setBackgroundResource(R.color.brown);
			view.setClickable(false);
			taskOrder.setText(taskOrder.getText().toString() + "Task5  ");
			taskNames.append("5");
			break;
		case R.id.back:
			
			if (!tasks.isEmpty()) {
				UserLogger.State task = tasks.remove(tasks.size() - 1);
				
				Button button;
				if (task == UserLogger.State.LIST_SEARCH) {
					button = (Button) findViewById(R.id.task1);
					button.setBackgroundResource(R.color.grey);
					button.setClickable(true);
					String str = taskOrder.getText().toString();
					str = str.replace("Task1 ","");
					taskOrder.setText(str);
					taskNames.deleteCharAt(taskNames.length()-1);
					
					
				} else if (task == UserLogger.State.KEYBOARD) {
					button = (Button) findViewById(R.id.task2);
					button.setBackgroundResource(R.color.grey);
					button.setClickable(true);
					
					String str = taskOrder.getText().toString();
					str = str.replace("Task2 ","");
					taskOrder.setText(str);
					taskNames.deleteCharAt(taskNames.length()-1);
					
				} else if (task == UserLogger.State.SEEKBAR) {
					button = (Button) findViewById(R.id.task3);
					button.setBackgroundResource(R.color.grey);
					button.setClickable(true);
					
					String str = taskOrder.getText().toString();
					str = str.replace("Task3 ","");
					taskOrder.setText(str);
					taskNames.deleteCharAt(taskNames.length()-1);
					
					
				} else if (task == UserLogger.State.PAUSE_BACK_PLAY) {
					button = (Button) findViewById(R.id.task4);
					button.setBackgroundResource(R.color.grey);
					button.setClickable(true);
					
					String str = taskOrder.getText().toString();
					str = str.replace("Task4 ","");
					taskOrder.setText(str);
					taskNames.deleteCharAt(taskNames.length()-1);
					
				} else if (task == UserLogger.State.LOOPING_FORWARD_SHUFFLE) {
					button = (Button) findViewById(R.id.task5);
					button.setBackgroundResource(R.color.grey);
					button.setClickable(true);
					
					String str = taskOrder.getText().toString();
					str = str.replace("Task5 ","");
					taskOrder.setText(str);
					taskNames.deleteCharAt(taskNames.length()-1);
				}
			}
			break;
		case R.id.buttonEnter:
			String idName = ((EditText) findViewById(R.id.idName)).getText()
					.toString();
			RadioGroup radioHud = (RadioGroup) findViewById(R.id.radiohud);

			if (!tasks.isEmpty()) {
				if (radioHud.getCheckedRadioButtonId() == R.id.hudon) {
					UserLogger.init(idName + "hud" + taskNames.toString(),
							tasks);
				} else {
					UserLogger.init(idName + "touch" + taskNames.toString(),
							tasks);
				}
			}
			Intent i = new Intent(getApplicationContext(), MusicPlayer.class);
			startActivity(i);
			break;
		}

	}
	
	private void setText(UserLogger.State state) {
		
	}
}
