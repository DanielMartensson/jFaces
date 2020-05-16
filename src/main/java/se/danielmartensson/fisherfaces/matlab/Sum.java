package se.danielmartensson.fisherfaces.matlab;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

public class Sum {
	// MATLAB: sum(A, k), k = 1 or 2
	static public RealMatrix sum(RealMatrix A, int k) {
		return procedure(A, k);
	}

	// MATLAB: sum(A, 1)
	static public RealMatrix sum(RealMatrix A) {
		return procedure(A, 1);
	}

	private static RealMatrix procedure(RealMatrix A, int k) {
		int columns = A.getColumnDimension();
		int rows = A.getRowDimension();
		if (k == 1) {
			// Columns
			RealMatrix B = MatrixUtils.createRealMatrix(1, columns);
			for (int i = 0; i < columns; i++) {
				double value = 0;
				for (int j = 0; j < rows; j++) {
					value += A.getEntry(j, i);
				}
				B.setEntry(0, i, value);
			}
			return B;
		} else {
			// Rows
			RealMatrix B = MatrixUtils.createRealMatrix(rows, 1);
			for (int i = 0; i < rows; i++) {
				double value = 0;
				for (int j = 0; j < columns; j++) {
					value += A.getEntry(i, j);
				}
				B.setEntry(i, 0, value);
			}
			return B;
		}
	}
}
