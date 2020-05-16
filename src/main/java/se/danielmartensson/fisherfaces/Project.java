package se.danielmartensson.fisherfaces;

import org.apache.commons.math3.linear.RealMatrix;

import se.danielmartensson.fisherfaces.matlab.Repmat;

public class Project {

	/**
	 * X [dim x num_data] input data W [dim x num_components] transformation matrix
	 * mu [dim x 1] sample mean
	 * 
	 * @return
	 */
	static public RealMatrix project(RealMatrix X, RealMatrix W, RealMatrix mu) {
		X = X.subtract(Repmat.repmat(mu, 1, X.getColumnDimension())); // MATLAB: X = X - repmat(mu, 1, size(X, 2))
		return W.transpose().multiply(X); // MATLAB: Y = W'*X
	}
}
