package se.danielmartensson.fisherfaces;

import org.ojalgo.matrix.Primitive64Matrix;
import se.danielmartensson.fisherfaces.matlab.Repmat;

public class Project {

	/**
	 * X [dim x num_data] input data 
	 * W [dim x num_components] transformation matrix
	 * mu [dim x 1] sample mean
	 * 
	 * @return
	 */
	static public Primitive64Matrix project(Primitive64Matrix xTrainPrimitive, Primitive64Matrix wpca, Primitive64Matrix mu) {
		xTrainPrimitive = xTrainPrimitive.subtract(Repmat.repmat(mu, 1, xTrainPrimitive.countColumns())); // MATLAB: X = X - repmat(mu, 1, size(X, 2))
		return wpca.transpose().multiply(xTrainPrimitive); // MATLAB: Y = W'*X
	}
}
