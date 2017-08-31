package com.perso.utils;

public class Point {

	private int x;
	
	private int y;
	private final static double MULTIPLIER = 3.064;

	public Point(int x, int y) {
		super();
		Double tempX = x * MULTIPLIER;
		Double tempY = y * MULTIPLIER;
		this.x = tempX.intValue();
		this.y = tempY.intValue();
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	@Override
	public String toString() {
		return "Point [x=" + x + ", y=" + y + "]";
	}
	
	
}
