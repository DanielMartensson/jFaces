package se.danielmartensson.fisherfaces.matlab;

import org.apache.commons.math3.linear.RealMatrix;

public class Sqrt {

	static public RealMatrix sqrt(RealMatrix A) {
		RealMatrix B = A.copy();
		int columns = B.getColumnDimension();
		int rows = B.getRowDimension();
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				double value = B.getEntry(i, j);
				B.setEntry(i, j, Math.sqrt(value)); // MATLAB sqrt(distances)
			}
		}
		return B;
	}

}
