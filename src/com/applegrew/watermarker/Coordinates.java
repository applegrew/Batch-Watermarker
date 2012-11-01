package com.applegrew.watermarker;
public class Coordinates {
	
	private int xCoord;
	private int yCoord;
	
	public Coordinates (int xCoord, int yCoord){
		this.xCoord = xCoord;
		this.yCoord = yCoord;
	}
	
	public int getYCoord() {
		return this.yCoord;
	}
	
	public int getXCoord() {
		return this.xCoord;
	}
	
	public void setYCoord(int yCoord) {
		this.yCoord = yCoord;
	}
	
	public void setXCoord(int xCoord) {
		this.xCoord = xCoord;
	}
}
