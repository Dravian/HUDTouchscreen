package com.hudtouchscreen.message;

public class TimeMessage implements Message{
	private static final long serialVersionUID = 3465860934876269231L;
	private double startTime;
	private double endTime;
	
	public TimeMessage(double startTime, double endTime) {
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	public double getStartTime() {
		return startTime;
	}
	
	public double getEndTime() {
		return endTime;
	}
}
