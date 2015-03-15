package hudmessage;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

@SuppressWarnings("serial")
public class TimeMessage implements Parcelable, Serializable, HudMessage{
	private double startTime;
	private double endTime;
	private int progress;
	
	public TimeMessage(double startTime, double endTime, int progress) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.progress = progress;
	}
	
	public TimeMessage() {
		startTime = 0;
		endTime = 0;
		progress = 0;
	}
	
	public TimeMessage(Parcel in) {
		readFromParcel(in);
	} 
	
	private void readFromParcel(Parcel in) {
		startTime  = in.readDouble();
		endTime = in.readDouble();
		progress = in.readInt();
	}

	
	public double getStartTime() {
		return startTime;
	}
	
	public double getEndTime() {
		return endTime;
	}
	
	public int getProgress() {
		return progress;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDouble(startTime);
		dest.writeDouble(endTime);
		dest.writeInt(progress);
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
