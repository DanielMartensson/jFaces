package se.danielmartensson.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

public class PictureSize {

	static public int[] pictureSize(String folderPath) {
		// Read all the pictures from the sub folders
		File[] folders = new File(folderPath).listFiles();
		Arrays.sort(folders);

		// Return array
		int[] size = { -1, -1 }; // x, y

		// Count the amount of pictures;
		int countOfPictures = 0;
		for (File folder : folders) {
			if(folder.isDirectory() == false)
				continue;
			File[] pictures = folder.listFiles();
			for (File picture : pictures) {
				try {
					BufferedImage image = ImageIO.read(picture);
					int width = image.getWidth();
					int height = image.getHeight();
					if (countOfPictures == 0) {
						size[0] = height;
						size[1] = width;
					} else {
						if (width != size[1] && height != size[0]) {
							System.out.println("Not same size - Check picture: " + picture.getAbsolutePath());
						}
					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}
			}
		}
		return size;
	}
}
