package se.danielmartensson;

import org.ojalgo.matrix.Primitive64Matrix;
import org.ojalgo.matrix.store.Primitive64Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.danielmartensson.fisherfaces.Fisherfaces;
import se.danielmartensson.fisherfaces.Model;
import se.danielmartensson.fisherfaces.Prediction;
import se.danielmartensson.fisherfaces.Validation;
import se.danielmartensson.utils.CountImages;
import se.danielmartensson.utils.PictureSize;
import se.danielmartensson.utils.ReadImages;

public class Main {

	static Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		String path = null;
		String method = null;
		int knearest = 0;
		int predictionSample = 0;
		boolean buildModel = false;
		
		// Run arguments
		for(int i = 0; i < args.length; i++) {
			String argument = args[i];
			switch (argument) {
			case "-f": // Folder path
				path = args[i+1];
				i++;
				break;
			case "-m": // Metod
				/*
				 * P1 = Prediction on random sample
				 * P2 = Prediction on sample
				 * V1 = Validation on subjects
				 * V2 = Validation on samples
				 */
				method = args[i+1]; 
				i++;
				if(method.toLowerCase().equals("p2")){
					predictionSample = Integer.parseInt(args[i+1]);
					i++;
				}
				break;
			case "-k":
				knearest = Integer.parseInt(args[i+1]);
				i++;
				break;
			case "-b":
				buildModel = true;
				break;
			case "--help":
				System.out.println("-f Path to the folder that contains subfolders with pictures");
				System.out.println("-m Method which prediction or validation to use");
				System.out.println("\tP1 Prediction on random sample");
				System.out.println("\tP2 = Prediction on sample");
				System.out.println("\tV1 = Validation on subjects");
				System.out.println("\tV2 = Validation on samples");
				System.out.println("-k K-nearest value");
			default:
				logger.info("Cannot understand what you are saying. Please use flag --help");
				return;
			}
		}
		
		logger.info("Collecting data");
		// Get data
		int countImages = CountImages.countImages(path);
		int[] size = PictureSize.pictureSize(path);
		Primitive64Store X = Primitive64Store.FACTORY.make(size[0] * size[1], countImages);
		Primitive64Store y = Primitive64Store.FACTORY.make(1, countImages);
		ReadImages.readImages(path, X, y);
		
		// Do the heavy stuff
		method = method.toLowerCase();
		if(method.equals("p1")) {
			Prediction.predictionOnRandomSample(X, y, knearest);
		}else if(method.equals("p2")) {
			Prediction.predictionOnSample(X, y, knearest, predictionSample);
		}else if(method.equals("v1")) {
			Validation.validateOnSubjects(X, y, knearest);
		}else if(method.equals("v2")) {
			Validation.validateOnSamples(X, y, knearest);
		}
		
		// Train model - TODO: Serialize(save) the object model - How can we use that later?
		if(buildModel) {
			Primitive64Matrix completeX = Primitive64Matrix.FACTORY.rows(X.toRawCopy2D());
			Primitive64Matrix completeY = Primitive64Matrix.FACTORY.rows(y.toRawCopy2D());
			@SuppressWarnings("unused")
			Model model = Fisherfaces.train(completeX, completeY);
		}
	}
}
