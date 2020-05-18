package se.danielmartensson.fisherfaces.matlab;

import org.ojalgo.function.aggregator.Aggregator;
import org.ojalgo.matrix.Primitive64Matrix;

public class Histc {

	// Hist counting - MATLAB/Octave command: histc
	static public int[] histc(Primitive64Matrix ysorted) {
		int maxValue = (int) (ysorted.aggregateAll(Aggregator.MAXIMUM) + 1);

		// Do histogram counting
		double[] arryNum = ysorted.toRawCopy1D();
		int[] counter = new int[maxValue];
		for (int i = 0; i < arryNum.length; i++) {
			counter[(int) arryNum[i]]++;
		}
		return counter;

	}
}
