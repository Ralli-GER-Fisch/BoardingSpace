package de.boardingspace.ralligerfisch.util;

public class Color {
	public static final Color GREEN = new Color(0,1,0,1);
	
	
	public float r,g,b,a;
	public Color(float r,float g,float b,float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
	public Color(float r,float g,float b) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = 1f;
	}
	
	public float getRed(){return r;}
	public float getGreen(){return g;}
	public float getBlue(){return b;}
	public float getAlpha(){return a;}
}
