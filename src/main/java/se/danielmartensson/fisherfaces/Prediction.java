package se.danielmartensson.fisherfaces;

import java.util.Random;

import org.apache.commons.math3.linear.RealMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Prediction {

	static Logger logger = LoggerFactory.getLogger(Prediction.class);

	static public int predictionRandom(RealMatrix X, RealMatrix y, int k) {
		int columns = X.getColumnDimension(); // Number of samples
		int rows = X.getRowDimension();
		int randomIdx = new Random().nextInt(columns); // Get a random index
		return predictProcedure(X, y, k, columns, rows, randomIdx);
	}

	static public int predictionSelected(RealMatrix X, RealMatrix y, int k, int randomIdx) {
		int columns = X.getColumnDimension(); // Number of samples
		int rows = X.getRowDimension();
		return predictProcedure(X, y, k, columns, rows, randomIdx);
	}

	static private int predictProcedure(RealMatrix X, RealMatrix y, int k, int columns, int rows, int randomIdx) {
		logger.info("Creating traning and test data with random test class number");

		// Split into training set
		int[] selectedXRows = new int[rows];
		for (int i = 0; i < rows; i++) {
			selectedXRows[i] = i;
		}
		int[] selectedColumns = new int[columns - 1];
		int counter = 0;
		for (int i = 0; i < columns; i++) {
			if (i != randomIdx) {
				selectedColumns[counter] = i;
				counter++;
			}
		}
		int[] selectedyRows = { 0 }; // Only one row matrix

		RealMatrix XTrain = X.getSubMatrix(selectedXRows, selectedColumns);
		RealMatrix yTrain = y.getSubMatrix(selectedyRows, selectedColumns);

		// Get test sample
		RealMatrix XTest = X.getColumnMatrix(randomIdx);
		RealMatrix yTest = y.getColumnMatrix(randomIdx);
		logger.info("Selected random test class number = " + yTest.getEntry(0, 0));

		// Train model
		logger.info("Training model");
		Model model = Fisherfaces.train(XTrain, yTrain);

		// Predict model
		logger.info("Predicting test class number on trained model");
		int predictedNumber = Fisherfaces.predict(model, XTest, k);
		logger.info("Model found that selected class number = " + predictedNumber + " as test class");
		return predictedNumber;

	}
}
