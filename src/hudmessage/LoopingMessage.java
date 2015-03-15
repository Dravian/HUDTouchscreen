package hudmessage;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

@SuppressWarnings("serial")
public class LoopingMessage implements Parcelable, Serializable, HudMessage{
	private int looping;
	
		
	public LoopingMessage(boolean looping) {
		this.looping = (looping ? 1 : 0);
	}
	
	public LoopingMessage(Parcel in) {
		readFromParcel(in);
	} 
	
	private void readFromParcel(Parcel in) {
		// TODO Auto-generated method stub
		looping = in.readInt();
	}

	public boolean isLooping(){
		
		if(looping == 1) {
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
		dest.writeInt(looping);
	}
	
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR =
	    	new LoopingMessage.Creator() {
	            public LoopingMessage createFromParcel(Parcel in) {
	                return new LoopingMessage(in);
	            }
	 
	            public LoopingMessage[] newArray(int size) {
	                return new LoopingMessage[size];
	            }

	        };

}
