package com.perso.pojo.ocr;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Point {

	private int x;
	
	private int y;
	private final static double MULTIPLIER = 1;

	public Point(final int x, final int y) {
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
