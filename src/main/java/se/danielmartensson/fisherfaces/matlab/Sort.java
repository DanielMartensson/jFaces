package se.danielmartensson.fisherfaces.matlab;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.IntStream;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.linear.RealMatrix;

public class Sort {

	// Sort eigenvalues on descended order
	static public void sortevd(RealMatrix V, RealMatrix D) {
		Double[] eigenvalues = ArrayUtils.toObject(D.getColumn(0));

		// Sort the indexes on descended order
		Integer[] indexes = IntStream.range(0, eigenvalues.length).boxed().toArray(Integer[]::new);
		Arrays.sort(indexes, Comparator.<Integer>comparingDouble(i -> eigenvalues[i]).reversed());
		// Sort the eigenvalues
		Arrays.sort(eigenvalues, Collections.reverseOrder());
		D.setColumn(0, ArrayUtils.toPrimitive(eigenvalues));

		// Sort the eigenvectors
		int[] selectedColumns = new int[V.getColumnDimension()];
		for (int i = 0; i < V.getColumnDimension(); i++) {
			selectedColumns[i] = i;
		}
		RealMatrix B = V.getSubMatrix(ArrayUtils.toPrimitive(indexes), selectedColumns);
		for (int i = 0; i < B.getColumnDimension(); i++) {
			V.setColumn(i, B.getColumn(i));
		}
	}

	// Sort on normal order
	static public RealMatrix sortdistances(RealMatrix d, RealMatrix y, int k) {
		Double[] distances = ArrayUtils.toObject(d.getRow(0));
		// Sort the index
		Integer[] indexes = IntStream.range(0, distances.length).boxed().toArray(Integer[]::new);
		Arrays.sort(indexes, Comparator.<Integer>comparingDouble(i -> distances[i]));

		// Select the index
		int[] selectedRows = { 0 };
		RealMatrix B = y.getSubMatrix(selectedRows, ArrayUtils.toPrimitive(indexes));
		RealMatrix C = B.getSubMatrix(0, 0, 0, k - 1);
		return C;

	}

}
