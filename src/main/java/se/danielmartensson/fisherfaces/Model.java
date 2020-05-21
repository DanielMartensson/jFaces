package se.danielmartensson.fisherfaces;

import org.ojalgo.matrix.Primitive64Matrix;


public class Model {
	private String name;
	//private Primitive64Matrix D;
	private Primitive64Matrix W;
	private Primitive64Matrix P;
	//private long num_components;
	private Primitive64Matrix mu;
	private Primitive64Matrix y;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Primitive64Matrix getW() {
		return W;
	}
	public void setW(Primitive64Matrix w) {
		W = w;
	}
	public Primitive64Matrix getMu() {
		return mu;
	}
	public void setMu(Primitive64Matrix mu) {
		this.mu = mu;
	}
	public Primitive64Matrix getP() {
		return P;
	}
	public void setP(Primitive64Matrix p) {
		P = p;
	}
	public Primitive64Matrix getY() {
		return y;
	}
	public void setY(Primitive64Matrix y) {
		this.y = y;
	}
	
}
