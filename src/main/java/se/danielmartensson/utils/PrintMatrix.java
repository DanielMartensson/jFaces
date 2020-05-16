package se.danielmartensson.utils;

import org.apache.commons.math3.linear.RealMatrix;

public class PrintMatrix {

	static public void printMatrix(RealMatrix A) {
		int rows = A.getRowDimension();
		int columns = A.getColumnDimension();
		System.out.println("Rows = " + rows + " columns = " + columns);
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				double value = A.getEntry(i, j);
				System.out.print("\t" + value);
			}
			System.out.println("");
		}
	}
}
