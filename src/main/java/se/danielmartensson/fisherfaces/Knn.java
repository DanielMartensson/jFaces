package se.danielmartensson.fisherfaces;

import org.ojalgo.function.aggregator.Aggregator;
import org.ojalgo.function.constant.PrimitiveMath;
import org.ojalgo.matrix.Primitive64Matrix;
import org.ojalgo.matrix.Primitive64Matrix.DenseReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.danielmartensson.fisherfaces.matlab.Histc;
import se.danielmartensson.fisherfaces.matlab.Repmat;
import se.danielmartensson.fisherfaces.matlab.Sort;


public class Knn {
	
	static Logger logger = LoggerFactory.getLogger(Knn.class);

	/** This is k-nearest neighbor 
	 *  double[][] p = {{0.5815037 , 0.2126437 , 0.2778553 , 0.9896006 , 0.7984958 , 0.7577199 , 0.0470290, 0.0615568, 0.3166059, 0.4474530},
	 *	 		   	    {0.8899696 , 0.9083204 , 0.8482830 , 0.7057446 , 0.6736160 , 0.7784272 , 0.6961607 , 0.4131566 , 0.0023143 , 0.2253922}, 
	 *	 		        {0.4088286 , 0.4405119 , 0.7431652 , 0.3027775 , 0.5088486 , 0.3344635 , 0.0390168 , 0.8200820 , 0.7779813 , 0.4115481}}; 
	 *  double[] y1 = {1 , 1 , 2 , 2 , 3 , 3 , 4, 4 , 5 , 5}; 
	 *  double[][] q = {{1},{1},{10}}; 
	 * 	
	 *  Primitive64Matrix P = Primitive64Matrix.FACTORY.rows(p);
	 *	Primitive64Matrix Q = Primitive64Matrix.FACTORY.rows(q);
	 *	Primitive64Matrix y = Primitive64Matrix.FACTORY.rows(y1);
	 * 
	 * knn(P, y, Q, k) = 4, for k = 1 
	 * knn(P, y, Q, k) = 2, for k = 2 
	 * knn(P, y, Q, k) = 2, for k = 3 
	 * knn(P, y, Q, k) = 2, for k = 4 
	 * knn(P, y, Q, k) = 1, for k = 5
	 * 
	 */
	static public int knn(Primitive64Matrix P, Primitive64Matrix y, Primitive64Matrix Q, int k) {
		long columns = P.countColumns();
		long rows = P.countRows();
		if (k > columns) {
			k = (int) (columns - 1);
			logger.info("K cannot be larger than " + columns + ". Setting k = " + k);
		}
		if(k < 1) {
			logger.info("Value k cannot be under 1. Setting k = 1");
			k = 1; // Cannot be 0
		}
		return procedure(columns, rows, P, Q, k, y);

	}

	private static int procedure(long columns, long rows, Primitive64Matrix P, Primitive64Matrix Q, int k, Primitive64Matrix y) {
		Q = Repmat.repmat(Q, 1, columns);
		Q = P.subtract(Q); // MATLAB: Q = P-Q
		DenseReceiver Qmutable = Q.copy();
		Qmutable.modifyAll(PrimitiveMath.POWER.parameter(2)); // MATLAB: Q = Q.^2
		Q = Qmutable.get();
		Q = Q.reduceColumns(Aggregator.SUM); // MATLAB: Q = sum(Q, 1)
		Qmutable = Q.copy();
		Qmutable.modifyAll(PrimitiveMath.ROOT.parameter(2)); // // MATLAB: Q = sqrt(Q)
		Q = Qmutable.get();	
		Primitive64Matrix ysorted = Sort.sortdistances(Q, y, k); // MATLAB: [~, index] = sort(Q), ysorted = y(index);
		int[] h = Histc.histc(ysorted); // MATLAB: h = hstic(ysorted, 1:max(ysorted))
		
		// Find max value's index of h
		int maxValue = h[0];
		int index = 0;
		for (int i = 1; i < h.length; i++) {
			if (maxValue < h[i]) {
				maxValue = h[i];
				index = i;
			}
		}
		return index;
	}
}
