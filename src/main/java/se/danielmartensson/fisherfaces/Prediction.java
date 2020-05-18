package se.danielmartensson.fisherfaces;

import java.util.Random;

import org.ojalgo.matrix.Primitive64Matrix;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.Primitive64Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Prediction {

	static Logger logger = LoggerFactory.getLogger(Prediction.class);

	static public int predictionOnRandomSample(Primitive64Store X, Primitive64Store y, int k) {
		long columns = X.countColumns(); // Number of samples
		long rows = X.countRows();
		int randomIdx = new Random().nextInt((int) columns); // Get a random index
		return predictProcedure(X, y, k, columns, rows, randomIdx);
	}

	static public int predictionOnSample(Primitive64Store X, Primitive64Store y, int k, int randomIdx) {
		long columns = X.countColumns(); // Number of samples
		long rows = X.countRows();
		return predictProcedure(X, y, k, columns, rows, randomIdx);
	}

	static private int predictProcedure(Primitive64Store X, Primitive64Store y, int k, long columns, long rows, int randomIdx) {
		logger.info("Creating traning and test data with random test class number");

		// Split into training set
		long[] selectedColumns = new long[(int) (columns - 1)];
		int counter = 0;
		for (int i = 0; i < columns; i++) {
			if (i != randomIdx) {
				selectedColumns[counter] = i;
				counter++;
			}
		}
		
		// Create train data
		MatrixStore<Double> XTrain = X.logical().columns(selectedColumns).get();
		MatrixStore<Double> yTrain = y.logical().columns(selectedColumns).get();

		// Get test sample
		MatrixStore<Double> XTest = X.logical().column(randomIdx).get(); // This is the unknown random picture
		MatrixStore<Double> yTest = y.logical().column(randomIdx).get();
		logger.info("Selected random test class number = " + yTest.get(0, 0));
		
		// Turn them into Primitive64Matrix for faster use.
		// Except for yTest - It's only a index number of which class we are trying to predict
		Primitive64Matrix XTrainPrimitive = Primitive64Matrix.FACTORY.rows(XTrain.toRawCopy2D());
		Primitive64Matrix yTrainPrimitive = Primitive64Matrix.FACTORY.rows(yTrain.toRawCopy2D());
		Primitive64Matrix XTestPrimitive = Primitive64Matrix.FACTORY.rows(XTest.toRawCopy2D());

		// Train model
		logger.info("Training model");
		Model model = Fisherfaces.train(XTrainPrimitive, yTrainPrimitive);

		// Predict model
		logger.info("Predicting test class number on trained model");
		int predictedNumber = Fisherfaces.predict(model, XTestPrimitive, k);
		logger.info("Model found that selected class number = " + predictedNumber + " as test class");
		return predictedNumber;

	}
}
