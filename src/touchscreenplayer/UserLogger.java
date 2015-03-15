package touchscreenplayer;

import hudmessage.KeyTouchMessage;
import hudmessage.TimeMessage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import service.ServiceManager;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

/**
 * Logt die Aktionen die ein Nutzer tätigt während er eine bestimmte Aufgabe
 * erfüllen muss
 * 
 * @author daniel
 * 
 */
public final class UserLogger {

	private static long startDate;
	private static SimpleDateFormat dateFormat;
	private static List<String> actions;
	private static List<UserLogger.State> tasks;
	private static State currentState = State.OFF;
	private static int error;
	private static int counter;
	private static FileWriter writer;
	private static String fileID;
	private final static Handler TIME_LIMIT = new Handler();

	private static final int LIST_POSITION = 38;

	private static final String KEYBOARD_GOAL = "vampir"; // lower case letters

	private static final int SLIDE_START_MAX = 15;
	private static final int SLIDE_END_MIN = 40;
	private static final int SLIDE_END_MAX = 60;
	private static final int FORWARD_SWIPES = 3;
	private static final int PREVIOUS_SWIPES = 2;
	private static boolean checkSlideLog = false;
	private static int delay;

	/**
	 * Beschreibt in welchen Zustand der Logger ist
	 * 
	 * @author daniel
	 * 
	 */
	public enum State {
		OFF(0, ""), IDLE(0, ""), LIST_SEARCH(9, "LIST SEARCH"), KEYBOARD(8,
				"KEYBOARD SEARCH"), SWIPE(7, "SWIPE"), CLICK(6, "CLICK");

		private int minTaskActions;
		private String task;

		private State(int minTaskActions, String task) {
			this.minTaskActions = minTaskActions;
			this.task = task;
		}

		public int getMinTaskActions() {
			return minTaskActions;
		}

		public String getTask() {
			return task;
		}
	}

	/**
	 * Beschreibt in welchen Logevent der Logger sich gerade befindet
	 * 
	 * @author daniel
	 * 
	 */
	public enum UserView {
		PLAYER("PLAYER"), LIST("LIST"), KEYBOARD("KEYBOARD");

		private String text;

		private UserView(String text) {
			this.text = text;
		}

		public String toString() {
			return text;
		}
	}

	/**
	 * Beschreibt die Action die geloggt wird
	 * 
	 * @author daniel
	 * 
	 */
	public enum Action {
		CLICK_PLAY("CLICK_PLAY"), CLICK_PAUSE("CLICK_PAUSE"), CLICK_STOP(
				"CLICK_STOP"),

		CLICK_SHUFFLE("CLICK_SHUFFLE"), CLICK_START("CLICK_START"), CLICK_LOOPING(
				"CLICK_LOOPING"),

		CLICK_ITEM("CLICK_ITEM"), CLICK_KEY("CLICK_KEY"), MOVE_SEEKBAR(
				"MOVE_SEEKBAR"),

		SWIPE_LEFT("SWIPE_LEFT"), SWIPE_RIGHT("SWIPE_RIGHT"), SWIPE_UP(
				"SWIPE_UP"),

		SWIPE_DOWN("SWIPE_DOWN"), START_SEEKBAR("START_SEEKBAR"), STOP_SEEKBAR(
				"STOP_SEEKBAR"), KEY_BACK(
				"KEY_BACK");

		private String text;

		private Action(String text) {
			this.text = text;
		}

		public String toString() {
			return text;
		}
	}

	private UserLogger() {
		currentState = State.OFF;
	}

	/**
	 * Speichert eine neue Action in die Liste
	 * 
	 * @param view
	 *            Der Ort an dem Aktion gemacht wurde
	 * @param action
	 *            Die Aktion die gemacht wurde
	 * @param item
	 *            Ein String Wer der die Aktion spezifiziert
	 * @param position
	 *            Ein int Wert der die Aktion spezifiert
	 */
	private static void addAction(UserView view, Action action, String item,
			int position) {
		StringBuilder sb = new StringBuilder();

		switch (view) {
		case PLAYER:
			if (action == Action.START_SEEKBAR || action == Action.STOP_SEEKBAR) {
				sb.append("(" + view.toString() + ": " + action.toString()
						+ " " + Integer.toString(position) + "%" + ")");

			} else {
				sb.append("(" + view.toString() + ": " + action.toString()
						+ ")");
			}
			break;
		case KEYBOARD:
			if (action == Action.CLICK_KEY) {

				if (item.equals("/")) {
					item = "DELETE";
				} else if (item.equals("_")) {
					item = "ENTER";
				}
				sb.append("(" + view.toString() + ": " + action.toString()
						+ " + " + item + ")");
			} else {
				sb.append("(" + view.toString() + ": " + action.toString()
						+ ")");
			}
			break;
		case LIST:
			if (action == Action.CLICK_ITEM) {
				sb.append("(" + view.toString() + ": " + action.toString()
						+ " + " + item + ")");
			} else if (action == Action.SWIPE_UP || action == Action.SWIPE_DOWN) {
				position = position + 1;
				sb.append("(" + view.toString() + ": " + action.toString()
						+ " from Startposition " + position + ")");
			} else {
				sb.append("(" + view.toString() + ": " + action.toString()
						+ ")");
			}
			break;
		default:
			break;

		}

		if (action == Action.KEY_BACK ) {
			sb.append("(" + view.toString() + ": " + action.toString() + ")");
		}
		actions.add(sb.toString());
	}

	/**
	 * Loogt eine Aktion
	 * 
	 * @param view
	 *            Der Ort an dem Aktion gemacht wurde
	 * @param action
	 *            Die Aktion die gemacht wurde
	 * @param item
	 *            Ein String Wer der die Aktion spezifiziert
	 * @param position
	 *            Ein int Wert der die Aktion spezifiert
	 */
	static void logAction(UserView view, Action action, String item,
			int position) {
		boolean taskFinished = false;

		switch (currentState) {
		case OFF:
			return;
		case IDLE:
			return;
		case LIST_SEARCH:
			addAction(view, action, item, position);
			taskFinished = logListTask(view, action, position);
			break;
		case KEYBOARD:
			addAction(view, action, item, position);
			taskFinished = logKeyBoardTask(view, action, item, position);
			break;
		case CLICK:
			addAction(view, action, item, position);
			taskFinished = logClick(view, action, item, position);
			break;
		case SWIPE:
			addAction(view, action, item, position);
			taskFinished = logSwipe(view, action, item, position);
			break;
		default:
			return;
		}

		StringBuilder log = new StringBuilder();
		log.append(fileID);
		log.append(", " + currentState.getTask());

		long currentTime = getCurrentTime();

		String current = dateFormat.format(currentTime);

		long seconds = currentTime - startDate;

		String duration = Long.toString(seconds);

		int usedActions = actions.size();
		log.append(", " + current);
		log.append(", " + duration + "ms");
		log.append(", " + Integer.toString(currentState.getMinTaskActions()));
		log.append(", " + Integer.toString(usedActions));
		log.append(", " + error);
		log.append(", " + actions.get(actions.size() - 1));

		log.append("\n");

		try {
			Log.i("Userlogger", log.toString());
			writer.append(log.toString());

			if (taskFinished) {
				TIME_LIMIT.removeCallbacks(timeOver);

				actions = new ArrayList<String>();
				currentState = State.IDLE;
				error = 0;
				counter = 0;
				writer.append("\n");
			}

			if (taskFinished && tasks.size() == 0) {
				currentState = State.OFF;

				writer.flush();
				writer.close();
			}

		} catch (IOException e) {
			Log.w("UserLogger", "Error writing/flushing/closing file");
			e.printStackTrace();
		}

	}

	/**
	 * Verarbeitet die Listensuche Aufgabe,
	 * 
	 * @param view
	 *            Der Ort an dem Aktion gemacht wurde
	 * @param action
	 *            Die Aktion die gemacht wurde
	 * @param position
	 *            Falls in der Listen Suche nach unten oder nach oben Swipe
	 *            ausgeführt wird, die Position des ersten Titels in der
	 *            Listenanzeige nachdem Swipe ausgeführt wurde, falls in der
	 *            Liste ein Element ausgewählt wird, die Position des Elements
	 *            in der Liste
	 */
	private static boolean logListTask(UserView view, Action action,
			int position) {

		if (view == UserView.PLAYER && action != Action.SWIPE_DOWN) {
			if (action == Action.START_SEEKBAR) {

			} else {
				error++;
			}
		} else if (view == UserView.KEYBOARD && action != Action.SWIPE_RIGHT) {
			error++;
		} else if (view == UserView.LIST) {

			if (action == Action.CLICK_ITEM && LIST_POSITION != position) {
				error++;

			} else if (action == Action.CLICK_ITEM && LIST_POSITION == position) {

				return true;

			} else if (action == Action.SWIPE_UP) {

				if (LIST_POSITION < position + 5) {
					error++;
				}
			} else if (action == Action.SWIPE_DOWN) {

				if (LIST_POSITION > position) {
					error++;
				}

			} else if (action == Action.SWIPE_RIGHT) {
				error++;
			}
		}

		return false;
	}

	/**
	 * Verarbeitet die Keyboard Aufgabe, counter zeigt an wie oft die delete
	 * taste gedrückt werden muss,um falsch eingegebene Wörter zu löschen
	 * 
	 * @param view
	 * @param action
	 * @param item
	 * @param size
	 */
	private static boolean logKeyBoardTask(UserView view, Action action,
			String item, int size) {

		if (view == UserView.PLAYER) {
			counter = 0;

			if (action == Action.START_SEEKBAR) {

			} else if (action != Action.SWIPE_UP) {
				error++;
			}
		} else if (view == UserView.LIST && action != Action.SWIPE_RIGHT) {
			counter = 0;

			if (action != Action.SWIPE_RIGHT) {
				error++;
			}
		} else if (view == UserView.KEYBOARD) {
			if (action == Action.CLICK_KEY) {
				char key = item.charAt(0);
				

				if (counter > 0 && key != KeyTouchMessage.KEY_DELETE.charAt(0)) {
					error++;
					if (size < MusikKeyboard.MAX_TEXT_LENGTH && key != KeyTouchMessage.KEY_ENTER.charAt(0)) {
						counter++;
					}

				} else if (counter > 0
						&& key == KeyTouchMessage.KEY_DELETE.charAt(0)
						&& size > 0) {
					counter--;

				} else {
					//for (int i = 0; i < MusikKeyboard.MAX_TEXT_LENGTH; i++) {
					
					Log.i("UserLogger keyboard", Integer.toString(size) + key);
					
					if (size == KEYBOARD_GOAL.length()
							&& key == KeyTouchMessage.KEY_ENTER.charAt(0)) {
						return true;

					}
					else if (size >= KEYBOARD_GOAL.length()
								&& key != KeyTouchMessage.KEY_ENTER.charAt(0)) {
							error++;

							if (!item.equals(KeyTouchMessage.KEY_DELETE)
									&& size < MusikKeyboard.MAX_TEXT_LENGTH) {
								counter++;
							}
							return false;

							
						}  
				
						for(int i = 0; i < KEYBOARD_GOAL.length();i++) {
							if (size == i && key != (KEYBOARD_GOAL.toLowerCase()).charAt(size)) {
								error++;

								if (!item.equals(KeyTouchMessage.KEY_DELETE) && !item.equals(KeyTouchMessage.KEY_ENTER)) {
									counter++;
								}
								return false;
						} 
						}
						
					/*
						 * else if (size == i && key != KEYBOARD_GOAL.charAt(i){
						 * error++;
						 * 
						 * if (!item.equals(KeyTouchMessage.KEY_DELETE) && size
						 * < MusikKeyboard.MAX_TEXT_LENGTH) { counter++; }
						 * return false;
						 * 
						 * }
						 */
						/*else if (size == 0 && key != 'v') {
							error++;

							if (!item.equals(KeyTouchMessage.KEY_DELETE) && !item.equals(KeyTouchMessage.KEY_ENTER)) {
								counter++;
							}
							return false;

						} else if (size == 1 && key != 'a') {
							error++;

							if (!item.equals(KeyTouchMessage.KEY_DELETE)&& !item.equals(KeyTouchMessage.KEY_ENTER)) {
								counter++;
							}
							return false;

						} else if (size == 2 && key != 'm') {
							error++;

							if (!item.equals(KeyTouchMessage.KEY_DELETE)&& !item.equals(KeyTouchMessage.KEY_ENTER)) {
								counter++;
							}
							return false;

						} else if (size == 3 && key != 'p') {
							error++;

							if (!item.equals(KeyTouchMessage.KEY_DELETE)&& !item.equals(KeyTouchMessage.KEY_ENTER)) {
								counter++;
							}
							return false;

						} else if (size == 4 && key != 'i') {
							error++;

							if (!item.equals(KeyTouchMessage.KEY_DELETE)&& !item.equals(KeyTouchMessage.KEY_ENTER)) {
								counter++;
							}
							return false;

						} else if (size == 5 && key != 'r') {
							error++;

							if (!item.equals(KeyTouchMessage.KEY_DELETE)&& !item.equals(KeyTouchMessage.KEY_ENTER)) {
								counter++;
							}
							return false;

						}*/
				}
			} else {
				error++;
			}
		}
		return false;
	}

	private static boolean logClick(UserView view, Action action, String item,
			int percent) {

		if (view == UserView.LIST && action != Action.SWIPE_RIGHT) {
			error++;
		} else if (view == UserView.KEYBOARD && action != Action.SWIPE_RIGHT) {
			error++;

		} else if (view == UserView.PLAYER) {
			if (action == Action.START_SEEKBAR && counter != 3) {

			} else if (counter == 0) {
				if (action == Action.CLICK_SHUFFLE) {
					counter++;
				} else {
					error++;
				}

			} else if (counter == 1) {
				if (action == Action.CLICK_LOOPING) {
					counter++;
				} else {
					error++;
				}

			} else if (counter == 2) {
				if (action == Action.CLICK_STOP) {
					counter++;
					checkSlideLog = true;
				} else {
					error++;
				}

			} else if (counter == 3) {
				if (action == Action.START_SEEKBAR) {
					if (percent >= SLIDE_END_MIN && percent <= SLIDE_END_MAX) {
						counter++;
					}
				} else {
					error++;
				}

			} else if (counter == 4) {
				if (action == Action.STOP_SEEKBAR && percent >= SLIDE_END_MIN
						&& percent <= SLIDE_END_MAX) {
					counter++;
					checkSlideLog = false;
				} else {
					counter--;
					error++;
				}
			}

			else if (counter == 5) {
				if (action == Action.CLICK_PLAY || action == Action.CLICK_PAUSE) {
					return true;
				} else {
					error++;
				}
			}
		}
		return false;

	}

	private static boolean logSwipe(UserView view, Action action, String item,
			int percent) {

		if (action == Action.START_SEEKBAR
				&& counter != FORWARD_SWIPES + PREVIOUS_SWIPES) {

		} else if (view == UserView.LIST && action != Action.SWIPE_RIGHT) {
			error++;
		} else if (view == UserView.KEYBOARD && action != Action.SWIPE_RIGHT) {
			error++;
		} else if (view == UserView.PLAYER) {

			if (counter < FORWARD_SWIPES) {
				if (action == Action.SWIPE_LEFT) {
					counter++;
				} else {
					error++;
				}
			} else if (counter < FORWARD_SWIPES + PREVIOUS_SWIPES) {
				if (action == Action.SWIPE_RIGHT) {
					counter++;

					if (counter == FORWARD_SWIPES + PREVIOUS_SWIPES) {
						checkSlideLog = true;
					}
				} else {
					error++;
				}

			} else if (counter == FORWARD_SWIPES + PREVIOUS_SWIPES) {
				if (action == Action.START_SEEKBAR) {
					if (percent <= SLIDE_START_MAX) {
						counter++;
					}
				} else {
					error++;
				}

			} else if (counter == FORWARD_SWIPES + PREVIOUS_SWIPES + 1) {
				if (action == Action.STOP_SEEKBAR && percent >= SLIDE_END_MIN
						&& percent <= SLIDE_END_MAX) {
					checkSlideLog = false;
					return true;
				} else {
					counter--;
					error++;
				}

			}
		}
		return false;
	}

	private static long getCurrentTime() {
		return System.currentTimeMillis() + delay;
	}

	/**
	 * Beginnt eine LogAufgabe
	 */
	static void start() {
		if (currentState == State.IDLE) {
			currentState = tasks.remove(0);

			StringBuilder log = new StringBuilder();

			log.append(fileID);
			log.append(", " + currentState.getTask());

			startDate = getCurrentTime();

			String current = dateFormat.format(startDate);

			int usedActions = actions.size();
			log.append(", " + current);
			log.append(",   0ms");
			log.append(", "
					+ Integer.toString(currentState.getMinTaskActions()));
			log.append(", " + Integer.toString(usedActions));
			log.append(", " + error);
			log.append(", (Player: CLICK_START)");

			log.append("\n");

			try {
				Log.i("Userlogger", log.toString());
				writer.append(log.toString());
			} catch (IOException e) {
				Log.w("UserLogger", "Error writing/flushing/closing file");
				e.printStackTrace();
			}

			TIME_LIMIT.postDelayed(timeOver, 60000);
		}
	}

	private static Runnable timeOver = new Runnable() {
		public void run() {
			String timeOut = "TIME OUT";

			StringBuilder log = new StringBuilder();
			log.append(fileID);
			log.append(", " + currentState.getTask());

			long currentTime = getCurrentTime();

			String current = dateFormat.format(currentTime);

			long milliSeconds = currentTime - startDate;

			String duration = Long.toString(milliSeconds);

			int usedActions = actions.size();
			log.append(", " + current);
			log.append(", " + duration + "ms");
			log.append(", "
					+ Integer.toString(currentState.getMinTaskActions()));
			log.append(", " + Integer.toString(usedActions));
			log.append(", " + error);

			log.append(", " + timeOut);

			log.append("\n");

			try {
				Log.i("Userlogger", log.toString());
				writer.append(log.toString());

				actions = new ArrayList<String>();
				currentState = State.IDLE;
				error = 0;
				counter = 0;
				writer.append("\n");

				if (tasks.size() == 0) {
					currentState = State.OFF;

					writer.flush();
					writer.close();
				}

			} catch (IOException e) {
				Log.w("UserLogger", "Error writing/flushing/closing file");
				e.printStackTrace();
			}
		}
	};

	/**
	 * Intitialisert UseLogger
	 * 
	 * @throws IOException
	 */
	static void init(String fileName, List<UserLogger.State> initTasks,
			int timeDelay) {

		actions = new ArrayList<String>();
		tasks = new LinkedList<UserLogger.State>();
		tasks.addAll(initTasks);
		currentState = State.IDLE;
		error = 0;
		counter = 0;
		fileID = fileName;
		dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS");
		delay = timeDelay;

		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)
				|| Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED_READ_ONLY)) {

			String logFileName = Environment.getExternalStoragePublicDirectory(
					Environment.DIRECTORY_MUSIC).getAbsolutePath()
					+ "/zLog/" + fileName + ".cvs";
			try {

				writer = new FileWriter(logFileName);
				writer.append("ID, Task, CurrentTime, Duration, Min. Actions, Used Actions, Errors, CurrentAction"
						+ "\n");

			} catch (IOException e) {
				Log.w("UserLogger", "Error creating file");
				e.printStackTrace();
			}
		}

	}

	public static State getState() {
		return currentState;
	}

	public static boolean checkSeekbarLog() {
		if (currentState == State.CLICK || currentState == State.SWIPE) {
			return checkSlideLog;
		}
		return false;
	}

	public static void stopLog() {
		if (currentState != State.IDLE && currentState != State.OFF) {
			currentState = State.IDLE;
			TIME_LIMIT.removeCallbacks(timeOver);

			if (tasks.size() == 0) {
				currentState = State.OFF;

			}
		}
	}
}
