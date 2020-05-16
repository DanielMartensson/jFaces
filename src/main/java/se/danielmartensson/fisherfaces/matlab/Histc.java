package se.danielmartensson.fisherfaces.matlab;

import org.apache.commons.math3.linear.RealMatrix;

public class Histc {

	static public int[] histc(RealMatrix A) {
		int maxValue = (int) Max.max(A) + 1;

		// Do histogram counting
		double[] arryNum = A.getRow(0);
		int[] counter = new int[maxValue];
		for (int i = 0; i < arryNum.length; i++) {
			counter[(int) arryNum[i]]++;
		}
		return counter;

	}
}
