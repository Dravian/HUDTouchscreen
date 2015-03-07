package com.hudtouchscreen.hudmessage;

	import java.io.Serializable;

	import android.os.Parcel;
	import android.os.Parcelable;

@SuppressWarnings("serial")
public class SeekbarLogMessage  implements Parcelable, Serializable, HudMessage {


		private int seekbarLog;

		public SeekbarLogMessage(boolean seekbarLog) {
			this.seekbarLog = (seekbarLog ? 1 : 0);
		}

		public SeekbarLogMessage(Parcel in) {
			readFromParcel(in);
		}

		private void readFromParcel(Parcel in) {
			seekbarLog = in.readInt();
		}

		public boolean checkSeekbarLog() {

			if (seekbarLog == 1) {
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
			dest.writeInt(seekbarLog);
		}
		
		@SuppressWarnings("rawtypes")
		public static final Parcelable.Creator CREATOR = new ShuffleMessage.Creator() {
			public SeekbarLogMessage createFromParcel(Parcel in) {
				return new SeekbarLogMessage(in);
			}

			public SeekbarLogMessage[] newArray(int size) {
				return new SeekbarLogMessage[size];
			}

		};
	
}
