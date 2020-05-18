package se.danielmartensson.fisherfaces;

import org.ojalgo.matrix.Primitive64Matrix;
import org.ojalgo.matrix.store.Primitive64Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.danielmartensson.fisherfaces.matlab.IndexSearch;

public class Validation {
	
	static Logger logger = LoggerFactory.getLogger(Validation.class);
	
	// Test each sample - This takes long time
	static public double validateOnSamples(Primitive64Store X, Primitive64Store y, int k) {
		double points = 0.0;
		int amoutOfSamples = (int) y.countColumns();
		for(int i = 0; i < amoutOfSamples; i++) {
			int subjectID = y.get(i).intValue();
			int predictedID = Prediction.predictionOnSample(X, y, k, i);
			if(subjectID == predictedID) {
				points++;
			}
		}
		logger.info("The result of the validation on samples was = " + points/amoutOfSamples);
		return points/amoutOfSamples;
	}
	
	// Test each subject - A quick test
	static public double validateOnSubjects(Primitive64Store X, Primitive64Store y, int k) {
		Primitive64Matrix Y = Primitive64Matrix.FACTORY.rows(y.toRawCopy1D());
		Primitive64Matrix indexes = IndexSearch.indexSearch(Y);
		double points = 0.0;
		int amoutOfSubjects = (int) indexes.countColumns();
		for(int i = 0; i < amoutOfSubjects; i++) {
			int sampleIndex = indexes.get(i).intValue();
			int subjectID = y.get(sampleIndex).intValue();
			int predictedID = Prediction.predictionOnSample(X, y, k, sampleIndex);
			if(subjectID == predictedID) {
				points++;
			}
		}
		logger.info("The result of the validation on subjects was = " + points/amoutOfSubjects);
		return points/amoutOfSubjects;
	}

}
