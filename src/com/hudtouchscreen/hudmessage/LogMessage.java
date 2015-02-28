package com.hudtouchscreen.hudmessage;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

@SuppressWarnings("serial")
public class LogMessage implements Parcelable, Serializable, HudMessage{
	private int logStatus;
	
	public LogMessage(boolean logStatus) {
		this.logStatus = (logStatus ? 1 : 0) ;
	}
	
	public LogMessage(Parcel in) {
		readFromParcel(in);
	} 
	
	private void readFromParcel(Parcel in) {
		logStatus = in.readInt();
	}

	
	public boolean getLogStatus() {
		if(logStatus == 1) {
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
		dest.writeInt(logStatus);
	}
	
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR =
	    	new LogMessage.Creator() {
	            public LogMessage createFromParcel(Parcel in) {
	                return new LogMessage(in);
	            }
	 
	            public LogMessage[] newArray(int size) {
	                return new LogMessage[size];
	            }

	        };
}
