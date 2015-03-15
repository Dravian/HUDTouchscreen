package hudmessage;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

@SuppressWarnings("serial")
public class ShuffleMessage implements Parcelable, Serializable, HudMessage {
	private int shuffle;

	public ShuffleMessage(boolean shuffle) {
		this.shuffle = (shuffle ? 1 : 0);
	}

	public ShuffleMessage(Parcel in) {
		readFromParcel(in);
	}

	private void readFromParcel(Parcel in) {
		shuffle = in.readInt();
	}

	public boolean isShuffled() {

		if (shuffle == 1) {
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
		dest.writeInt(shuffle);
	}
	
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new ShuffleMessage.Creator() {
		public ShuffleMessage createFromParcel(Parcel in) {
			return new ShuffleMessage(in);
		}

		public ShuffleMessage[] newArray(int size) {
			return new ShuffleMessage[size];
		}

	};
}
