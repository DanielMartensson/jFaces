package se.danielmartensson.fisherfaces.matlab.ojalgo;

import org.ojalgo.matrix.Primitive64Matrix;
import org.ojalgo.matrix.decomposition.SingularValue;
import org.ojalgo.matrix.store.MatrixStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Svd {

	static Logger logger = LoggerFactory.getLogger(Svd.class);

	static public void svd(double A[][], double U[][], double S[], int rows, int columns) {

		// Create data
		Primitive64Matrix data = Primitive64Matrix.FACTORY.rows(A);

		// Perform SVD
		SingularValue<Double> svd = SingularValue.PRIMITIVE.make(data, false);
		svd.decompose(data);
		MatrixStore<Double> svdU = svd.getU();
		MatrixStore<Double> svdS = svd.getD();
		logger.info("Done with Singular Value Decomposition");

		// Copy over to U, S, V
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				U[i][j] = svdU.get(i, j); // Must be negative
			}
		}
		for (int i = 0; i < columns; i++) {
			S[i] = svdS.get(i, i);
		}
	}
}
