package hudmessage;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;


@SuppressWarnings("serial")
public class SongtitleMessage implements Parcelable, Serializable, HudMessage{	
	private String text;
	
	public SongtitleMessage(String text) {
		this.text = text;
	}
	
	public SongtitleMessage(Parcel in) {
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
	    	new SongtitleMessage.Creator() {
	            public SongtitleMessage createFromParcel(Parcel in) {
	                return new SongtitleMessage(in);
	            }
	 
	            public SongtitleMessage[] newArray(int size) {
	                return new SongtitleMessage[size];
	            }

	        };


}
