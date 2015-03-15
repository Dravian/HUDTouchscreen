package touchscreenplayer;

import java.util.LinkedList;
import java.util.List;

import service.ServiceManager;

import com.touchscreen.touchscreenplayer.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
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
	
	@Override
	public void onDestroy() {

		try {
			service.unbind();
		} catch (Throwable t) {
		}

		super.onDestroy();

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
			tasks.add(UserLogger.State.CLICK);
			view.setBackgroundResource(R.color.brown);
			view.setClickable(false);
			taskOrder.setText(taskOrder.getText().toString() + "Task3  ");
			taskNames.append("3");
			break;
		case R.id.task4:
			tasks.add(UserLogger.State.SWIPE);
			view.setBackgroundResource(R.color.brown);
			view.setClickable(false);
			taskOrder.setText(taskOrder.getText().toString() + "Task4  ");
			taskNames.append("4");
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
					str = str.replace("Task1 ", "");
					taskOrder.setText(str);
					taskNames.deleteCharAt(taskNames.length() - 1);

				} else if (task == UserLogger.State.KEYBOARD) {
					button = (Button) findViewById(R.id.task2);
					button.setBackgroundResource(R.color.grey);
					button.setClickable(true);

					String str = taskOrder.getText().toString();
					str = str.replace("Task2 ", "");
					taskOrder.setText(str);
					taskNames.deleteCharAt(taskNames.length() - 1);

				} else if (task == UserLogger.State.CLICK) {
					button = (Button) findViewById(R.id.task3);
					button.setBackgroundResource(R.color.grey);
					button.setClickable(true);

					String str = taskOrder.getText().toString();
					str = str.replace("Task3 ", "");
					taskOrder.setText(str);
					taskNames.deleteCharAt(taskNames.length() - 1);

				} else if (task == UserLogger.State.SWIPE) {
					button = (Button) findViewById(R.id.task4);
					button.setBackgroundResource(R.color.grey);
					button.setClickable(true);

					String str = taskOrder.getText().toString();
					str = str.replace("Task4 ", "");
					taskOrder.setText(str);
					taskNames.deleteCharAt(taskNames.length() - 1);

				}
			}
			break;
		case R.id.buttonEnter:
			String idName = ((EditText) findViewById(R.id.idName)).getText()
					.toString();
			int delay;
			try {
				String delayText = ((EditText) findViewById(R.id.delay))
						.getText().toString();
				
				int userID = Integer.parseInt(idName);
				
				if (delayText.length() == 0 || idName.length() == 0) {
					return;
				}

				delay = Integer.parseInt(delayText);

			} catch (NumberFormatException e) {
				return;
			}

			RadioGroup radioHud = (RadioGroup) findViewById(R.id.radiohud);

			if (!tasks.isEmpty()) {
				if (radioHud.getCheckedRadioButtonId() == R.id.hudon) {
					UserLogger.init(idName + "hud" + taskNames.toString(),
							tasks, delay);
				} else {
					UserLogger.init(idName + "touch" + taskNames.toString(),
							tasks, delay);
				}
			}
			Intent i = new Intent(getApplicationContext(), MusicPlayer.class);

			startActivity(i);

			break;
		default:
		}

	}
}
