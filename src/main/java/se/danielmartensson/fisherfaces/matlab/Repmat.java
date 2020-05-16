package se.danielmartensson.fisherfaces.matlab;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

public class Repmat {

	static public RealMatrix repmat(RealMatrix A, int m, int n) {
		int rows = A.getRowDimension();
		int columns = A.getColumnDimension();
		double[][] data = A.getData();
		RealMatrix B = MatrixUtils.createRealMatrix(rows * m, columns * n);
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				B.setSubMatrix(data, i * rows, j * columns);
			}
		}
		return B;
	}
}
