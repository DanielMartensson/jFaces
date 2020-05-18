package se.danielmartensson.fisherfaces.matlab;

import org.ojalgo.matrix.Primitive64Matrix;
import org.ojalgo.matrix.Primitive64Matrix.LogicalBuilder;


public class Repmat {

	static public Primitive64Matrix repmat(Primitive64Matrix X, int m, long n) {

	    LogicalBuilder builder = X.logical();

	    for (int i = 1; i < m; i++) {
	        builder.below(X);
	    }

	    Primitive64Matrix firstCol = builder.get();

	    for (int j = 1; j < n; j++) {
	        builder.right(firstCol);
	    }

	    return builder.get();
	}
}
