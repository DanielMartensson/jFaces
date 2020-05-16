package se.danielmartensson.fisherfaces.matlab;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

public class IndexSearch {

	static public RealMatrix indexSearch(RealMatrix A) {
		int columns = A.getColumnDimension();
		List<Integer> index = new ArrayList<Integer>();
		int lastValue = 0;
		for (int i = 0; i < columns; i++) {
			if (i == 0) {
				index.add(0); // We always start at 0
			} else {
				int newValue = (int) A.getEntry(0, i);
				if (lastValue != newValue) {
					index.add(i);
					lastValue = newValue;
				}
			}
		}

		RealMatrix B = MatrixUtils.createRealMatrix(1, index.size());
		for (int i = 0; i < index.size(); i++) {
			B.setEntry(0, i, index.get(i));
		}
		return B;
	}

}
