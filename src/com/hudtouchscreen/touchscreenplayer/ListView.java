package com.hudtouchscreen.touchscreenplayer;

import com.touchscreen.touchscreenplayer.R;
import com.touchscreen.touchscreenplayer.R.layout;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

public class ListView extends ListActivity {

	public void test(View view) {
		Intent i = new Intent();
		int ins = 10;

		i.putExtra("Song", ins);

		setResult(100, i);
		finish();
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.list_view);
		
		String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
				"Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
				"Linux", "OS/2", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, values);
		setListAdapter(adapter);
	}

	protected void onListItemClick(ListView l, View v, int position, long id) {
		String item = (String) getListAdapter().getItem(position);

		Intent i = new Intent();

		i.putExtra("Song", position);

		setResult(100, i);
		finish();
	}
}
