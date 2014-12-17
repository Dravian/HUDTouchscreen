package com.hudtouchscreen.hudmessage;


public class SongTitleMessage implements HudMessage{
	private static final long serialVersionUID = 4589260509634629772L;
	String songTitle;

	public SongTitleMessage(String songTitle) {
		this.songTitle = songTitle;
	}

	public String getSongTitle() {
		return songTitle;
	}

}
