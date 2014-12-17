package com.hudtouchscreen.hudmessage;

public class LoopingMessage implements HudMessage{
	private static final long serialVersionUID = -8802014776208355439L;
	private boolean looping;
	
	public LoopingMessage(boolean looping) {
		this.looping = looping;
	}
	
	public boolean isLooping() {
		return looping;
	}

}
