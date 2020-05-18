package se.danielmartensson;

import org.ojalgo.matrix.store.Primitive64Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.danielmartensson.fisherfaces.Validation;
import se.danielmartensson.utils.CountImages;
import se.danielmartensson.utils.PictureSize;
import se.danielmartensson.utils.ReadImages;

public class Main {

	static Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		logger.info("Collecting data. Got out of memory in Java heap space? Add -Xmx6g to VM arguments.");
		// Get data
		String path = "/home/dell/FisherFaces-Examples/pictures/Yale Database sample";
		int countImages = CountImages.countImages(path);
		int[] size = PictureSize.pictureSize(path);
		Primitive64Store X = Primitive64Store.FACTORY.make(size[0] * size[1], countImages);
		Primitive64Store y = Primitive64Store.FACTORY.make(1, countImages);
		ReadImages.readImages(path, X, y);

		//Prediction.predictionOnRandomSample(X, y, 4); // Random class with k = 4
		//Prediction.predictionOnSample(X, y, 10, 90); // k = 10 at test sample 19
		Validation.validateOnSubjects(X, y, 10);
	}

}
