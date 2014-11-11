package com.hudtouchscreen.message;

public class ShuffleMessage implements Message{
	private static final long serialVersionUID = 8619956003794177707L;
	private boolean shuffle;
	
	public ShuffleMessage(boolean shuffle) {
		this.shuffle = shuffle;
	}
	
	public boolean isShuffled(){
		return shuffle;
	}
}
