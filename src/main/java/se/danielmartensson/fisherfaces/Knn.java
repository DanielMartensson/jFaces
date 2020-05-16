package se.danielmartensson.fisherfaces;

import org.apache.commons.math3.linear.RealMatrix;

import se.danielmartensson.fisherfaces.matlab.Histc;
import se.danielmartensson.fisherfaces.matlab.Power;
import se.danielmartensson.fisherfaces.matlab.Repmat;
import se.danielmartensson.fisherfaces.matlab.Sort;
import se.danielmartensson.fisherfaces.matlab.Sqrt;
import se.danielmartensson.fisherfaces.matlab.Sum;

public class Knn {

	/**
	 * Try to run this double[][] p = {{0.5815037 , 0.2126437 , 0.2778553 ,
	 * 0.9896006 , 0.7984958 , 0.7577199 , 0.0470290, 0.0615568, 0.3166059,
	 * 0.4474530}, {0.8899696 , 0.9083204 , 0.8482830 , 0.7057446 , 0.6736160 ,
	 * 0.7784272 , 0.6961607 , 0.4131566 , 0.0023143 , 0.2253922}, {0.4088286 ,
	 * 0.4405119 , 0.7431652 , 0.3027775 , 0.5088486 , 0.3344635 , 0.0390168 ,
	 * 0.8200820 , 0.7779813 , 0.4115481}}; double[] y1 = {1 , 1 , 2 , 2 , 3 , 3 , 4
	 * , 4 , 5 , 5}; double[][] q = {{1},{1},{10}}; int k = 3; RealMatrix P =
	 * MatrixUtils.createRealMatrix(p); RealMatrix Y =
	 * MatrixUtils.createRowRealMatrix(y1); RealMatrix Q =
	 * MatrixUtils.createRealMatrix(q);
	 * 
	 * knn(P, y, Q, k) = 4, for k = 1 knn(P, y, Q, k) = 2, for k = 2 knn(P, y, Q, k)
	 * = 2, for k = 3 knn(P, y, Q, k) = 2, for k = 4 knn(P, y, Q, k) = 1, for k = 5
	 * 
	 */
	static public int knn(RealMatrix P, RealMatrix y, RealMatrix Q, int k) {
		int columns = P.getColumnDimension();
		int rows = P.getRowDimension();
		if (k > columns)
			k = columns - 1;
		return procedure(columns, rows, P, Q, k, y);

	}

	private static int procedure(int columns, int rows, RealMatrix P, RealMatrix Q, int k, RealMatrix y) {
		Q = Repmat.repmat(Q, 1, columns);
		RealMatrix PQ = P.subtract(Q); // MATLAB: P-Q
		RealMatrix PQPower = Power.power(PQ, 2); // MATLAB: PQ.^2
		RealMatrix distances = Sum.sum(PQPower, 1); // MATLAB: sum(PQPower, 1)
		RealMatrix distancesSqrt = Sqrt.sqrt(distances); // MATLAB: sqrt(distances)
		RealMatrix ysorted = Sort.sortdistances(distancesSqrt, y, k); // MATLAB: [~, ysorted] = sort(distancesSqrt)
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

	static public int knn(RealMatrix P, RealMatrix y, RealMatrix Q) {
		int columns = P.getColumnDimension();
		int rows = P.getRowDimension();
		int k = 0;
		return procedure(columns, rows, P, Q, k, y);
	}

}
