package se.danielmartensson.fisherfaces.matlab.ojalgoroutines;

import org.ojalgo.matrix.Primitive64Matrix;
import org.ojalgo.matrix.decomposition.Eigenvalue;
import org.ojalgo.matrix.decomposition.Eigenvalue.Generalisation;
import org.ojalgo.matrix.store.Primitive64Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.danielmartensson.fisherfaces.matlab.Sort;

public class Eig {

	static Logger logger = LoggerFactory.getLogger(Eig.class);

	// [A][V] = [B][V][D] - Find D and V
	static public void eig(Primitive64Matrix A, Primitive64Matrix B, Primitive64Store D, Primitive64Store V, long dim) {

		// Solve generalized eigenvalue problem with regularization on B matrix if it's needed
		Eigenvalue.Generalised<Double> eig = Eigenvalue.PRIMITIVE.makeGeneralised(A, Generalisation.A_B);
		boolean success = false;
		double lambda = 0.01;
		while(success == false) {
			success = eig.decompose(A, B);
			if(success == false) {
				logger.info("Could not solve the generalized eigenvalue problem!");
				logger.info("Adding regularization onto B with lambda = " + lambda);
				Primitive64Matrix I = Primitive64Matrix.FACTORY.makeIdentity((int) dim);
				I = I.multiply(lambda); // MATLAB = I = I*lambda
				B = B.add(I); // B = B + I;
				lambda = lambda + 0.01;
			}else {
				logger.info("Solved the generalized eigenvalue problem!");
			}
		}
		
		// Copy over to V and sort if not ordered
		V.accept(eig.getV());
		if(eig.isOrdered() == false) {
			logger.info("Eigenvectors are not sorted - Sorting them in a descended order with respect on the eigenvalues");
			D.accept(eig.getD());
			Sort.sortdescended(V, D, dim); 
		}
	}
}
