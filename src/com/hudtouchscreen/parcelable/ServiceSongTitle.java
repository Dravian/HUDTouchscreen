package com.hudtouchscreen.parcelable;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

@SuppressWarnings("serial")
public class ServiceSongTitle implements Parcelable, Serializable{
	private String songTitle;
	
	public ServiceSongTitle() {}
	
	public ServiceSongTitle(Parcel in) {
		readFromParcel(in);
	} 
	
	public String getSongTitle() {
		return songTitle;
	}
	
	public void setSongTitle(String strValue) {
		this.songTitle = strValue;
	}
	
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		arg0.writeString(songTitle);
	}
	
	private void readFromParcel(Parcel in) {
		 
		// We just need to read back each
		// field in the order that it was
		// written to the parcel
		songTitle = in.readString();
	}
	
	public static final Parcelable.Creator CREATOR =
	    	new ServiceSongTitle.Creator() {
	            public ServiceSongTitle createFromParcel(Parcel in) {
	                return new ServiceSongTitle(in);
	            }
	 
	            public ServiceSongTitle[] newArray(int size) {
	                return new ServiceSongTitle[size];
	            }

	        };

}
