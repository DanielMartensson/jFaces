package se.danielmartensson.fisherfaces;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.util.MathArrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.danielmartensson.fisherfaces.matlab.Mean;
import se.danielmartensson.fisherfaces.matlab.Repmat;
import se.danielmartensson.fisherfaces.matlab.ojalgo.Svd;

public class Pca {

	static Logger logger = LoggerFactory.getLogger(Pca.class);

	/**
	 * X [dim x num_data] input data y [1 x num_data] classes
	 */
	static public Model pca(RealMatrix X, int num_components) {
		return createCenterData(X, num_components);
	}

	/**
	 * X [dim x num_data] input data
	 */
	static public Model pca(RealMatrix X) {
		int num_components = X.getColumnDimension();
		return createCenterData(X, num_components); // Create data

	}

	static public Model createCenterData(RealMatrix X, int num_components) {
		RealMatrix mu = Mean.mean(X, 2); // MATLAB: mu = mean(X, 2)
		X = X.subtract(Repmat.repmat(mu, 1, X.getColumnDimension())); // MATLAB: X = X - repmat(mu, 1 size(X, 2))
		return svdOnCenterData(X, num_components, mu);
	}

	private static Model svdOnCenterData(RealMatrix X, int num_components, RealMatrix mu) {
		logger.info("Perform Singular Value Decomposition with OjAlgo's routine - This can take time");
		// Here we use OjAlgo's SVD - Much faster
		int rows = X.getRowDimension();
		int columns = X.getColumnDimension();
		double[][] Adata = X.getData();
		double[][] Udata = new double[rows][columns];
		double[] Sdata = new double[columns];
		Svd.svd(Adata, Udata, Sdata, rows, columns);

		// Build PCA model
		Model PCAModel = new Model();
		PCAModel.setName("pca");
		// Perform element multiplication
		RealMatrix D = MatrixUtils.createColumnRealMatrix(MathArrays.ebeMultiply(Sdata, Sdata)); // MATLAB: D = S.^2 =
																									// S.*S
		PCAModel.setD(D.getSubMatrix(0, num_components - 1, 0, 0)); // MATLAB: D(1:num_components)
		RealMatrix U = MatrixUtils.createRealMatrix(Udata);
		PCAModel.setW(U.getSubMatrix(0, U.getRowDimension() - 1, 0, num_components - 1)); // MATLAB: U(:,
																							// 1:num_components)
		PCAModel.setNum_components(num_components - 1);
		PCAModel.setMu(mu);
		return PCAModel;

	}

}
