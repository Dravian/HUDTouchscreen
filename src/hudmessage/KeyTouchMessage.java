package hudmessage;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

@SuppressWarnings("serial")
public class KeyTouchMessage implements Parcelable, Serializable, HudMessage{
	private String key;
	private int touching;
	public static final String KEY_DELETE = "/";
	public static final String KEY_ENTER = "_";
	
	
	public KeyTouchMessage(String key, boolean touching) {
		this.key = key;
		this.touching = (touching ? 1 : 0);
	}
	
	
	public KeyTouchMessage(Parcel in) {
		readFromParcel(in);
	} 
	
	private void readFromParcel(Parcel in) {
		key  = in.readString();
		touching = in.readInt();
	}

	public String getKey() {
		return key;
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
		dest.writeString(key);
		dest.writeInt(touching);
	}
	
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR =
	    	new KeyTouchMessage.Creator() {
	            public KeyTouchMessage createFromParcel(Parcel in) {
	                return new KeyTouchMessage(in);
	            }
	 
	            public KeyTouchMessage[] newArray(int size) {
	                return new KeyTouchMessage[size];
	            }

	        };
}

