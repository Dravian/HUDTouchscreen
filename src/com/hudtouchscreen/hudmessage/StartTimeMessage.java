package com.hudtouchscreen.hudmessage;

public class StartTimeMessage implements HudMessage{
	private double startTime;
	
	public StartTimeMessage(double startTime) {
		this.startTime = startTime;
	}
	
	public double getStartTimeMessage() {
		return startTime;
	}
}
