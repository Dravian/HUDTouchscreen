package com.hudtouchscreen.hudmessage;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;


@SuppressWarnings("serial")
public class SongTitleMessage implements Parcelable, Serializable, HudMessage{	
	private String songTitle;
	
	public SongTitleMessage(String songTitle) {
		this.songTitle = songTitle;
	}
	
	public SongTitleMessage(Parcel in) {
		readFromParcel(in);
	} 
	
	public String getSongTitle() {
		return songTitle;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		dest.writeString(songTitle);
	}
	
	private void readFromParcel(Parcel in) {
		songTitle = in.readString();
	}
	
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR =
	    	new SongTitleMessage.Creator() {
	            public SongTitleMessage createFromParcel(Parcel in) {
	                return new SongTitleMessage(in);
	            }
	 
	            public SongTitleMessage[] newArray(int size) {
	                return new SongTitleMessage[size];
	            }

	        };


}
