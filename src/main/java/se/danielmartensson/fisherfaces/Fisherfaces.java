package se.danielmartensson.fisherfaces;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Fisherfaces {

	static Logger logger = LoggerFactory.getLogger(Fisherfaces.class);

	/**
	 * X [dim x num_data] input data y [1 x num_data] classes
	 * 
	 * @return
	 */
	static public Model train(RealMatrix X, RealMatrix y) {

		// Find columns
		int N = X.getColumnDimension();
		int c = (int) y.getEntry(0, y.getColumnDimension() - 1) + 1; // Last element is total classes
		int num_components = c - 1;

		// Build model
		logger.info("Building model with " + c + " classes");
		return buildModel(X, N, c, y, num_components);
	}

	/**
	 * X [dim x num_data] input data y [1 x num_data] classes num_components [int]
	 * components to keep
	 * 
	 * @return
	 */
	static public Model train(RealMatrix X, RealMatrix y, int num_components) {

		// Find columns
		int N = X.getColumnDimension();
		int c = (int) y.getEntry(0, y.getColumnDimension() - 1) + 1; // Last element is total classes

		// Check which one is smallest
		if (c - 1 < num_components)
			num_components = c - 1;

		// Build model
		logger.info("Building model with " + c + " classes");
		return buildModel(X, N, c, y, num_components);
	}

	private static Model buildModel(RealMatrix X, int N, int c, RealMatrix y, int num_components) {
		// Create PCA Model
		logger.info("Create PCA model");
		Model pca = Pca.pca(X, N - c);
		RealMatrix Wpca = pca.getW();
		RealMatrix mu = pca.getMu();

		// Create the projection
		RealMatrix Y = Project.project(X, Wpca, mu);

		// Create LDA model
		logger.info("Create LDA model");
		Model lda = Lda.lda(Y, y, num_components);
		RealMatrix Wlda = lda.getW();
		RealMatrix D = lda.getD();

		// Create fisher model
		logger.info("Create Fisherfaces model");
		Model fisher = new Model();
		fisher.setName("fisherfaces");
		fisher.setMu(MatrixUtils.createRealMatrix(X.getRowDimension(), 1)); // MATLAB: repmat(0, size(X,1), 1);
		fisher.setD(D);
		fisher.setW(Wpca.multiply(Wlda)); // MATLAB: W = Wpca*Wlda
		fisher.setP(fisher.getW().transpose().multiply(X)); // MATLAB: P = W'*X
		fisher.setNum_components(num_components);
		fisher.setY(y);
		return fisher;

	}

	static public int predict(Model model, RealMatrix XTest, int k) {
		RealMatrix Q = model.getW().transpose().multiply(XTest); // MATLAB: Q = W'*XText
		return Knn.knn(model.getP(), model.getY(), Q, k);
	}

}
