package se.danielmartensson.fisherfaces.matlab.ojalgoroutines;

import org.ojalgo.matrix.Primitive64Matrix;
import org.ojalgo.matrix.decomposition.SingularValue;
import org.ojalgo.matrix.store.Primitive64Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Svd {

	static Logger logger = LoggerFactory.getLogger(Svd.class);

	static public void svd(Primitive64Matrix X, Primitive64Store U, long rows, long columns) {

		// Perform SVD
		SingularValue<Double> svd = SingularValue.PRIMITIVE.make(X, false);
		svd.decompose(X);
		logger.info("Done with Singular Value Decomposition");

		// Copy over to U
		U.accept(svd.getU());
	}
}
