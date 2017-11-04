package com.perso.pojo.ocr;

public class Zone {

	private Point debut;
	private Point fin;
	private int heigth;
	private int width;
	
	
	public Zone(final Point debut, final Point fin) {
		super();
		this.debut = debut;
		this.fin = fin;
		this.width = this.fin.getX() - this.debut.getX();
		this.heigth = this.fin.getY() - this.debut.getY();
	}

	public Point getDebut() {
		return debut;
	}

	public Point getFin() {
		return fin;
	}

	public int getWidth() {
		return this.width;		
	}

	public int getHeigth() {
		return this.heigth;		
	}

	@Override
	public String toString() {
		return "Zone [debut=" + debut + ", heigth=" + heigth + ", width=" + width + "]";
	}
	
	
}
