package com.hudtouchscreen.hudmessage;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;


@SuppressWarnings("serial")
public class TextMessage implements Parcelable, Serializable, HudMessage{	
	private String text;
	
	public TextMessage(String text) {
		this.text = text;
	}
	
	public TextMessage(Parcel in) {
		readFromParcel(in);
	} 
	
	public String getText() {
		return text;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		dest.writeString(text);
	}
	
	private void readFromParcel(Parcel in) {
		text = in.readString();
	}
	
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR =
	    	new TextMessage.Creator() {
	            public TextMessage createFromParcel(Parcel in) {
	                return new TextMessage(in);
	            }
	 
	            public TextMessage[] newArray(int size) {
	                return new TextMessage[size];
	            }

	        };


}
