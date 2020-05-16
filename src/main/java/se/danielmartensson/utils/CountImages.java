package se.danielmartensson.utils;

import java.io.File;
import java.util.Arrays;

public class CountImages {

	static public int countImages(String folderPath) {
		// Read all the pictures from the sub folders
		File[] folders = new File(folderPath).listFiles();
		Arrays.sort(folders);

		// Count the amount of pictures;
		int countOfPictures = 0;
		for (File folder : folders) {
			if(folder.isDirectory() == false)
				continue;
			File[] pictures = folder.listFiles();
			countOfPictures += pictures.length;
		}
		return countOfPictures;
	}
}
