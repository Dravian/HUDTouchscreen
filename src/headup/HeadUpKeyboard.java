package headup;

import service.ServiceManager;
import hudmessage.ActivityMessage;
import hudmessage.KeyTouchMessage;
import hudmessage.KeyboardMessage;
import hudmessage.SongtitleMessage;
import hudmessage.TouchMessage;

import com.touchscreen.touchscreenplayer.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class HeadUpKeyboard extends Activity {
	private ServiceManager service;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.keyboardheadup);
		final View a = findViewById(R.id.a);
		final View b = findViewById(R.id.b);
		final View c = findViewById(R.id.c);
		final View d = findViewById(R.id.d);
		final View e = findViewById(R.id.e);
		final View f = findViewById(R.id.f);
		final View g = findViewById(R.id.g);
		final View h = findViewById(R.id.h);
		final View i = findViewById(R.id.i);
		final View j = findViewById(R.id.j);
		final View k = findViewById(R.id.k);
		final View l = findViewById(R.id.l);
		final View m = findViewById(R.id.m);
		final View n = findViewById(R.id.n);
		final View o = findViewById(R.id.o);
		final View p = findViewById(R.id.p);
		final View q = findViewById(R.id.q);
		final View r = findViewById(R.id.r);
		final View s = findViewById(R.id.s);
		final View t = findViewById(R.id.t);
		final View u = findViewById(R.id.u);
		final View v = findViewById(R.id.v);
		final View w = findViewById(R.id.w);
		final View x = findViewById(R.id.x);
		final View y = findViewById(R.id.y);
		final View z = findViewById(R.id.z);
		final View delete = findViewById(R.id.delete);
		final View enter = findViewById(R.id.enter);
		final ImageView rightText = (ImageView) findViewById(R.id.rightText);
		
		this.service = new ServiceManager(this, ClientService.class,
				new Handler() {

					EditText keyBoardText = (EditText) findViewById(R.id.editKeyBoard);

					@Override
					public void handleMessage(Message msg) {
						final Bundle bundle = msg.getData();
						bundle.setClassLoader(getClassLoader());

						switch (msg.what) {
						case ClientService.MSG_KEYBOARD:
							KeyboardMessage letterMessage = (KeyboardMessage) bundle
									.getParcelable("Keyboard");

							if (letterMessage.getText() != null) {
								final String letter = (letterMessage).getText();
								final boolean rightWord = letterMessage.isRightWord();
								
								runOnUiThread(new Runnable() {

									@Override
									public void run() {
										keyBoardText.setText(letter);
										if(rightWord) {
											rightText.setImageResource(R.drawable.check);
										} else {
											rightText.setImageResource(R.drawable.redx);
										}
									}
								});
							}
							break;
						case ClientService.MSG_KEYTOUCH:
							KeyTouchMessage touchMessage = (KeyTouchMessage)bundle.getParcelable("KeyTouch");
							final String key = touchMessage.getKey();
							final boolean touching = touchMessage.isTouching();
							
							
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									
									if(key.equals("q")) {
										if(touching){
											q.setBackgroundResource(R.color.orange);
										} else {
											q.setBackgroundResource(R.color.grey);
										}
										
										
									} else if(key.equals("w")){
										if(touching){
											w.setBackgroundResource(R.color.orange);
										} else {
											w.setBackgroundResource(R.color.grey);
										}
										
									}else if(key.equals("e")){
										if(touching){
											e.setBackgroundResource(R.color.orange);
										} else {
											e.setBackgroundResource(R.color.grey);
										}
										
										
									}else if(key.equals("r")){
										if(touching){
											r.setBackgroundResource(R.color.orange);
										} else {
											r.setBackgroundResource(R.color.grey);
										}
										
										
									}else if(key.equals("t")){
										if(touching){
											t.setBackgroundResource(R.color.orange);
										} else {
											t.setBackgroundResource(R.color.grey);
										}
										
									}else if(key.equals("z")){
										if(touching){
											z.setBackgroundResource(R.color.orange);
										} else {
											z.setBackgroundResource(R.color.grey);
										}
										
									}else if(key.equals("u")){
										
										if(touching){
											u.setBackgroundResource(R.color.orange);
										} else {
											u.setBackgroundResource(R.color.grey);
										}
										
									}else if(key.equals("i")){
										if(touching){
											i.setBackgroundResource(R.color.orange);
										} else {
											i.setBackgroundResource(R.color.grey);
										}
										
									}else if(key.equals("o")){
										if(touching){
											o.setBackgroundResource(R.color.orange);
										} else {
											o.setBackgroundResource(R.color.grey);
										}
										
									}else if(key.equals("p")){
										if(touching){
											p.setBackgroundResource(R.color.orange);
										} else {
											p.setBackgroundResource(R.color.grey);
										}
										
									}else if(key.equals("a")){
										if(touching){
											a.setBackgroundResource(R.color.orange);
										} else {
											a.setBackgroundResource(R.color.grey);
										}
										
									}else if(key.equals("s")){
										if(touching){
											s.setBackgroundResource(R.color.orange);
										} else {
											s.setBackgroundResource(R.color.grey);
										}
										
									}else if(key.equals("d")){
										if(touching){
											d.setBackgroundResource(R.color.orange);
										} else {
											d.setBackgroundResource(R.color.grey);
										}
										
									}else if(key.equals("f")){
										if(touching){
											f.setBackgroundResource(R.color.orange);
										} else {
											f.setBackgroundResource(R.color.grey);
										}
										
									}else if(key.equals("g")){
										if(touching){
											g.setBackgroundResource(R.color.orange);
										} else {
											g.setBackgroundResource(R.color.grey);
										}
										
									}else if(key.equals("h")){
										if(touching){
											h.setBackgroundResource(R.color.orange);
										} else {
											h.setBackgroundResource(R.color.grey);
										}
										
									}else if(key.equals("j")){
										if(touching){
											j.setBackgroundResource(R.color.orange);
										} else {
											j.setBackgroundResource(R.color.grey);
										}
										
									}else if(key.equals("k")){
										if(touching){
											k.setBackgroundResource(R.color.orange);
										} else {
											k.setBackgroundResource(R.color.grey);
										}
										
									}else if(key.equals("l")){
										if(touching){
											l.setBackgroundResource(R.color.orange);
										} else {
											l.setBackgroundResource(R.color.grey);
										}
										
									}else if(key.equals("y")){
										if(touching){
											y.setBackgroundResource(R.color.orange);
										} else {
											y.setBackgroundResource(R.color.grey);
										}
										
									}else if(key.equals("x")){
										if(touching){
											x.setBackgroundResource(R.color.orange);
										} else {
											x.setBackgroundResource(R.color.grey);
										}
										
									}else if(key.equals("c")){
										if(touching){
											c.setBackgroundResource(R.color.orange);
										} else {
											c.setBackgroundResource(R.color.grey);
										}
										
									}else if(key.equals("v")){
										if(touching){
											v.setBackgroundResource(R.color.orange);
										} else {
											v.setBackgroundResource(R.color.grey);
										}
										
									}else if(key.equals("b")){
										if(touching){
											b.setBackgroundResource(R.color.orange);
										} else {
											b.setBackgroundResource(R.color.grey);
										}
										
									}else if(key.equals("n")){
										if(touching){
											n.setBackgroundResource(R.color.orange);
										} else {
											n.setBackgroundResource(R.color.grey);
										}
										
									}else if(key.equals("m")){
										if(touching){
											m.setBackgroundResource(R.color.orange);
										} else {
											m.setBackgroundResource(R.color.grey);
										}
										
									} else if(key.equals(KeyTouchMessage.KEY_DELETE)) {
										if(touching){
											delete.setBackgroundResource(R.color.orange);
										} else {
											delete.setBackgroundResource(R.color.grey);
										}
									} else if(key.equals(KeyTouchMessage.KEY_ENTER)) {
										if(touching){
											enter.setBackgroundResource(R.color.orange);
										} else {
											enter.setBackgroundResource(R.color.grey);
										}
									}
									
								}
							});
							break;
							
						case ClientService.MSG_ACTIVITY:
							ActivityMessage activityMessage = (ActivityMessage) bundle
									.getParcelable("Activity");

							if (activityMessage.getActivity() == ActivityMessage.BACK_TO_MAIN) {
								finish();
							}
							break;
						default:
						}
					}
				});

		service.start();

	}
	
	@Override
	  protected void onDestroy() {
	    super.onDestroy();
	    
	    try { service.unbind(); }
	    catch (Throwable t) { }

	}
}
