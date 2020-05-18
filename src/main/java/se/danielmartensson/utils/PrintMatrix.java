package se.danielmartensson.utils;

import org.ojalgo.matrix.Primitive64Matrix;

public class PrintMatrix {

	static public void printMatrix(Primitive64Matrix A) {
		long rows = A.countRows();
		long columns = A.countColumns();
		System.out.println("Rows = " + rows + " columns = " + columns);
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				double value = A.get(i, j);
				System.out.print("\t" + value);
			}
			System.out.println("");
		}
	}
}
