package com.hudtouchscreen.hudmessage;

import java.io.Serializable;
import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

@SuppressWarnings("serial")
public class ActivityMessage implements Parcelable, Serializable, HudMessage {
	public static final int BACK_TO_MAIN = 0;
	public static final int SWITCH_TO_LIST = 1;
	public static final int SWITCH_TO_KEYBOARD = 2;
	private int activity;
	private ArrayList<String> list;

	public ActivityMessage(int change, ArrayList<String> list) {
		this.activity = change;

		this.list = new ArrayList<String>();
		
		if (list != null) {
			for (String item : list) {
				this.list.add(item);
			}
		}
		

	}

	public ActivityMessage(Parcel in) {
		readFromParcel(in);
	}

	public int getActivity() {
		return activity;
	}

	public void fillEmptyList(ArrayList<String> emptyList) {
		
			if(emptyList.size() == 0) {
				for(String item : list) {
					emptyList.add(item);
				}
			}
		
	}

	private void readFromParcel(Parcel in) {
		activity = in.readInt();
		list = in.createStringArrayList();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		dest.writeInt(activity);
		dest.writeStringList(list);
	}

	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new ActivityMessage.Creator() {
		public ActivityMessage createFromParcel(Parcel in) {
			return new ActivityMessage(in);
		}

		public ActivityMessage[] newArray(int size) {
			return new ActivityMessage[size];
		}

	};

}
