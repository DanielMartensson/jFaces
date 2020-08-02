package se.danielmartensson.fisherfaces.tools;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.ojalgo.matrix.Primitive64Matrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.danielmartensson.fisherfaces.Model;

public class SaveLoad {

	static Logger logger = LoggerFactory.getLogger(SaveLoad.class);

	static public void saveModel(Model model, String modelPath) {
		try {
			// Copy over to the template
			ModelTemplate modelTemplate = new ModelTemplate();
			modelTemplate.setP(model.getP().toRawCopy2D());
			modelTemplate.setW(model.getW().toRawCopy2D());
			modelTemplate.setY(model.getY().toRawCopy2D());			
			
			// Save
			FileOutputStream fileOut = new FileOutputStream(modelPath);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(modelTemplate);
			out.close();
			fileOut.close();
			logger.info("Model saved at " + modelPath);
						
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static public Model loadModel(String modelPath) {
		try {
			// Load
			FileInputStream fileIn = new FileInputStream(modelPath);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			ModelTemplate modelTemplate = (ModelTemplate) in.readObject();
			in.close();
			fileIn.close();
			logger.info("Model loaded from " + modelPath);
			
			// Copy over to model
			Model model = new Model();
			model.setW(Primitive64Matrix.FACTORY.rows(modelTemplate.getW()));
			model.setP(Primitive64Matrix.FACTORY.rows(modelTemplate.getP()));
			model.setY(Primitive64Matrix.FACTORY.rows(modelTemplate.getY()));
			return model;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		return null;
	}
}
