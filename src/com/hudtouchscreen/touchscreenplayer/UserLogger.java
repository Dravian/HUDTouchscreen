package com.hudtouchscreen.touchscreenplayer;

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

import com.hudtouchscreen.hudmessage.KeyTouchMessage;

import android.os.Environment;
import android.util.Log;

/**
 * Logt die Aktionen die ein Nutzer tätigt während er eine bestimmte Aufgabe
 * erfüllen muss
 * 
 * @author daniel
 * 
 */
public final class UserLogger {

	private static Logger logger;
	private static FileHandler fhandler;
	private static Timestamp userStart;

	private static List<String> actions;
	private static List<UserLogger.State> tasks;
	private static State currentState = State.OFF;
	private static int error;
	private static int counter;
	private static FileWriter writer;
	private static String fileID;
	private static final String LIST_GOAL = "Things";
	private static final int LIST_POSITION = 5;
	private static final String KEYBOARD_GOAL = "Spidey";

	/**
	 * Beschreibt in welchen Zustand der Logger ist
	 * 
	 * @author daniel
	 * 
	 */
	public enum State {
		OFF, IDLE, LIST_SEARCH, KEYBOARD, SEEKBAR, PAUSE_BACK_PLAY, LOOPING_FORWARD_SHUFFLE;
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

		SWIPE_LEFT("SWIPE_LEFT"), SWIPE_RIGHT("SCROLL_RIGHT"), SWIPE_UP(
				"SWIPE_UP"),

		SWIPE_DOWN("SWIPE_DOWN"), SEEKBAR("SEEK_BAR");

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
	 * Setzt die Startzeit
	 */
	private static void userStart() {
		java.util.Date date = new java.util.Date();
		userStart = new Timestamp(date.getTime());
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
			sb.append("(" + view.toString() + ": " + action.toString() + ")");
			break;
		case KEYBOARD:
			if (action == Action.CLICK_KEY) {
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
			} else {
				sb.append("(" + view.toString() + ": " + action.toString()
						+ ")");
			}
			break;
		default:
			break;

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

		switch (currentState) {
		case OFF:
			return;
		case IDLE:
			return;
		}

		addAction(view, action, item, position);
		switch (currentState) {
		case LIST_SEARCH:
			logListTask(view, action, position);
			break;
		case KEYBOARD:
			logKeyBoardTask(view, action, item, position);
			break;
		case SEEKBAR:
			logSeekbarTask(view, action, item, position);
			break;
		default:
			break;

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
	private static void logListTask(UserView view, Action action, int position) {

		if (view == UserView.PLAYER && action != Action.SWIPE_DOWN) {
			error++;
		} else if (view == UserView.KEYBOARD && action != Action.SWIPE_RIGHT) {
			error++;
		} else if (view == UserView.LIST) {

			if (action == Action.CLICK_ITEM && LIST_POSITION != position) {
				error++;

			} else if (action == Action.CLICK_ITEM && LIST_POSITION == position) {
				StringBuilder sb = new StringBuilder();
				sb.append("List_Search: 3");

				endTask(sb);

			} else if (action == Action.SWIPE_UP) {

				if (LIST_POSITION % 5 != position % 5) {
					error++;
				}
			} else if (action == Action.SWIPE_DOWN) {

				if (position % 5 != LIST_POSITION % 5) {
					error++;
				}

			} else if (action == Action.SWIPE_RIGHT) {
				error++;
			}
		}

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
	private static void logKeyBoardTask(UserView view, Action action,
			String item, int size) {

		if (view == UserView.PLAYER && action != Action.SWIPE_UP) {
			error++;
		} else if (view == UserView.LIST && action != Action.SWIPE_RIGHT) {
			error++;
		} else if (view == UserView.KEYBOARD) {
			if (action == Action.CLICK_KEY) {
				char key = item.charAt(0);

				if (counter > 0 && key != KeyTouchMessage.KEY_DELETE.charAt(0)) {
					error++;
					if (size < MusikKeyboard.MAX_TEXT_LENGTH) {
						counter++;
					}

				} else if (counter > 0
						&& key == KeyTouchMessage.KEY_DELETE.charAt(0)) {
					counter--;

				} else {
					for (int i = 0; i < MusikKeyboard.MAX_TEXT_LENGTH; i++) {

						if (size == KEYBOARD_GOAL.length()
								&& key != KeyTouchMessage.KEY_ENTER.charAt(0)) {
							error++;

							if (!item.equals(KeyTouchMessage.KEY_DELETE)
									&& size < MusikKeyboard.MAX_TEXT_LENGTH) {
								counter++;
							}
							return;

						} else if (size == KEYBOARD_GOAL.length()
								&& key == KeyTouchMessage.KEY_ENTER.charAt(0)) {

							StringBuilder sb = new StringBuilder();
							sb.append("Keyboard_Input: 8");

							endTask(sb);
							return;

						} else if (size == i
								&& key != (KEYBOARD_GOAL.toLowerCase())
										.charAt(i)) {
							error++;

							if (!item.equals(KeyTouchMessage.KEY_DELETE)
									&& size < MusikKeyboard.MAX_TEXT_LENGTH) {
								counter++;
							}
							return;

						} else if (size > KEYBOARD_GOAL.length() || size < 0) {
							error++;

							if (!item.equals(KeyTouchMessage.KEY_DELETE)
									&& size < MusikKeyboard.MAX_TEXT_LENGTH) {
								counter++;
							}
							return;
						}
					}
				}
			} else {
				error++;
			}
		}
	}

	private static void logSeekbarTask(UserView view, Action action,
			String item, int percent) {
		int minPercent;
		int maxPercent;

		if (view == UserView.LIST && action != Action.SWIPE_RIGHT) {
			error++;
		} else if (view == UserView.KEYBOARD && action != Action.SWIPE_RIGHT) {
			error++;
		} else if (view == UserView.PLAYER) {

			if (counter == 0 && action == Action.CLICK_STOP) {
				counter++;
			} else if (counter == 1) {

			}
		}
	}

	static void logPauseBackPlayTask(UserView view, Action action, String item,
			int position) {
		if (view == UserView.LIST && action != Action.SWIPE_RIGHT) {
			error++;
		} else if (view == UserView.KEYBOARD && action != Action.SWIPE_RIGHT) {
			error++;

		} else if (view == UserView.PLAYER) {

			if (counter == 0 && action == Action.CLICK_PAUSE) {
				counter++;
			} else if (counter == 1 && action == Action.SWIPE_RIGHT) {
				counter++;

			} else if (counter == 2 && action == Action.SWIPE_RIGHT) {
				counter++;

			} else if (counter == 3 && action == Action.CLICK_PLAY) {

			}
		}
	}

	/**
	 * Beendet eine Aufgabe
	 * 
	 * @param log
	 *            Der String in einer Datei geloggt wird
	 */
	private static void endTask(StringBuilder log) {
		String startTime = new SimpleDateFormat("HH:mm:ss").format(userStart);

		java.util.Date date = new java.util.Date();
		Timestamp userEnd = new Timestamp(date.getTime());

		long seconds = userEnd.getTime() - userStart.getTime();

		String duration = Long.toString(seconds);

		int usedActions = actions.size();
		log.append(", " + startTime);
		log.append(", " + duration + "ms");
		log.append(", " + Integer.toString(usedActions));
		log.append(", " + error);

		for (String s : actions) {

			log.append(", " + s);
		}

		log.append("\n");

		logger.info(log.toString());
		// fhandler.flush();

		actions = new ArrayList<String>();
		currentState = State.IDLE;
		error = 0;
		counter = 0;

		try {
			writer.append(log);

			if (tasks.size() == 0) {

				writer.flush();
				writer.close();
				currentState = State.OFF;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.w("UserLogger", "Error writing/flushing/closing file");
			e.printStackTrace();
		}

	}

	/**
	 * Beginnt eine LogAufgabe
	 */
	static void start() {
		if (currentState == State.IDLE) {
			currentState = tasks.remove(0);
			userStart();

		}
	}

	/**
	 * Intitialisert UseLogger
	 * 
	 * @throws IOException
	 */
	static void init(String fileName, List<UserLogger.State> initTasks) {

		actions = new ArrayList<String>();
		tasks = new LinkedList<UserLogger.State>();
		tasks.addAll(initTasks);
		currentState = State.IDLE;
		error = 0;
		counter = 0;

		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)
				|| Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED_READ_ONLY)) {

			 String logFileName =
					  Environment.getExternalStoragePublicDirectory(
					  Environment.DIRECTORY_MUSIC).getAbsolutePath() + "/zLog/" + fileName + ".cvs";
			try {

				writer = new FileWriter(logFileName);

			} catch (IOException e) {
				Log.w("UserLogger", "Error creating file");
				e.printStackTrace();
			}
		}
		logger = Logger.getLogger(UserLogger.class.getName()); //
		/*
		 * logger.setUseParentHandlers(false);
		 * 
		 * try { String logfileName =
		 * Environment.getExternalStoragePublicDirectory(
		 * Environment.DIRECTORY_MUSIC).getAbsolutePath() + "/" + fileName +
		 * ".txt";
		 * 
		 * fhandler = new FileHandler(logfileName); SimpleFormatter formatterTxt
		 * = new SimpleFormatter(); fhandler.setFormatter(formatterTxt);
		 * 
		 * logger.addHandler(fhandler);
		 * 
		 * logger.info(
		 * "Task: MinActions, Starttime, Duration, UserActions, Errors, Actions"
		 * + "\n"); fhandler.flush();
		 * 
		 * } catch (SecurityException e) { e.printStackTrace(); } catch
		 * (IOException e) { e.printStackTrace(); }
		 */
	}
}
