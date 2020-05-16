package se.danielmartensson.fisherfaces;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.danielmartensson.fisherfaces.matlab.IndexSearch;
import se.danielmartensson.fisherfaces.matlab.Mean;
import se.danielmartensson.fisherfaces.matlab.Repmat;
import se.danielmartensson.fisherfaces.matlab.octave.OctaveEig;

public class Lda {

	static Logger logger = LoggerFactory.getLogger(Lda.class);

	/**
	 * X [dim x num_data] input data y [1 x num_data] classes
	 * 
	 * @return
	 */
	static public Model lda(RealMatrix X, RealMatrix y) {

		// Find dimension
		int dim = X.getRowDimension();
		int c = (int) y.getEntry(0, y.getColumnDimension() - 1) + 1; // Last element is total classes
		int num_components = c - 1;

		// create scatters
		return scatters(X, y, dim, c, num_components);

	}

	/**
	 * X [dim x num_data] input data y [1 x num_data] classes num_components [int]
	 * components to keep
	 * 
	 * @return
	 */
	static public Model lda(RealMatrix X, RealMatrix y, int num_components) {

		// Find dimension
		int dim = X.getRowDimension();
		int c = (int) y.getEntry(0, y.getColumnDimension() - 1) + 1; // Last element is total classes

		// Check which one is smallest
		if (c - 1 < num_components)
			num_components = c - 1;

		// create scatters
		return scatters(X, y, dim, c, num_components);
	}

	private static Model scatters(RealMatrix X, RealMatrix y, int dim, int c, int num_components) {
		RealMatrix meanTotal = Mean.mean(X, 2); // MATLAB: mean(X, 2)
		RealMatrix Sw = MatrixUtils.createRealMatrix(dim, dim);
		RealMatrix Sb = MatrixUtils.createRealMatrix(dim, dim);

		// Create index search
		RealMatrix indices = IndexSearch.indexSearch(y);
		for (int i = 0; i < c; i++) {

			// Find the start and stop index for Xi matrix
			int start = (int) indices.getEntry(0, i);
			int stop = 0;
			if (i == c - 1) {
				stop = y.getColumnDimension() - 1;
			} else {
				stop = (int) indices.getEntry(0, i + 1) - 1;
			}

			RealMatrix Xi = X.getSubMatrix(0, dim - 1, start, stop);
			RealMatrix meanClass = Mean.mean(Xi, 2); // MATLAB: mu = mean(Xi, 2)
			// Center data
			Xi = Xi.subtract(Repmat.repmat(meanClass, 1, Xi.getColumnDimension())); // MATLAB: X = X - repmat(mu, 1,
																					// size(Xi, 2))
			// Calculate within-class scatter
			Sw = Sw.add(Xi.multiply(Xi.transpose())); // MATLAB: Sw = Sw + Xi*Xi'

			// Calculate between-class scatter
			RealMatrix difference = meanClass.subtract(meanTotal); // MATLAB: difference = meanClass - meanTotal
			RealMatrix trans = difference.multiply(difference.transpose()); // MATLAB: trans = difference*difference'
			Sb = Sb.add(trans.scalarMultiply(Xi.getColumnDimension())); // MATLAB: Sb = Sb + trans*size(Xi, 2)
		}

		// Here we use OjAlgo's EIG - Much faster than other Java libraries
		// logger.info("Perform eigendecomposition with OjAlgo's routine");
		// double[][] Adata = Sb.getData();
		// double[][] Bdata = Sw.getData();
		// double[] Ddata = new double[dim];
		// double[][] Vdata = new double[dim][dim];
		// Eig.eig(Adata, Bdata, Ddata, Vdata, dim);

		logger.info("Perform eigendecomposition with GNU Octave's routine");
		RealMatrix V = MatrixUtils.createRealMatrix(dim, dim);
		RealMatrix D = MatrixUtils.createRealMatrix(dim, 1);
		OctaveEig.eig(Sb, Sw, V, D);

		// Sort eigenvalues and eigenvectors descending by eigenvalue - Need only when we are using OjAlgo's routine
		// RealMatrix D = MatrixUtils.createColumnRealMatrix(Ddata);
		// RealMatrix V = MatrixUtils.createRealMatrix(Vdata);
		// Sort.sortevd(V, D); // TODO: Behöver vi denna?

		// Build model
		Model LDAModel = new Model();
		LDAModel.setName("lda");
		LDAModel.setNum_components(num_components - 1);
		LDAModel.setD(D.getSubMatrix(0, num_components - 1, 0, 0)); // MATLAB: D(1:num_components)
		LDAModel.setW(V.getSubMatrix(0, V.getRowDimension() - 1, 0, num_components - 1)); // MATLAB: V(:,
																							// 1:num_components)
		return LDAModel;
	}
}
