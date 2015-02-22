package com.hudtouchscreen.hudmessage;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

@SuppressWarnings("serial")
public class TouchMessage implements Parcelable, Serializable, HudMessage{
	private int buttonType;
	private int touching;
	public final static int PLAYBUTTON = 1;
	public final static int PAUSEBUTTON = 2;
	public final static int STOPBUTTON = 3;
	public final static int SHUFFLEBUTTON = 4;
	public final static int LOOPINGBUTTON = 5;
	public final static int STARTBUTTON = 6;
	public final static int LIST1 = 7;
	public final static int LIST2 = 8;
	public final static int LIST3 = 9;
	public final static int LIST4 = 10;
	public final static int LIST5 = 11;
	
	
	
	public TouchMessage(int buttonType, boolean touching) {
		this.buttonType = buttonType;
		this.touching = (touching ? 1 : 0);
	}
	
	public TouchMessage() {
		buttonType = 0;
		touching = 0;
	}
	
	public TouchMessage(Parcel in) {
		readFromParcel(in);
	} 
	
	private void readFromParcel(Parcel in) {
		buttonType  = in.readInt();
		touching = in.readInt();
	}

	public int getButtonType() {
		return buttonType;
	}
	
	public boolean isTouching() {
		if(touching == 1) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(buttonType);
		dest.writeInt(touching);
	}
	
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR =
	    	new TouchMessage.Creator() {
	            public TouchMessage createFromParcel(Parcel in) {
	                return new TouchMessage(in);
	            }
	 
	            public TouchMessage[] newArray(int size) {
	                return new TouchMessage[size];
	            }

	        };
}
