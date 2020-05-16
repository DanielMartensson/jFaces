package se.danielmartensson.fisherfaces.matlab;

import org.apache.commons.math3.linear.RealMatrix;

public class Max {

	static public double max(RealMatrix A) {
		// Find max value
		int columns = A.getColumnDimension();
		double maxValue = A.getEntry(0, 0);
		for (int i = 1; i < columns; i++) {
			double newValue = A.getEntry(0, i);
			if (maxValue < newValue) {
				maxValue = newValue;
			}
		}
		return maxValue;
	}
}
