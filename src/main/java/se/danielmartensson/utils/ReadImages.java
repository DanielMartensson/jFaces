package se.danielmartensson.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.ojalgo.matrix.store.Primitive64Store;
import org.ojalgo.structure.Access1D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadImages {

	static Logger logger = LoggerFactory.getLogger(ReadImages.class);

	static public void readImages(String folderPath, Primitive64Store X, Primitive64Store y) {
		logger.info("Reading images from " + folderPath);
		// Read all the pictures from the sub folders
		File[] folders = new File(folderPath).listFiles();
		Arrays.sort(folders);
		long countOfPictures = 0;
		int countOfContinue = 0;

		// Read the images now
		for (int i = 0; i < folders.length; i++) {
			if (folders[i].isDirectory() == false) {
				countOfContinue++;
				continue; // Prevent so regular files will be counted
			}
			File[] pictures = folders[i].listFiles();
			Arrays.sort(pictures);
			for (File picture : pictures) {
				logger.info("Reading picture " + picture.getAbsolutePath());

				// Get the data from images
				double[] data = imageData(picture);

				// Save data
				X.fillColumn(0, countOfPictures, Access1D.wrap(data));

				// Notice the subjects with y
				y.set(0, countOfPictures, i - countOfContinue); // We will always start at 0
				countOfPictures++;
				logger.info("Done...");

			}
		}
	}

	// This turns the image data to 0..255 and return the data
	private static double[] imageData(File picture) {
		try {
			BufferedImage image = ImageIO.read(picture);
			int width = image.getWidth();
			int height = image.getHeight();
			double[] data = new double[height * width];

			// Get 8-bit image
			int countRows = 0;
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					// Get pixel
					int gray = image.getRGB(x, y) & 0xFF; // Important with 0xFF to turn gray into 0..255 values
					data[countRows] = gray;
					countRows++;
				}
			}

			return data;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
}
