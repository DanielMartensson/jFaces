package se.danielmartensson.fisherfaces;

import org.apache.commons.math3.linear.RealMatrix;

import lombok.Data;

@Data
public class Model {
	private String name;
	private RealMatrix D;
	private RealMatrix W;
	private RealMatrix P;
	private int num_components;
	private RealMatrix mu;
	private RealMatrix y;
}
