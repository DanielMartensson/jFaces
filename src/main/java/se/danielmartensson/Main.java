package se.danielmartensson;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.danielmartensson.fisherfaces.Prediction;
import se.danielmartensson.utils.CountImages;
import se.danielmartensson.utils.PictureSize;
import se.danielmartensson.utils.ReadImages;

public class Main {

	static Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		logger.info("Collecting data. Got out of memory in Java heap space? Add -Xmx6g to VM arguments.");
		// Get data
		String path = "/home/dell/FisherFaces-Examples/pictures/FEI Face Database";
		int countImages = CountImages.countImages(path);
		int[] size = PictureSize.pictureSize(path);
		RealMatrix X = MatrixUtils.createRealMatrix(size[0] * size[1], countImages);
		RealMatrix y = MatrixUtils.createRealMatrix(1, countImages);
		ReadImages.readImages(path, X, y);

		Prediction.predictionRandom(X, y, 4); // Random class with k = 4
		// Prediction.predictionSelected(X, y, 10, 100); // k = 10 at test sample 19

	}

}
