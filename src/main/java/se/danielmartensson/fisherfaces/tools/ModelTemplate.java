package se.danielmartensson.fisherfaces.tools;

import java.io.Serializable;

// Modified model class to save the real model class
public class ModelTemplate implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double[][] W;
	private double[][] P;
	private double[][] y;
	public double[][] getW() {
		return W;
	}
	public void setW(double[][] w) {
		W = w;
	}
	public double[][] getP() {
		return P;
	}
	public void setP(double[][] p) {
		P = p;
	}
	public double[][] getY() {
		return y;
	}
	public void setY(double[][] y) {
		this.y = y;
	}


}
