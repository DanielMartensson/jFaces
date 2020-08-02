package se.danielmartensson.fisherfaces.matlab;

import org.ojalgo.matrix.Primitive64Matrix;

public class Repmat {

	static public Primitive64Matrix repmat(Primitive64Matrix X, int m, long n) {
	    return X.logical().repeat(m, (int) n).get();
	}
}
