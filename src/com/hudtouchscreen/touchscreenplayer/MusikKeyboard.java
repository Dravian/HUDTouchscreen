package com.hudtouchscreen.touchscreenplayer;

import java.util.List;

import com.hudtouchscreen.hudmessage.ActivityMessage;
import com.hudtouchscreen.hudmessage.KeyBoardMessage;
import com.hudtouchscreen.hudmessage.KeyTouchMessage;
import com.touchscreen.touchscreenplayer.R;

import Service.ServiceManager;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.GestureDetector.OnGestureListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class MusikKeyboard extends Activity {
	private ServiceManager service;
	private EditText keyBoardText;
	private GestureDetector gDetector;
	private TouchListener touchListener;
	private List<String> listValues;
	private boolean rightWord;
	private ImageView rightText;
	protected final static int MAX_TEXT_LENGTH = 10;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.keyboard);
		
		Intent intent = getIntent();
		listValues = intent.getStringArrayListExtra("Track Names");
		rightWord = false;
	
		gDetector = new GestureDetector(this, new GestureListener());
		touchListener = new TouchListener();

		keyBoardText = (EditText) findViewById(R.id.editKeyBoard);
		rightText = (ImageView) findViewById(R.id.rightText);
		
		this.service = new ServiceManager(this, ServerService.class,
				new Handler() {

					@Override
					public void handleMessage(Message msg) {
						final Bundle bundle = msg.getData();
						bundle.setClassLoader(getClassLoader());

						switch (msg.what) {
						case ServerService.MSG_NEWCLIENT:
							sendToService(ServerService.MSG_NEWCLIENT);
							break;
						default:
							super.handleMessage(msg);
							break;
						}
					}
				});
		service.start();
	}
	

	private void finishTyping() {
		if (rightWord) {
			Intent i = new Intent();
			i.putExtra("Keyboard", keyBoardText.getText().toString());
			setResult(200, i);
			sendToService(ServerService.MSG_ACTIVITY);
			finish();	
		} 
	}

	private void sendToService(int type) {
		Message message;

		switch (type) {
		case ServerService.MSG_KEYBOARD:
			message = Message.obtain(null, ServerService.MSG_KEYBOARD, 0, 0);
			KeyBoardMessage keyMessage = new KeyBoardMessage(keyBoardText.getText()
					.toString(), rightWord);
			message.getData().putParcelable("Keyboard", keyMessage);
			break;
		case ServerService.MSG_ACTIVITY:
			message = Message.obtain(null, ServerService.MSG_ACTIVITY, 0, 0);
			ActivityMessage activityMessage = new ActivityMessage(
					ActivityMessage.BACK_TO_MAIN, null);
			message.getData().putParcelable("Activity", activityMessage);
			break;
		case ServerService.MSG_NEWCLIENT:
			message = Message.obtain(null, ServerService.MSG_NEWCLIENT, 0, 0);
			
			ActivityMessage newClientMessage = new ActivityMessage(
					ActivityMessage.SWITCH_TO_KEYBOARD, null);
			message.getData().putParcelable("Activity", newClientMessage);
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

	@Override
	public boolean onTouchEvent(MotionEvent me) {
		return touchListener.onTouch(me);
	}

	
	@Override
	  protected void onDestroy() {
	    super.onDestroy();
	    
	    try { service.unbind(); }
	    catch (Throwable t) { }

	}
	
	protected class TouchListener {
		private final Handler handler = new Handler();
		private float mDownX = 0;
		private float mDownY = 0;
		private View view = null;
		private View a = findViewById(R.id.a);
		private View b = findViewById(R.id.b);
		private View c = findViewById(R.id.c);
		private View d = findViewById(R.id.d);
		private View e = findViewById(R.id.e);
		private View f = findViewById(R.id.f);
		private View g = findViewById(R.id.g);
		private View h = findViewById(R.id.h);
		private View i = findViewById(R.id.i);
		private View j = findViewById(R.id.j);
		private View k = findViewById(R.id.k);
		private View l = findViewById(R.id.l);
		private View m = findViewById(R.id.m);
		private View n = findViewById(R.id.n);
		private View o = findViewById(R.id.o);
		private View p = findViewById(R.id.p);
		private View q = findViewById(R.id.q);
		private View r = findViewById(R.id.r);
		private View s = findViewById(R.id.s);
		private View t = findViewById(R.id.t);
		private View u = findViewById(R.id.u);
		private View v = findViewById(R.id.v);
		private View w = findViewById(R.id.w);
		private View x = findViewById(R.id.x);
		private View y = findViewById(R.id.y);
		private View z = findViewById(R.id.z);
		private View delete = findViewById(R.id.delete);
		private View enter = findViewById(R.id.enter);

		private Rect rectA;
		private Rect rectB;
		private Rect rectC;
		private Rect rectD;
		private Rect rectE;
		private Rect rectF;
		private Rect rectG;
		private Rect rectH;
		private Rect rectI;
		private Rect rectJ;
		private Rect rectK;
		private Rect rectL;
		private Rect rectM;
		private Rect rectN;
		private Rect rectO;
		private Rect rectP;
		private Rect rectQ;
		private Rect rectR;

		private Rect rectS;
		private Rect rectT;
		private Rect rectU;
		private Rect rectV;
		private Rect rectW;
		private Rect rectX;
		private Rect rectY;
		private Rect rectZ;
		private Rect rectDelete;
		private Rect rectEnter;

		private String keyType = "-1";

		RelativeLayout layout = (RelativeLayout) findViewById(R.id.keyboardview);
		ViewTreeObserver vto = layout.getViewTreeObserver();

		{
			layout.post(new Runnable() {

				@Override
				public void run() {
					final ViewTreeObserver vto = layout.getViewTreeObserver();

					vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
						@Override
						public void onGlobalLayout() {

							drawRect();
							vto.removeGlobalOnLayoutListener(this);

						}
					});
				}

			});
			
			
		}

		private void drawRect() {

			int[] coordA = new int[2];
			a.getLocationOnScreen(coordA);
			int aX = coordA[0];
			int aY = coordA[1];

			int[] coordB = new int[2];
			b.getLocationOnScreen(coordB);
			int bX = coordB[0];
			int bY = coordB[1];

			int[] coordC = new int[2];
			c.getLocationOnScreen(coordC);
			int cX = coordC[0];
			int cY = coordC[1];

			int[] coordD = new int[2];
			d.getLocationOnScreen(coordD);
			int dX = coordD[0];
			int dY = coordD[1];

			int[] coordE = new int[2];
			e.getLocationOnScreen(coordE);
			int eX = coordE[0];
			int eY = coordE[1];

			int[] coordF = new int[2];
			f.getLocationOnScreen(coordF);
			int fX = coordF[0];
			int fY = coordF[1];

			int[] coordG = new int[2];
			g.getLocationOnScreen(coordG);
			int gX = coordG[0];
			int gY = coordG[1];

			int[] coordH = new int[2];
			h.getLocationOnScreen(coordH);
			int hX = coordH[0];
			int hY = coordH[1];

			int[] coordI = new int[2];
			i.getLocationOnScreen(coordI);
			int iX = coordI[0];
			int iY = coordI[1];

			int[] coordJ = new int[2];
			j.getLocationOnScreen(coordJ);
			int jX = coordJ[0];
			int jY = coordJ[1];

			int[] coordK = new int[2];
			k.getLocationOnScreen(coordK);
			int kX = coordK[0];
			int kY = coordK[1];

			int[] coordL = new int[2];
			l.getLocationOnScreen(coordL);
			int lX = coordL[0];
			int lY = coordL[1];

			int[] coordM = new int[2];
			m.getLocationOnScreen(coordM);
			int mX = coordM[0];
			int mY = coordM[1];

			int[] coordN = new int[2];
			n.getLocationOnScreen(coordN);
			int nX = coordN[0];
			int nY = coordN[1];

			int[] coordO = new int[2];
			o.getLocationOnScreen(coordO);
			int oX = coordO[0];
			int oY = coordO[1];

			int[] coordP = new int[2];
			p.getLocationOnScreen(coordP);
			int pX = coordP[0];
			int pY = coordP[1];

			int[] coordQ = new int[2];
			q.getLocationOnScreen(coordQ);
			int qX = coordQ[0];
			int qY = coordQ[1];

			int[] coordR = new int[2];
			r.getLocationOnScreen(coordR);
			int rX = coordR[0];
			int rY = coordR[1];

			int[] coordS = new int[2];
			s.getLocationOnScreen(coordS);
			int sX = coordS[0];
			int sY = coordS[1];

			int[] coordT = new int[2];
			t.getLocationOnScreen(coordT);
			int tX = coordT[0];
			int tY = coordT[1];

			int[] coordU = new int[2];
			u.getLocationOnScreen(coordU);
			int uX = coordU[0];
			int uY = coordU[1];

			int[] coordV = new int[2];
			v.getLocationOnScreen(coordV);
			int vX = coordV[0];
			int vY = coordV[1];

			int[] coordW = new int[2];
			w.getLocationOnScreen(coordW);
			int wX = coordW[0];
			int wY = coordW[1];

			int[] coordX = new int[2];
			x.getLocationOnScreen(coordX);
			int xX = coordX[0];
			int xY = coordX[1];

			int[] coordY = new int[2];
			y.getLocationOnScreen(coordY);
			int yX = coordY[0];
			int yY = coordY[1];

			int[] coordZ = new int[2];
			z.getLocationOnScreen(coordZ);
			int zX = coordZ[0];
			int zY = coordZ[1];

			int[] coordDel = new int[2];
			delete.getLocationOnScreen(coordDel);
			int deleteX = coordDel[0];
			int deleteY = coordDel[1];

			int[] coordEnter = new int[2];
			enter.getLocationOnScreen(coordEnter);
			int enterX = coordEnter[0];
			int enterY = coordEnter[1];

			rectA = new Rect(aX, aY, aX + a.getWidth(), aY + a.getHeight());

			rectB = new Rect(bX, bY, bX + b.getWidth(), bY + b.getHeight());

			rectC = new Rect(cX, cY, cX + c.getWidth(), cY + c.getHeight());

			rectD = new Rect(dX, dY, dX + d.getWidth(), dY + d.getHeight());

			rectE = new Rect(eX, eY, eX + e.getWidth(), eY + e.getHeight());

			rectF = new Rect(fX, fY, fX + f.getWidth(), fY + f.getHeight());

			rectG = new Rect(gX, gY, gX + g.getWidth(), gY + g.getHeight());

			rectH = new Rect(hX, hY, hX + h.getWidth(), hY + h.getHeight());

			rectI = new Rect(iX, iY, iX + i.getWidth(), iY + i.getHeight());

			rectJ = new Rect(jX, jY, jX + j.getWidth(), jY + j.getHeight());

			rectK = new Rect(kX, kY, kX + k.getWidth(), kY + k.getHeight());

			rectL = new Rect(lX, lY, lX + l.getWidth(), lY + l.getHeight());

			rectM = new Rect(mX, mY, mX + m.getWidth(), mY + m.getHeight());

			rectN = new Rect(nX, nY, nX + n.getWidth(), nY + n.getHeight());

			rectO = new Rect(oX, oY, oX + o.getWidth(), oY + o.getHeight());

			rectP = new Rect(pX, pY, pX + p.getWidth(), pY + p.getHeight());

			rectQ = new Rect(qX, qY, qX + q.getWidth(), qY + q.getHeight());

			rectR = new Rect(rX, rY, rX + r.getWidth(), rY + r.getHeight());

			rectS = new Rect(sX, sY, sX + s.getWidth(), sY + s.getHeight());

			rectT = new Rect(tX, tY, tX + t.getWidth(), tY + t.getHeight());

			rectU = new Rect(uX, uY, uX + u.getWidth(), uY + u.getHeight());

			rectV = new Rect(vX, vY, vX + v.getWidth(), vY + v.getHeight());

			rectW = new Rect(wX, wY, wX + w.getWidth(), wY + w.getHeight());

			rectX = new Rect(xX, xY, xX + x.getWidth(), xY + x.getHeight());

			rectY = new Rect(yX, yY, yX + y.getWidth(), yY + y.getHeight());

			rectZ = new Rect(zX, zY, zX + z.getWidth(), zY + z.getHeight());

			rectDelete = new Rect(deleteX, deleteY,
					deleteX + delete.getWidth(), deleteY + delete.getHeight());

			rectEnter = new Rect(enterX, enterY, enterX + enter.getWidth(),
					enterY + enter.getHeight());

		}

		Runnable mLongPressed = new Runnable() {
			public void run() {
				buttonOnTouch(true);
			}
		};

		private boolean containsRect(MotionEvent event) {
			if (rectA.contains((int) event.getX(), (int) event.getY())) {
				keyType = "a";
				view = a;

			} else if (rectB.contains((int) event.getX(), (int) event.getY())) {
				keyType = "b";
				view = b;

			} else if (rectC.contains((int) event.getX(), (int) event.getY())) {
				keyType = "c";
				view = c;

			} else if (rectD.contains((int) event.getX(), (int) event.getY())) {
				keyType = "d";
				view = d;

			} else if (rectE.contains((int) event.getX(), (int) event.getY())) {
				keyType = "e";
				view = e;

			} else if (rectF.contains((int) event.getX(), (int) event.getY())) {
				keyType = "f";
				view = f;

			} else if (rectG.contains((int) event.getX(), (int) event.getY())) {
				keyType = "g";
				view = g;

			} else if (rectH.contains((int) event.getX(), (int) event.getY())) {
				keyType = "h";
				view = h;

			} else if (rectI.contains((int) event.getX(), (int) event.getY())) {
				keyType = "i";
				view = i;

			} else if (rectJ.contains((int) event.getX(), (int) event.getY())) {
				keyType = "j";
				view = j;

			} else if (rectK.contains((int) event.getX(), (int) event.getY())) {
				keyType = "k";
				view = k;

			} else if (rectL.contains((int) event.getX(), (int) event.getY())) {
				keyType = "l";
				view = l;

			} else if (rectM.contains((int) event.getX(), (int) event.getY())) {
				keyType = "m";
				view = m;

			} else if (rectN.contains((int) event.getX(), (int) event.getY())) {
				keyType = "n";
				view = n;

			} else if (rectO.contains((int) event.getX(), (int) event.getY())) {
				keyType = "o";
				view = o;

			} else if (rectP.contains((int) event.getX(), (int) event.getY())) {
				keyType = "p";
				view = p;

			} else if (rectQ.contains((int) event.getX(), (int) event.getY())) {
				keyType = "q";
				view = q;

			} else if (rectR.contains((int) event.getX(), (int) event.getY())) {
				keyType = "r";
				view = r;

			} else if (rectS.contains((int) event.getX(), (int) event.getY())) {
				keyType = "s";
				view = s;

			} else if (rectT.contains((int) event.getX(), (int) event.getY())) {
				keyType = "t";
				view = t;

			} else if (rectU.contains((int) event.getX(), (int) event.getY())) {
				keyType = "u";
				view = u;

			} else if (rectV.contains((int) event.getX(), (int) event.getY())) {
				keyType = "v";
				view = v;

			} else if (rectW.contains((int) event.getX(), (int) event.getY())) {
				keyType = "w";
				view = w;

			} else if (rectX.contains((int) event.getX(), (int) event.getY())) {
				keyType = "x";
				view = x;

			} else if (rectY.contains((int) event.getX(), (int) event.getY())) {
				keyType = "y";
				view = y;

			} else if (rectZ.contains((int) event.getX(), (int) event.getY())) {
				keyType = "z";
				view = z;

			} else if (rectDelete.contains((int) event.getX(),
					(int) event.getY())) {
				keyType = KeyTouchMessage.KEY_DELETE;
				view = delete;

			} else if (rectEnter.contains((int) event.getX(),
					(int) event.getY())) {
				keyType = KeyTouchMessage.KEY_ENTER;
				view = enter;

			} else {
				keyType = "-1";
				view = null;
				return false;
			}
			return true;
		}

		public synchronized boolean onTouch(MotionEvent event) {

			if (gDetector.onTouchEvent(event)) {
				handler.removeCallbacks(mLongPressed);
				buttonOnTouch(false);
				return false;

			} else {

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:

					mDownX = event.getX();
					mDownY = event.getY();

					containsRect(event);

					handler.postDelayed(mLongPressed, 100);
					return true;

				case MotionEvent.ACTION_UP:
					handler.removeCallbacks(mLongPressed);
					buttonOnTouch(false);

					if (containsRect(event)) {
						click();
					}

					keyType = "-1";
					return false;

				case MotionEvent.ACTION_CANCEL:
					handler.removeCallbacks(mLongPressed);

					buttonOnTouch(false);
					keyType = "-1";
					return false;

				case MotionEvent.ACTION_MOVE:
					int SCROLL_THRESHOLD = 10;

					if ((Math.abs(mDownX - event.getX()) > SCROLL_THRESHOLD || Math
							.abs(mDownY - event.getY()) > SCROLL_THRESHOLD)) {

						View tempView = view;
						String temp = keyType;
						containsRect(event);

						if (!temp.equals(keyType)) {
							handler.removeCallbacks(mLongPressed);
							View newView = view;
							String newKey = keyType;
							view = tempView;
							keyType = temp;
							buttonOnTouch(false);

							keyType = newKey;
							view = newView;

							if (!keyType.equals("-1")) {
								handler.postDelayed(mLongPressed, 100);
							}
						}

						return true;
					}
				}

			}
			return false;
		}

		private void click() {
			UserLogger.logAction(UserLogger.UserView.KEYBOARD,
					UserLogger.Action.CLICK_KEY, keyType, keyBoardText.getText().length());
			
			if(keyBoardText.getText().length() >= 10 && keyType != KeyTouchMessage.KEY_DELETE) {
				return;
			}

			if (keyType.equals("q")) {
				if (keyBoardText.getText().length() == 0) {
					keyBoardText.append("Q");
				} else {
					keyBoardText.append("q");
				}
			} else if (keyType.equals("w")) {
				if (keyBoardText.getText().length() == 0) {
					keyBoardText.append("W");
				} else {
					keyBoardText.append("w");
				}
			} else if (keyType.equals("e")) {
				if (keyBoardText.getText().length() == 0) {
					keyBoardText.append("E");
				} else {
					keyBoardText.append("e");
				}
			} else if (keyType.equals("r")) {
				if (keyBoardText.getText().length() == 0) {
					keyBoardText.append("R");
				} else {
					keyBoardText.append("r");
				}
			} else if (keyType.equals("t")) {
				if (keyBoardText.getText().length() == 0) {
					keyBoardText.append("T");
				} else {
					keyBoardText.append("t");
				}
			} else if (keyType.equals("z")) {
				if (keyBoardText.getText().length() == 0) {
					keyBoardText.append("Z");
				} else {
					keyBoardText.append("z");
				}
			} else if (keyType.equals("u")) {
				if (keyBoardText.getText().length() == 0) {
					keyBoardText.append("U");
				} else {
					keyBoardText.append("u");
				}
			} else if (keyType.equals("i")) {
				if (keyBoardText.getText().length() == 0) {
					keyBoardText.append("I");
				} else {
					keyBoardText.append("i");
				}
			} else if (keyType.equals("o")) {
				if (keyBoardText.getText().length() == 0) {
					keyBoardText.append("O");
				} else {
					keyBoardText.append("o");
				}
			} else if (keyType.equals("p")) {
				if (keyBoardText.getText().length() == 0) {
					keyBoardText.append("P");
				} else {
					keyBoardText.append("p");
				}
			} else if (keyType.equals("a")) {
				if (keyBoardText.getText().length() == 0) {
					keyBoardText.append("A");
				} else {
					keyBoardText.append("a");
				}
			} else if (keyType.equals("s")) {
				if (keyBoardText.getText().length() == 0) {
					keyBoardText.append("S");
				} else {
					keyBoardText.append("s");
				}
			} else if (keyType.equals("d")) {
				if (keyBoardText.getText().length() == 0) {
					keyBoardText.append("D");
				} else {
					keyBoardText.append("d");
				}
			} else if (keyType.equals("f")) {
				if (keyBoardText.getText().length() == 0) {
					keyBoardText.append("F");
				} else {
					keyBoardText.append("f");
				}
			} else if (keyType.equals("g")) {
				if (keyBoardText.getText().length() == 0) {
					keyBoardText.append("G");
				} else {
					keyBoardText.append("g");
				}
			} else if (keyType.equals("h")) {
				if (keyBoardText.getText().length() == 0) {
					keyBoardText.append("H");
				} else {
					keyBoardText.append("h");
				}
			} else if (keyType.equals("j")) {
				if (keyBoardText.getText().length() == 0) {
					keyBoardText.append("J");
				} else {
					keyBoardText.append("j");
				}
			} else if (keyType.equals("k")) {
				if (keyBoardText.getText().length() == 0) {
					keyBoardText.append("K");
				} else {
					keyBoardText.append("k");
				}
			} else if (keyType.equals("l")) {
				if (keyBoardText.getText().length() == 0) {
					keyBoardText.append("L");
				} else {
					keyBoardText.append("l");
				}
			} else if (keyType.equals("y")) {
				if (keyBoardText.getText().length() == 0) {
					keyBoardText.append("Y");
				} else {
					keyBoardText.append("y");
				}
			} else if (keyType.equals("x")) {
				if (keyBoardText.getText().length() == 0) {
					keyBoardText.append("X");
				} else {
					keyBoardText.append("x");
				}
			} else if (keyType.equals("c")) {
				if (keyBoardText.getText().length() == 0) {
					keyBoardText.append("C");
				} else {
					keyBoardText.append("c");
				}
			} else if (keyType.equals("v")) {
				if (keyBoardText.getText().length() == 0) {
					keyBoardText.append("V");
				} else {
					keyBoardText.append("v");
				}
			} else if (keyType.equals("b")) {
				if (keyBoardText.getText().length() == 0) {
					keyBoardText.append("B");
				} else {
					keyBoardText.append("b");
				}
			} else if (keyType.equals("n")) {
				if (keyBoardText.getText().length() == 0) {
					keyBoardText.append("N");
				} else {
					keyBoardText.append("n");
				}
			} else if (keyType.equals("m")) {
				if (keyBoardText.getText().length() == 0) {
					keyBoardText.append("M");
				} else {
					keyBoardText.append("m");
				}
			} else if (keyType.equals(KeyTouchMessage.KEY_DELETE)) {
				if (keyBoardText.getText().length() > 0) {
					keyBoardText.getText().delete(
							keyBoardText.getText().length() - 1,
							keyBoardText.getText().length());
				}
			} else if (keyType.equals(KeyTouchMessage.KEY_ENTER)) {
				finishTyping();
				return;
			} else {
				return;
			}
			
			if(listValues.contains(keyBoardText.getText().toString())) {
				rightWord = true;
				rightText.setImageResource(R.drawable.check);
			} else {
				rightText.setImageResource(R.drawable.redx);				
				rightWord = false;
			}
			sendToService(ServerService.MSG_KEYBOARD);
		}

		private void buttonOnTouch(boolean touching) {

			if (touching && view != null) {
				view.setBackgroundResource(R.color.orange);
			} else if (view != null) {
				view.setBackgroundResource(R.color.grey);
			}

			Message message = Message.obtain(null, ServerService.MSG_KEYTOUCH,
					0, 0);

			KeyTouchMessage touchMessage = new KeyTouchMessage(keyType,
					touching);

			message.getData().putParcelable("KeyTouch", touchMessage);

			try {
				service.send(message);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

	}

	public class GestureListener implements OnGestureListener {

		@Override
		public boolean onDown(MotionEvent e) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean onFling(MotionEvent start, MotionEvent finish,
				float xVelocity, float yVelocity) {

			final int SWIPE_THRESHOLD = 100;
			final int SWIPE_VELOCITY_THRESHOLD = 120;

			try {
				float diffY = finish.getY() - start.getY();
				float diffX = finish.getX() - start.getX();

				if (Math.abs(diffX) > Math.abs(diffY)) {
					if (Math.abs(diffX) > SWIPE_THRESHOLD
							&& Math.abs(xVelocity) > SWIPE_VELOCITY_THRESHOLD) {
						if (diffX > 0) {
							onSwipeRight();
						} else {
							onSwipeLeft();
						}
					}
				} else {
					if (Math.abs(diffY) > SWIPE_THRESHOLD
							&& Math.abs(yVelocity) > SWIPE_VELOCITY_THRESHOLD) {
						if (diffY > 0) {
							onSwipeDown();
						} else {
							onSwipeUp();
						}
					}
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
			return true;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {

		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// TODO Auto-generated method stub

			return false;

		}

		private void onSwipeLeft() {
			// TODO Auto-generated method stub

		}

		private void onSwipeRight() {
			UserLogger.logAction(UserLogger.UserView.KEYBOARD,
					UserLogger.Action.SWIPE_RIGHT, "", -1);

			sendToService(ServerService.MSG_ACTIVITY);
			service.unbind();
			Intent i = new Intent();
			setResult(1, i);
			finish();
		}

		private void onSwipeDown() {
		}

		private void onSwipeUp() {
		}

	}

}