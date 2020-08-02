package se.danielmartensson;

import org.ojalgo.matrix.Primitive64Matrix;
import org.ojalgo.matrix.store.Primitive64Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.danielmartensson.fisherfaces.Fisherfaces;
import se.danielmartensson.fisherfaces.Model;
import se.danielmartensson.fisherfaces.tools.GenerateC;
import se.danielmartensson.fisherfaces.tools.Prediction;
import se.danielmartensson.fisherfaces.tools.SaveLoad;
import se.danielmartensson.fisherfaces.tools.Validation;
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
		double ratio = 0;
		String modelPath = null;
		boolean loadModel = false;
		Model model = null;
		
		// Run arguments
		for(int i = 0; i < args.length; i++) {
			String argument = args[i].toLowerCase();
			switch (argument) {
			case "-f": // Folder path, for either a model or subfolders
				path = args[i+1];
				i++;
				break;
			case "-m": // Metod
				/*
				 * P1 = Prediction on random sample
				 * P2 = Prediction on sample
				 * V1 = Cross validation on subjects
				 * V2 = Cross validation on samples
				 * V3 = Validation on
				 */
				method = args[i+1].toLowerCase(); 
				i++;
				if(method.equals("p2")){
					predictionSample = Integer.parseInt(args[i+1]); // -P2 index
					i++;
				}else if(method.equals("v3")) {
					ratio = Double.parseDouble(args[i+1]); // -V3 ratio
					i++;
				}
				break;
			case "-k":
				knearest = Integer.parseInt(args[i+1]);
				i++;
				break;
			case "-b":
				buildModel = true;
				modelPath = args[i+1];
				i++;
				break;
			case "-l":
				loadModel = true;
				modelPath = args[i+1];
				i++;
				break;
			case "--help":
				System.out.println("jFaces - Image classifiction in Java. Made by Daniel Mårtensson");
				System.out.println("Call jFaces.jar with: 'java -jar jFaces.jar' followed by the commands below");
				System.out.println("Example for increasing the heap memory: 'java -Xmx1000M -jar jFaces.jar' for 1000 megabyte in heap memory");
				System.out.println("Command list:");
				System.out.println("\t-f Path to the folder that contains subfolders with pictures");
				System.out.println("\t-m Method which prediction or validation to use, followed by 'P1', 'P2', 'V1', 'V2' or 'V3'");
				System.out.println("\t\tP1 = Prediction on random sample");
				System.out.println("\t\tP2 = Prediction on selected sample, followed by sample number");
				System.out.println("\t\tV1 = Cross validation on classes");
				System.out.println("\t\tV2 = Cross validation on samples");
				System.out.println("\t\tV3 = Test and train data validation, followed by value ration between 0 < ratio < 1");
				System.out.println("\t-b Build model and save it, followed by path to the save location");
				System.out.println("\t-l Load model, followed by path to the model location");
				System.out.println("\t-k K-nearest value, followed by a positive integer k-value");
				System.out.println("Examples:");
				System.out.println("\tRandom prediction with k = 30: -f '/path/to/subfolers/with/pictures' -m P1 -k 30");
				System.out.println("\tSelected sample 10 at prediction with k = 30: -f '/path/to/subfolers/with/pictures' -m P2 10 -k 30");
				System.out.println("\tCross validation on classes with k = 20: -f '/path/to/subfolers/with/pictures' -m V1 -k 20");
				System.out.println("\tCross validation on samples with k = 20: -f '/path/to/subfolers/with/pictures' -m V2 -k 20");
				System.out.println("\tTest 30% and train 70% set validation with k = 10: -f '/path/to/subfolers/with/pictures' -m V3 0.7 -k 10");
				System.out.println("\tBuild model: -f '/path/to/subfolers/with/pictures' -b '/path/to/model/location.ser'");
				System.out.println("\tBuild model and do validation on samples: -f '/path/to/subfolers/with/pictures' -b '/path/to/model/location.ser' -m V2 -k 20");
				System.out.println("\tLoad model and do validation on classes: -f '/path/to/subfolers/with/pictures' -l '/path/to/model/location.ser' -m V1 -k 20");
			default:
				if(argument.equals("--help") == false)
					logger.info("Cannot understand what you are saying. Please use flag --help");
				return;
			}
		}
		
		if(path == null) {
			logger.info("No path to picture folders is selected. Forgot to use flag -f?");
			return;
		}
		
		logger.info("Collecting data by reading images");
		int countImages = CountImages.countImages(path);
		int[] size = PictureSize.pictureSize(path);
		Primitive64Store X = Primitive64Store.FACTORY.make(size[0] * size[1], countImages);
		Primitive64Store y = Primitive64Store.FACTORY.make(1, countImages);
		ReadImages.readImages(path, X, y);
		
		if(loadModel) {
			logger.info("Loading the model");
			model = SaveLoad.loadModel(modelPath);
		}
		
		if(buildModel) {
			logger.info("Training a model and save it");
			Primitive64Matrix completeX = Primitive64Matrix.FACTORY.rows(X.toRawCopy2D());
			Primitive64Matrix completeY = Primitive64Matrix.FACTORY.rows(y.toRawCopy2D());
			model = Fisherfaces.train(completeX, completeY);
			SaveLoad.saveModel(model, modelPath);
			GenerateC.generateC(model, modelPath, X);
		}
		
		// Do prediction or validation
		if(method != null) {
			method = method.toLowerCase();
			if(method.equals("p1")) {
				Prediction.predictionOnRandomSample(model, X, y, knearest);
			}else if(method.equals("p2")) {
				Prediction.predictionOnSample(model, X, y, knearest, predictionSample);
			}else if(method.equals("v1")) {
				Validation.validateOnClasses(model, X, y, knearest);
			}else if(method.equals("v2")) {
				Validation.validateOnSamples(model,X, y, knearest);
			}else if(method.equals("v3")) {
				Validation.validateOnTrainTest(model, X, y, knearest, ratio);
			}
		}
	}
}
