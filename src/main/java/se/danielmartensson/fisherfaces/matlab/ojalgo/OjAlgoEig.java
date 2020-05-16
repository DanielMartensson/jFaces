package se.danielmartensson.fisherfaces.matlab.ojalgo;

import org.ojalgo.matrix.Primitive64Matrix;
import org.ojalgo.matrix.decomposition.Eigenvalue;
import org.ojalgo.matrix.store.MatrixStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OjAlgoEig {

	static Logger logger = LoggerFactory.getLogger(OjAlgoEig.class);

	// A*V = B*D*V - Find D and V - Will not work for current OjAlgo version
	static public void eig(double A[][], double B[][], double D[], double V[][], int rows) {

		// Create eigA and eigB from ->random<- symmetrical A and B
		Primitive64Matrix eigA = Primitive64Matrix.FACTORY.rows(A);
		Primitive64Matrix eigB = Primitive64Matrix.FACTORY.rows(B);

		// Perform [A][V] = [B][V][D]
		Eigenvalue.Generalised<Double> eig = Eigenvalue.PRIMITIVE.makeGeneralised(eigA);
		boolean success = eig.decompose(eigA, eigB);
		if (success == false)
			logger.error("Could not perform eigenvalue decomposition!");
		MatrixStore<Double> Deig = eig.getD();
		MatrixStore<Double> Veig = eig.getV();

		// Copy over to D, V
		for (int i = 0; i < rows; i++) {
			D[i] = Deig.get(i, i);
			for (int j = 0; j < rows; j++) {
				V[i][j] = Veig.get(i, j);
			}
		}
	}
}
