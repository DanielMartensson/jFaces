package se.danielmartensson.fisherfaces;

import org.ojalgo.matrix.Primitive64Matrix;
import lombok.Data;

@Data
public class Model {
	private String name;
	private Primitive64Matrix D;
	private Primitive64Matrix W;
	private Primitive64Matrix P;
	private long num_components;
	private Primitive64Matrix mu;
	private Primitive64Matrix y;
}
