package se.danielmartensson.fisherfaces.tools;

import java.util.ArrayList;
import java.util.List;

import org.ojalgo.function.aggregator.Aggregator;
import org.ojalgo.matrix.Primitive64Matrix;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.Primitive64Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.danielmartensson.fisherfaces.Fisherfaces;
import se.danielmartensson.fisherfaces.Model;
import se.danielmartensson.fisherfaces.matlab.IndexSearch;

public class Validation {
	
	static Logger logger = LoggerFactory.getLogger(Validation.class);
	
	// Test each sample - This takes long time
	static public double validateOnSamples(Model model, Primitive64Store X, Primitive64Store y, int k) {
		double points = 0.0;
		int amoutOfSamples = (int) y.countColumns();
		for(int i = 0; i < amoutOfSamples; i++) {
			int classID = y.get(i).intValue();
			int predictedID = Prediction.predictionOnSample(model, X, y, k, i);
			if(classID == predictedID) {
				points++;
			}
		}
		logger.info("The result of the validation on samples was = " + points/amoutOfSamples);
		return points/amoutOfSamples;
	}
	
	// Test each class - A quick test
	static public double validateOnClasses(Model model, Primitive64Store X, Primitive64Store y, int k) {
		Primitive64Matrix Y = Primitive64Matrix.FACTORY.rows(y.toRawCopy1D());
		Primitive64Matrix indexes = IndexSearch.indexSearch(Y);
		double points = 0.0;
		int amoutOfSubjects = (int) indexes.countColumns();
		for(int i = 0; i < amoutOfSubjects; i++) {
			int sampleIndex = indexes.get(i).intValue();
			int classID = y.get(sampleIndex).intValue();
			int predictedID = Prediction.predictionOnSample(model, X, y, k, sampleIndex);
			if(classID == predictedID) {
				points++;
			}
		}
		logger.info("The result of the validation on classes was = " + points/amoutOfSubjects);
		return points/amoutOfSubjects;
	}
	
	// Train a model of a part of the data, then use the rest of the data as validation, depending on the ratio
	static public double validateOnTrainTest(Model model, Primitive64Store X, Primitive64Store y, int k, double ratio) {
		// Check if we made a correct ratio
		if(ratio <= 0 || ratio >= 1) {
			logger.info("You have selected a ration that are " + ratio);
			return -1.0;
		}
		
		// Collect
		logger.info("Creating data samples for training and testning");
		Primitive64Matrix Y = Primitive64Matrix.FACTORY.rows(y.toRawCopy1D());
		Primitive64Matrix indices = IndexSearch.indexSearch(Y);
		int c = y.aggregateRow(0, Aggregator.MAXIMUM).intValue() + 1; // Last element is total classes - It's the maximum
		List<Integer> trainingIndices = new ArrayList<Integer>();
		List<Integer> testIndices = new ArrayList<Integer>();
		for(int i = 0; i < c; i++) {
			// Find the start and stop index
			int start = indices.get(0, i).intValue();
			int stop = 0;
			if (i == c - 1) {
				stop = (int) (y.countColumns() - 1); // Last index of y
			} else {
				stop = indices.get(0, i + 1).intValue() - 1;
			}
			
			int diff = (int) Math.round((stop - start)*ratio); // e.g stop = 30, start = 20 is 11 elements i difference. 
			for(int j = start; j < diff+start; j++)
				trainingIndices.add(j);
			for(int j = diff+start; j <= stop; j++)
				testIndices.add(j);
			
		}
		
		int[] selectedTrainColumns = trainingIndices.stream().mapToInt(i->i).toArray();
		int[] selectedTestColumns = testIndices.stream().mapToInt(i->i).toArray();
		logger.info("Creating " + selectedTrainColumns.length + " for training data and " + selectedTestColumns.length + " for test data");
		
		// Create train data
		MatrixStore<Double> XTrain = X.logical().columns(selectedTrainColumns).get();
		MatrixStore<Double> yTrain = y.logical().columns(selectedTrainColumns).get();
		Primitive64Matrix XTrainPrimitive = Primitive64Matrix.FACTORY.rows(XTrain.toRawCopy2D());
		Primitive64Matrix yTrainPrimitive = Primitive64Matrix.FACTORY.rows(yTrain.toRawCopy2D());
		
		if(model == null) {
			logger.info("Training model");
			model = Fisherfaces.train(XTrainPrimitive, yTrainPrimitive);
			logger.info("Model trained - Time to test it with the test data set");
		}
		
		double points = 0.0;
		for(int i = 0; i < selectedTestColumns.length; i++) {
			int classID = y.get(selectedTestColumns[i]).intValue();
			logger.info("Test " + i + " of " +  (selectedTestColumns.length-1) + ": Predicting class number = " + classID);
			MatrixStore<Double> XTest = X.logical().column(selectedTestColumns[i]).get();
			Primitive64Matrix XTestPrimitive = Primitive64Matrix.FACTORY.rows(XTest.toRawCopy2D());
			int predictedID = Fisherfaces.predict(model, XTestPrimitive, k);
			logger.info("Model found that selected class number = " + predictedID + " as test class");
			if(classID == predictedID) {
				points++;
			}
		}
		logger.info("The result of the validation on test data set was = " + points/selectedTestColumns.length);
		return points/selectedTestColumns.length;
		
	}

}
