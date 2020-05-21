package se.danielmartensson.fisherfaces.tools;

import java.util.Random;

import org.ojalgo.matrix.Primitive64Matrix;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.Primitive64Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.danielmartensson.fisherfaces.Fisherfaces;
import se.danielmartensson.fisherfaces.Model;

public class Prediction {

	static Logger logger = LoggerFactory.getLogger(Prediction.class);

	static public int predictionOnRandomSample(Model model, Primitive64Store X, Primitive64Store y, int k) {
		long columns = X.countColumns(); // Number of samples
		long rows = X.countRows();
		int randomIdx = new Random().nextInt((int) columns); // Get a random index
		return predictProcedure(model, X, y, k, columns, rows, randomIdx);
	}

	static public int predictionOnSample(Model model,Primitive64Store X, Primitive64Store y, int k, int randomIdx) {
		long columns = X.countColumns(); // Number of samples
		long rows = X.countRows();
		return predictProcedure(model, X, y, k, columns, rows, randomIdx);
	}

	static private int predictProcedure(Model model, Primitive64Store X, Primitive64Store y, int k, long columns, long rows, int randomIdx) {
		
		// Train model if we have no selected model
		if(model == null) {
			logger.info("Creating traning and test data with random test class number");
			
			// Create selected columns
			int[] selectedColumns = new int[(int) (columns - 1)];
			int counter = 0;
			for (int i = 0; i < columns; i++) {
				if (i != randomIdx) {
					selectedColumns[counter] = i;
					counter++;
				}
			}
			
			logger.info("Training model");
			MatrixStore<Double> XTrain = X.logical().columns(selectedColumns).get();
			MatrixStore<Double> yTrain = y.logical().columns(selectedColumns).get();
			Primitive64Matrix XTrainPrimitive = Primitive64Matrix.FACTORY.rows(XTrain.toRawCopy2D());
			Primitive64Matrix yTrainPrimitive = Primitive64Matrix.FACTORY.rows(yTrain.toRawCopy2D());
			model = Fisherfaces.train(XTrainPrimitive, yTrainPrimitive);
		}

		// Predict model
		MatrixStore<Double> XTest = X.logical().column(randomIdx).get(); // This is the unknown random picture
		MatrixStore<Double> yTest = y.logical().column(randomIdx).get(); // This our class number
		logger.info("Selected random test class number = " + yTest.get(0, 0));
		Primitive64Matrix XTestPrimitive = Primitive64Matrix.FACTORY.rows(XTest.toRawCopy2D());
		logger.info("Predicting test class number on trained model");
		int predictedNumber = Fisherfaces.predict(model, XTestPrimitive, k);
		logger.info("Model found that selected class number = " + predictedNumber + " as test class");
		return predictedNumber;

	}
}
