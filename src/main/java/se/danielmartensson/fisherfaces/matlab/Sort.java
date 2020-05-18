package se.danielmartensson.fisherfaces.matlab;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.IntStream;

import org.apache.commons.lang3.ArrayUtils;
import org.ojalgo.matrix.Primitive64Matrix;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.Primitive64Store;
import org.ojalgo.structure.Access1D;

public class Sort {

	// Sort eigenvalues on descended order. Also V will be sorted as well on the same index as D
	static public void sortdescended(Primitive64Store V, Primitive64Store D, long dim) {
		// Load the eigenvalues and sort them
		Double[] eigenvalues = ArrayUtils.toObject(D.toRawCopy1D());
		Integer[] indexes = IntStream.range(0, eigenvalues.length).boxed().toArray(Integer[]::new);
		Arrays.sort(indexes, Comparator.<Integer>comparingDouble(i -> eigenvalues[i]).reversed());
		
		// Sort the eigenvalues
		Arrays.sort(eigenvalues, Collections.reverseOrder());
		D.fillRow(0, Access1D.wrap(eigenvalues));

		// Sort the eigenvectors as well
		MatrixStore<Double> B = V.logical().rows(ArrayUtils.toPrimitive(indexes)).get();
		V.accept(B);

	}

	// Sort on normal order - Not descended
	static public Primitive64Matrix sortdistances(Primitive64Matrix Q, Primitive64Matrix y, int k) {
		// Get the distances Q and sort its index
		Double[] distances = ArrayUtils.toObject(Q.toRawCopy1D());
		Integer[] indexes = IntStream.range(0, distances.length).boxed().toArray(Integer[]::new);
		Arrays.sort(indexes, Comparator.<Integer>comparingDouble(i -> distances[i]));

		// Select the index from our list of samples y
		double[] selectedColumns = new double[k];
		for(int i = 0; i < k; i++) {
			selectedColumns[i] = y.get(indexes[i]);
		}
		return Primitive64Matrix.FACTORY.columns(selectedColumns);

	}

}
