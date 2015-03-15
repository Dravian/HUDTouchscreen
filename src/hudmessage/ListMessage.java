package hudmessage;

import java.io.Serializable;
import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

@SuppressWarnings("serial")
public class ListMessage implements Parcelable, Serializable, HudMessage {
	ArrayList<String> list;

	public ListMessage(ArrayList<String> list) {
		this.list = new ArrayList<String>();

		for (String item : list) {
			this.list.add(item);
		}

		 if (list.size() < 5) {
			for (int i = list.size(); i < 5; i++) {
				list.add("");
			}
		}

	}

	public ListMessage(Parcel in) {
		readFromParcel(in);
	}

	private void readFromParcel(Parcel in) {
		list = in.createStringArrayList();
	}

	public void fillList(ArrayList<String> emptyList) {
		if (emptyList.size() == 0) {
			for (String item : list) {
				emptyList.add(item);
			}
		}

	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeStringList(list);
	}

	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new ListMessage.Creator() {
		public ListMessage createFromParcel(Parcel in) {
			return new ListMessage(in);
		}

		public ListMessage[] newArray(int size) {
			return new ListMessage[size];
		}

	};
}
