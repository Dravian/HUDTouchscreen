package hudmessage;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class ConnectionMessage implements Parcelable, Serializable, HudMessage{
	private int connected;
	
		
	public ConnectionMessage(boolean connected) {
		this.connected = (connected ? 1 : 0);
	}
	
	public ConnectionMessage(Parcel in) {
		readFromParcel(in);
	} 
	
	private void readFromParcel(Parcel in) {
		connected = in.readInt();
	}

	public boolean isConnected(){
		
		if(connected == 1) {
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
		dest.writeInt(connected);
	}
	
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR =
	    	new ConnectionMessage.Creator() {
	            public ConnectionMessage createFromParcel(Parcel in) {
	                return new ConnectionMessage(in);
	            }
	 
	            public ConnectionMessage[] newArray(int size) {
	                return new ConnectionMessage[size];
	            }

	        };

}