package com.hudtouchscreen.hudmessage;

public class FinalTimeMessage implements HudMessage{
	private double finalTime;
	
	public FinalTimeMessage(double finalTime) {
		this.finalTime = finalTime;
	}
	
	public double getFinalTime() {
		return finalTime;
	}
}
