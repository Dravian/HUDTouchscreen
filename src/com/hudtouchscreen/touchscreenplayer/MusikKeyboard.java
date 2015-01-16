package com.hudtouchscreen.touchscreenplayer;

import com.hudtouchscreen.hudmessage.ActivityMessage;
import com.touchscreen.touchscreenplayer.R;

import Service.ServiceManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.view.View;
import android.widget.EditText;

public class MusikKeyboard extends Activity {

	private ServiceManager service;
	private boolean activityOn;
	/*
	private Button qKey;
	private Button wKey;
	private Button eKey;
	private Button rKey;
	private Button tKey;
	private Button zKey;
	private Button uKey;
	private Button iKey;
	private Button oKey;
	private Button pKey;
	private Button aKey;
	private Button sKey;
	private Button dKey;
	private Button fKey;
	private Button gKey;
	private Button hKey;
	private Button jKey;
	private Button kKey;
	private Button lKey;
	private Button yKey;
	private Button xKey;
	private Button cKey;
	private Button vKey;
	private Button bKey;
	private Button nKey;
	private Button mKey;

	private Button delKey;
	private Button enterKey;*/

	private EditText keyBoardText;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.keyboard);
		activityOn = true;
		keyBoardText = (EditText) findViewById(R.id.editKeyBoard);

		this.service = new ServiceManager(this, ServerService.class,
				new Handler() {

					@Override
					public void handleMessage(Message msg) {
						final Bundle bundle = msg.getData();
						bundle.setClassLoader(getClassLoader());

						switch (msg.what) {
						case ServerService.MSG_NEWCLIENT:
						}
					}
				});

		service.start();

	}
	
	private void initialize() {
		/*qKey = (Button) findViewById(R.id.Q);	
		wKey = (Button) findViewById(R.id.W);
		eKey = (Button) findViewById(R.id.E);
		rKey = (Button) findViewById(R.id.R);
		tKey = (Button) findViewById(R.id.T);
		zKey = (Button) findViewById(R.id.Z);
		uKey = (Button) findViewById(R.id.U);
		iKey = (Button) findViewById(R.id.I);
		oKey = (Button) findViewById(R.id.O);
		pKey = (Button) findViewById(R.id.P);
		aKey = (Button) findViewById(R.id.A);
		sKey = (Button) findViewById(R.id.S);
		dKey = (Button) findViewById(R.id.D);
		fKey = (Button) findViewById(R.id.F);
		gKey = (Button) findViewById(R.id.G);
		hKey = (Button) findViewById(R.id.H);
		jKey = (Button) findViewById(R.id.J);
		kKey = (Button) findViewById(R.id.K);
		lKey = (Button) findViewById(R.id.L);
		yKey = (Button) findViewById(R.id.Y);
		xKey = (Button) findViewById(R.id.X);
		cKey = (Button) findViewById(R.id.C);
		vKey = (Button) findViewById(R.id.V);
		bKey = (Button) findViewById(R.id.B);
		nKey = (Button) findViewById(R.id.N);
		mKey = (Button) findViewById(R.id.M);
		
		delKey = (Button) findViewById(R.id.Del);
		enterKey = (Button) findViewById(R.id.Enter);
		
		qKey.setOnClickListener(this);
		wKey.setOnClickListener(this);
		eKey.setOnClickListener(this);
		rKey.setOnClickListener(this);
		tKey.setOnClickListener(this);
		zKey.setOnClickListener(this);
		uKey.setOnClickListener(this);
		iKey.setOnClickListener(this);
		oKey.setOnClickListener(this);
		
		aKey.setOnClickListener(this);
		sKey.setOnClickListener(this);
		dKey.setOnClickListener(this);
		fKey.setOnClickListener(this);
		gKey.setOnClickListener(this);
		hKey.setOnClickListener(this);
		jKey.setOnClickListener(this);
		kKey.setOnClickListener(this);
		lKey.setOnClickListener(this);
		
		yKey.setOnClickListener(this);
		xKey.setOnClickListener(this);
		cKey.setOnClickListener(this);
		bKey.setOnClickListener(this);
		nKey.setOnClickListener(this);
		mKey.setOnClickListener(this);
		
		delKey.setOnClickListener(this);
		enterKey.setOnClickListener(this);*/
	}
	
	private void finishTyping() {
		Intent i = new Intent();

		//i.putExtra("Keyboard", keyBoardText.getText());

		setResult(200, i);
		
		sendToService(ServerService.MSG_ACTIVITY);
	
		activityOn = false;
		service.unbind();
		finish();
	}
	
	private void sendToService(int type) {
		Message message;
		switch(type){
		
		case ServerService.MSG_ACTIVITY:
			message = Message.obtain(null, ServerService.MSG_ACTIVITY, 0, 0);
			
			ActivityMessage activityMessage = new ActivityMessage(ActivityMessage.BACK_TO_MAIN);
			message.getData().putParcelable("Activity", activityMessage);
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

	
	public void click(View view) {
		int id = view.getId();
		
		switch(id) {
		case R.id.q:
			if (keyBoardText.getText().length() == 0) {
				keyBoardText.append("Q");
			} else {
				keyBoardText.append("q");
			}
			break;
		case R.id.w:
			if (keyBoardText.getText().length() == 0) {
				keyBoardText.append("W");
			} else {
				keyBoardText.append("w");
			}
			break;
		case R.id.e:
			if (keyBoardText.getText().length() == 0) {
				keyBoardText.append("E");
			} else {
				keyBoardText.append("e");
			}
			break;
		case R.id.r:
			if (keyBoardText.getText().length() == 0) {
				keyBoardText.append("R");
			} else {
				keyBoardText.append("r");
			}
			break;
		case R.id.t:
			if (keyBoardText.getText().length() == 0) {
				keyBoardText.append("T");
			} else {
				keyBoardText.append("t");
			}
			break;
		case R.id.z:
			if (keyBoardText.getText().length() == 0) {
				keyBoardText.append("Z");
			} else {
				keyBoardText.append("z");
			}
			break;
		case R.id.u:
			if (keyBoardText.getText().length() == 0) {
				keyBoardText.append("U");
			} else {
				keyBoardText.append("u");
			}
			break;
		case R.id.i:
			if (keyBoardText.getText().length() == 0) {
				keyBoardText.append("I");
			} else {
				keyBoardText.append("i");
			}
			break;
		case R.id.o:
			if (keyBoardText.getText().length() == 0) {
				keyBoardText.append("O");
			} else {
				keyBoardText.append("o");
			}
			break;
		case R.id.p:
			if (keyBoardText.getText().length() == 0) {
				keyBoardText.append("P");
			} else {
				keyBoardText.append("p");
			}
			break;
		case R.id.a:
			if (keyBoardText.getText().length() == 0) {
				keyBoardText.append("A");
			} else {
				keyBoardText.append("a");
			}
			break;
		case R.id.s:
			if (keyBoardText.getText().length() == 0) {
				keyBoardText.append("S");
			} else {
				keyBoardText.append("s");
			}
			break;
		case R.id.d:
			if (keyBoardText.getText().length() == 0) {
				keyBoardText.append("D");
			} else {
				keyBoardText.append("d");
			}
			break;
		case R.id.f:
			if (keyBoardText.getText().length() == 0) {
				keyBoardText.append("F");
			} else {
				keyBoardText.append("f");
			}
			break;
		case R.id.g:
			if (keyBoardText.getText().length() == 0) {
				keyBoardText.append("G");
			} else {
				keyBoardText.append("g");
			}
			break;
		case R.id.h:
			if (keyBoardText.getText().length() == 0) {
				keyBoardText.append("H");
			} else {
				keyBoardText.append("h");
			}
			break;
		case R.id.j:
			if (keyBoardText.getText().length() == 0) {
				keyBoardText.append("J");
			} else {
				keyBoardText.append("j");
			}
			break;
		case R.id.k:
			if (keyBoardText.getText().length() == 0) {
				keyBoardText.append("K");
			} else {
				keyBoardText.append("k");
			}
			break;
		case R.id.l:
			if (keyBoardText.getText().length() == 0) {
				keyBoardText.append("L");
			} else {
				keyBoardText.append("l");
			}
			break;
		case R.id.y:
			if (keyBoardText.getText().length() == 0) {
				keyBoardText.append("Y");
			} else {
				keyBoardText.append("y");
			}
			break;
		case R.id.x:
			if (keyBoardText.getText().length() == 0) {
				keyBoardText.append("X");
			} else {
				keyBoardText.append("x");
			}
			break;
		case R.id.c:
			if (keyBoardText.getText().length() == 0) {
				keyBoardText.append("C");
			} else {
				keyBoardText.append("c");
			}
			break;
		case R.id.v:
			if (keyBoardText.getText().length() == 0) {
				keyBoardText.append("V");
			} else {
				keyBoardText.append("v");
			}
			break;
		case R.id.b:
			if (keyBoardText.getText().length() == 0) {
				keyBoardText.append("B");
			} else {
				keyBoardText.append("b");
			}
			break;
		case R.id.n:
			if (keyBoardText.getText().length() == 0) {
				keyBoardText.append("N");
			} else {
				keyBoardText.append("n");
			}
			break;
		case R.id.m:
			if (keyBoardText.getText().length() == 0) {
				keyBoardText.append("M");
			} else {
				keyBoardText.append("m");
			}
			break;
		/*case R.id.Del:
			if (keyBoardText.getText().length() > 0) {
				keyBoardText.getText().delete(
						keyBoardText.getText().length() - 1,
						keyBoardText.getText().length());
			}
			break;
		case R.id.Enter:
			finishTyping();
			break;*/
		default:
			return;
		}

	}
}
