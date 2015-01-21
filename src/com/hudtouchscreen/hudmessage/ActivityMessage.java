package com.hudtouchscreen.hudmessage;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

@SuppressWarnings("serial")
public class ActivityMessage implements Parcelable, Serializable, HudMessage{
	public static final int BACK_TO_MAIN = 0;
	public static final int SWITCH_TO_LIST = 1;
	public static final int SWITCH_TO_KEYBOARD = 2;
	private int activity;
	
	public ActivityMessage(int change) {
		this.activity = change;;
	}
	
	public ActivityMessage(Parcel in) {
		readFromParcel(in);
	} 
	
	public int getActivity() {
		return activity;
	}
	
	private void readFromParcel(Parcel in) {
		activity = in.readInt();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		dest.writeInt(activity);
	}
	
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR =
	    	new ActivityMessage.Creator() {
	            public ActivityMessage createFromParcel(Parcel in) {
	                return new ActivityMessage(in);
	            }
	 
	            public ActivityMessage[] newArray(int size) {
	                return new ActivityMessage[size];
	            }

	        };

	
}
