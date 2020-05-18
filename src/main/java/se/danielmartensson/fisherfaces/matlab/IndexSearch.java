package se.danielmartensson.fisherfaces.matlab;

import java.util.ArrayList;
import java.util.List;

import org.ojalgo.matrix.Primitive64Matrix;

public class IndexSearch {

	/* This will return back indexes of rows vector A
	 * Example:
	 * If A = [0 0 0 1 1 1 1 1 1 2 2 2 2 2 3 3 3 4 4 5 5 5] ( These are the index of the test samples
	 * Then return back will be [0 3 9 14 17 19]
	 * Where 0, 3, 9, 1,4 17 and 19 representing start index for 0, 1, 2, 3, 4 and 5
	 */
	static public Primitive64Matrix indexSearch(Primitive64Matrix A) {
		long columns = A.countColumns();
		List<Integer> index = new ArrayList<Integer>();
		int lastValue = 0;
		for (int i = 0; i < columns; i++) {
			if (i == 0) {
				index.add(0); // We always start at 0
			} else {
				int newValue = A.get(0, i).intValue();
				if (lastValue != newValue) {
					index.add(i);
					lastValue = newValue;
				}
			}
		}
		
		// Use stream to convert List<Integer> to double[] and insert to a Primitive matrix
		return Primitive64Matrix.FACTORY.rows(index.stream().mapToDouble(i->i).toArray());
	}

}
