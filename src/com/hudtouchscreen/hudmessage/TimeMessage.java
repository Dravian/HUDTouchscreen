package com.hudtouchscreen.hudmessage;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

@SuppressWarnings("serial")
public class TimeMessage implements Parcelable, Serializable, HudMessage{
	private double startTime;
	private double endTime;
	
	public TimeMessage(double startTime, double endTime) {
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	public TimeMessage() {
		startTime = 0;
		endTime = 0;
	}
	
	public TimeMessage(Parcel in) {
		readFromParcel(in);
	} 
	
	private void readFromParcel(Parcel in) {
		startTime  = in.readDouble();
		endTime = in.readDouble();
	}

	public void setStartTime(double startTime) {
		this.startTime = startTime;
	}
	
	public void setEndTime(double endTime) {
		this.endTime = endTime;
	}
	
	public double getStartTime() {
		return startTime;
	}
	
	public double getEndTime() {
		return endTime;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDouble(startTime);
		dest.writeDouble(endTime);
	}
	
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR =
	    	new TimeMessage.Creator() {
	            public TimeMessage createFromParcel(Parcel in) {
	                return new TimeMessage(in);
	            }
	 
	            public TimeMessage[] newArray(int size) {
	                return new TimeMessage[size];
	            }

	        };
}
