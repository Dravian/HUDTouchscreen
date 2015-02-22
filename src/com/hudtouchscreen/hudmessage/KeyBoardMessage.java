package com.hudtouchscreen.hudmessage;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;


@SuppressWarnings("serial")
public class KeyBoardMessage implements Parcelable, Serializable, HudMessage{	
	private String letter;
	private int rightWord;
	
	public KeyBoardMessage(String letter, boolean rightWord) {
		this.letter = letter;
		this.rightWord = (rightWord ? 1 : 0);
	}
	
	public KeyBoardMessage(Parcel in) {
		readFromParcel(in);
	} 
	
	public String getText() {
		return letter;
	}
	
	public boolean isRightWord() {
		if(rightWord == 1) {
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
	public void writeToParcel(Parcel dest, int arg1) {
		dest.writeString(letter);
		dest.writeInt(rightWord);
	}
	
	private void readFromParcel(Parcel in) {
		letter = in.readString();
		rightWord = in.readInt();
	}
	
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR =
	    	new KeyBoardMessage.Creator() {
	            public KeyBoardMessage createFromParcel(Parcel in) {
	                return new KeyBoardMessage(in);
	            }
	 
	            public KeyBoardMessage[] newArray(int size) {
	                return new KeyBoardMessage[size];
	            }

	        };


}

