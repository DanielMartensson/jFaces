package se.danielmartensson.fisherfaces;

import org.ojalgo.matrix.Primitive64Matrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Fisherfaces {

	static Logger logger = LoggerFactory.getLogger(Fisherfaces.class);

	/**
	 * X [dim x num_data] input data 
	 * y [1 x num_data] classes
	 * 
	 * @return
	 */
	static public Model train(Primitive64Matrix X, Primitive64Matrix y) {

		// Find columns
		long N = X.countColumns();
		long c = (long) (y.get(0, N - 1) + 1); // Last element is total classes - It's the maximum
		long num_components = c - 1;

		// Build model
		logger.info("Building model with " + c + " classes");
		return buildModel(X, N, c, y, num_components);
	}

	/**
	 * X [dim x num_data] input data 
	 * y [1 x num_data] classes 
	 * num_components [int] components to keep
	 * 
	 * @return
	 */
	static public Model train(Primitive64Matrix X, Primitive64Matrix y, long num_components) {

		// Find columns
		long N = X.countColumns();
		long c = (long) (y.get(0, N - 1) + 1); // Last element is total classes - It's the maximum

		// Check which one is smallest
		if (c - 1 < num_components)
			num_components = c - 1;

		// Build model
		logger.info("Building model with " + c + " classes");
		return buildModel(X, N, c, y, num_components);
	}

	private static Model buildModel(Primitive64Matrix X, long N, long c, Primitive64Matrix y, long num_components) {
		// Create PCA Model
		logger.info("Create PCA model");
		Model pca = Pca.pca(X, N - c);
		Primitive64Matrix Wpca = pca.getW();
		Primitive64Matrix mu = pca.getMu();

		// Create the projection
		Primitive64Matrix projection = Project.project(X, Wpca, mu);

		// Create LDA model
		logger.info("Create LDA model");
		Model lda = Lda.lda(projection, y, num_components);
		Primitive64Matrix Wlda = lda.getW();
		//MatrixStore<Double> D = lda.getD();

		// Create fisher model
		logger.info("Create Fisherfaces model");
		Model fisher = new Model();
		fisher.setName("fisherfaces");
		//fisher.setMu(MatrixUtils.createRealMatrix(X.getRowDimension(), 1)); // MATLAB: repmat(0, size(X,1), 1);
		//fisher.setD(D);
		fisher.setW(Wpca.multiply(Wlda)); // MATLAB: W = Wpca*Wlda
		fisher.setP(fisher.getW().transpose().multiply(X)); // MATLAB: P = W'*X
		//fisher.setNum_components(num_components);
		fisher.setY(y);
		return fisher;

	}

	// Call this function with a fisher faces model and a picture sample of a column vector and k-nearest vaalue
	static public int predict(Model model, Primitive64Matrix pictureX, int k) {
		Primitive64Matrix Q = model.getW().transpose().multiply(pictureX); // MATLAB: Q = W'*pictureX
		return Knn.knn(model.getP(), model.getY(), Q, k);
	}

}
