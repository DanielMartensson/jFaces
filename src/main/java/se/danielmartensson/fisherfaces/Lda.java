package se.danielmartensson.fisherfaces;

import java.util.stream.IntStream;

import org.ojalgo.function.aggregator.Aggregator;
import org.ojalgo.matrix.Primitive64Matrix;
import org.ojalgo.matrix.store.Primitive64Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.danielmartensson.fisherfaces.matlab.IndexSearch;
import se.danielmartensson.fisherfaces.matlab.Repmat;
import se.danielmartensson.fisherfaces.matlab.ojalgoroutines.Eig;

public class Lda {

	static Logger logger = LoggerFactory.getLogger(Lda.class);

	/**
	 * X [dim x num_data] input data 
	 * y [1 x num_data] classes
	 * 
	 * @return
	 */
	static public Model lda(Primitive64Matrix X, Primitive64Matrix yTrainPrimitive) {

		// Find dimension
		long dim = X.countRows();
		int c =  (int) (yTrainPrimitive.get(0, yTrainPrimitive.countColumns() - 1) + 1); // Last element is total classes - Maximum
		int num_components = c - 1;

		// create scatters
		return scatters(X, yTrainPrimitive, dim, c, num_components);

	}

	/**
	 * X [dim x num_data] input data 
	 * y [1 x num_data] classes num_components [int]
	 * components to keep
	 * 
	 * @return
	 */
	static public Model lda(Primitive64Matrix X, Primitive64Matrix yTrainPrimitive, long num_components) {

		// Find dimension
		long dim = X.countRows();
		int c =  (int) (yTrainPrimitive.get(0, yTrainPrimitive.countColumns() - 1) + 1); // Last element is total classes - Maximum

		// Check which one is smallest
		if (c - 1 < num_components)
			num_components = c - 1;

		// create scatters
		return scatters(X, yTrainPrimitive, dim, c, num_components);
	}

	private static Model scatters(Primitive64Matrix X, Primitive64Matrix yTrainPrimitive, long dim, int c, long num_components) {
		Primitive64Matrix meanTotal = X.reduceRows(Aggregator.AVERAGE); // MATLAB: mean(X, 2)
		Primitive64Matrix Sw = Primitive64Matrix.FACTORY.make(dim, dim);
		Primitive64Matrix Sb = Primitive64Matrix.FACTORY.make(dim, dim);

		// Create index search
		Primitive64Matrix indices = IndexSearch.indexSearch(yTrainPrimitive);
		for (int i = 0; i < c; i++) {

			// Find the start and stop index for Xi matrix
			int start = indices.get(0, i).intValue();
			int stop = 0;
			if (i == c - 1) {
				stop = (int) (yTrainPrimitive.countColumns() - 1); // Last index of yTrainPrimitive
			} else {
				stop = indices.get(0, i + 1).intValue() - 1;
			}
			
			Primitive64Matrix Xi = X.logical().columns(IntStream.rangeClosed(start, stop).toArray()).get(); // MATLAB: Xi = X(: find(y==i))
			Primitive64Matrix meanClass = Xi.reduceRows(Aggregator.AVERAGE); // MATLAB: meanClass = mean(Xi, 2)
			// Center data
			long Xicolumns = Xi.countColumns();
			Xi = Xi.subtract(Repmat.repmat(meanClass, 1, Xicolumns)); // MATLAB: X = X - repmat(mu, 1, size(Xi, 2))
			// Calculate within-class scatter
			Sw = Sw.add(Xi.multiply(Xi.transpose())); // MATLAB: Sw = Sw + Xi*Xi'

			// Calculate between-class scatter
			Primitive64Matrix difference = meanClass.subtract(meanTotal); // MATLAB: difference = meanClass - meanTotal
			Primitive64Matrix trans = difference.multiply(difference.transpose()); // MATLAB: trans = difference*difference'
			Sb = Sb.add(trans.multiply(Xicolumns)); // MATLAB: Sb = Sb + trans*size(Xi, 2)
		}
						
		// Here we use OjAlgo's EIG - Much faster than other Java libraries
		logger.info("Perform eigendecomposition with OjAlgo's routine");
		Primitive64Store D = Primitive64Store.FACTORY.make(dim, 1); 
		Primitive64Store V = Primitive64Store.FACTORY.make(dim, dim);
		Eig.eig(Sb, Sw, D, V, dim);

		// Build model
		Model LDAModel = new Model();
		LDAModel.setName("lda");
		LDAModel.setNum_components(num_components - 1);
		Primitive64Matrix primitiveV = Primitive64Matrix.FACTORY.rows(V.logical().limits(-1, num_components - 1).get().toRawCopy2D()); // MATLAB: V(:, 1:num_components)
		LDAModel.setW(primitiveV); 
		return LDAModel;
	}
}
