package se.danielmartensson.fisherfaces;

import org.ojalgo.function.aggregator.Aggregator;
import org.ojalgo.matrix.Primitive64Matrix;
import org.ojalgo.matrix.store.Primitive64Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.danielmartensson.fisherfaces.matlab.Repmat;
import se.danielmartensson.fisherfaces.matlab.ojalgoroutines.Svd;

public class Pca {

	static Logger logger = LoggerFactory.getLogger(Pca.class);

	/**
	 * X [dim x num_data] input data
	 * y [1 x num_data] classes
	 */
	static public Model pca(Primitive64Matrix X, long num_components) {
		return createCenterData(X, num_components);
	}

	/**
	 * X [dim x num_data] input data
	 */
	static public Model pca(Primitive64Matrix X) {
		long num_components = X.countColumns();
		return createCenterData(X, num_components); // Create data

	}

	static public Model createCenterData(Primitive64Matrix X, long num_components) {
		Primitive64Matrix mu = X.reduceRows(Aggregator.AVERAGE); // MATLAB: mu = mean(X, 2)
		X = X.subtract(Repmat.repmat(mu, 1, X.countColumns())); // MATLAB: X = X - repmat(mu, 1 size(X, 2))
		return svdOnCenterData(X, num_components, mu);
	}

	private static Model svdOnCenterData(Primitive64Matrix X, long num_components, Primitive64Matrix mu) {
		logger.info("Perform Singular Value Decomposition with OjAlgo's routine - This can take time");
		long rows = X.countRows();
		long columns = X.countColumns();		
		Primitive64Store U = Primitive64Store.FACTORY.make(rows, columns);
		Svd.svd(X, U, rows, columns);

		// Build PCA model
		Model PCAModel = new Model();
		PCAModel.setName("pca");
		// Perform element multiplication
		Primitive64Matrix primitiveU = Primitive64Matrix.FACTORY.rows(U.logical().limits(-1, num_components).get().toRawCopy2D()); // MATLAB: U(:, 1:num_components)
		PCAModel.setW(primitiveU); 
		PCAModel.setMu(mu);
		return PCAModel;

	}

}
