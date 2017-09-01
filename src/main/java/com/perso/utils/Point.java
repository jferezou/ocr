package com.perso.utils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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


	@Override
	public String toString() {
		return "Point [x=" + x + ", y=" + y + "]";
	}
	
	
}
