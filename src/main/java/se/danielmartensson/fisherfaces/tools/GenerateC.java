package se.danielmartensson.fisherfaces.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.ojalgo.matrix.store.Primitive64Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.danielmartensson.fisherfaces.Model;

public class GenerateC {
	
	static Logger logger = LoggerFactory.getLogger(SaveLoad.class);

	static public void generateC(Model model, String modelPath, Primitive64Store columnPictures) {
			// Generate C code
			generateModel(model, modelPath);
			generatePictures(modelPath, columnPictures);
			generateLogic(model, modelPath);
			generateExample(model, modelPath);
	}
	
	private static void generateExample(Model model, String modelPath) {
		try {
			// Rename the path
			if(modelPath.contains(".")) 
				modelPath = modelPath.split("\\.")[0] + "_example_" + ".c"; 
			else 
				modelPath = modelPath + "_example_" + ".c";
			
			// Create the file
			File file = new File(modelPath);
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			
			// Get size
			long Wcolumns = model.getW().countColumns();
			
			// Create data now
			StringWriter data = new StringWriter();		
			data.write(getHeaderText(file.getName()));
									
			// Generate #include
			data.write("#include \"" + file.getName().replace("_example_.c", "_model_.h") + "\"\n");
			data.write("#include \"" + file.getName().replace("_example_.c", "_pictures_.h") + "\"\n");
			data.write("#include <stdio.h> \n");
			data.write("#include <stdint.h> \n");
			data.write("#include <time.h> \n\n");
			
			// Print the functions
			String functions = 
					"int main(){\n" + 
					"\n" + 
					"	// Declare the picture\n" + 
					"	uint8_t single_column_picture_of_WCOLUMNS_rows[Xrows];\n" + 
					"	uint16_t knearest_factor = 15; // Recommended for the YALE database\n" + 
					"	float sigma;\n" + 
					"	uint16_t ID;\n" + 
					"\n" + 
					"	// Loop all the pictures\n" + 
					"	for(uint16_t j = 0; j < Xcolumns; j++){\n" + 
					"\n" + 
					"		// Read the picture\n" + 
					"		printf(\"Reading picture %d, which has ID %d\\n\", j, Yinit[j]);\n" + 
					"		for(uint32_t k = 0; k < Xrows; k++){\n" + 
					"			single_column_picture_of_WCOLUMNS_rows[k] = Xmat[Xcolumns*k + j];\n" + 
					"		}\n" + 
					"\n" + 
					"		// Do the prediction and measure the time\n" + 
					"		clock_t start = clock();\n" + 
					"		predict(single_column_picture_of_WCOLUMNS_rows, knearest_factor, &sigma, &ID);\n" + 
					"		clock_t end = clock();\n" + 
					"		float seconds = (float)(end - start) / CLOCKS_PER_SEC;\n" + 
					"		printf(\"The picture has ID = %d and the identify standard deviation sigma = %f (low = better). Prediction: %f seconds. Success: \", ID,  sigma, seconds);\n" + 
					"		if(Yinit[j] == ID)\n" + 
					"			printf(\"true\\n\");\n" + 
					"		else\n" + 
					"			printf(\"false\\n\");\n" + 
					"\n" + 
					"	}\n" + 
					"\n" + 
					"    return 0;\n" + 
					"}";
			functions = functions.replaceAll("WCOLUMNS", String.valueOf(Wcolumns));
			data.write(functions);
			
			// Write C-code
			bw.write(data.toString());
			bw.close();
			logger.info("Model header generated at " + modelPath);
			
			/*
			int main(){

				// Declare the picture
				uint8_t single_column_picture_of_WCOLUMNS_rows[Xrows];
				uint16_t knearest_factor = 15; // Recommended for the YALE database
				float sigma;
				uint16_t ID;

				// Loop all the pictures
				for(uint16_t j = 0; j < Xcolumns; j++){

					// Read the picture
					printf("Reading picture %d, which has ID %d\n", j, Yinit[j]);
					for(uint32_t k = 0; k < Xrows; k++){
						single_column_picture_of_WCOLUMNS_rows[k] = Xmat[Xcolumns*k + j];
					}

					// Do the prediction and measure the time
					clock_t start = clock();
					predict(single_column_picture_of_WCOLUMNS_rows, knearest_factor, &sigma, &ID);
					clock_t end = clock();
					float seconds = (float)(end - start) / CLOCKS_PER_SEC;
					printf("The picture has ID = %d and the identify standard deviation sigma = %f (low = better). Prediction: %f seconds. Success: ", ID,  sigma, seconds);
					if(Yinit[j] == ID)
						printf("true\n");
					else
						printf("false\n");

				}

			    return 0;
			}*/
			
		}catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private static void generateLogic(Model model, String modelPath) {
		try {
			// Rename the path
			if(modelPath.contains(".")) 
				modelPath = modelPath.split("\\.")[0] + "_logic_" + ".c"; 
			else 
				modelPath = modelPath + "_logic_" + ".c";
			
			// Create the file
			File file = new File(modelPath);
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			
			// Get size
			long Wcolumns = model.getW().countColumns();
			
			// Create data now
			StringWriter data = new StringWriter();		
			data.write(getHeaderText(file.getName()));
						
			// Generate #include
			data.write("#include \"" + file.getName().replace("_logic_.c", "_model_.h") + "\"\n");
			data.write("#include <stdint.h> \n");
			data.write("#include <string.h> \n");
			data.write("#include <math.h> \n\n");
			
			// Generate empty arrays for using later with the function predict
			data.write("static float Qmat[Prows*Pcolumns];\n");
			data.write("static uint16_t Ymat[Yrows*Ycolumns];\n\n");
			
			// Print the functions
			String functions =
					"// Check the standard deviation of H - Large standard deviation is not likely recognizable. Small standard deviation is likely recognizable\n" + 
					"// MATLAB:\n" + 
					"// sigma = std(H);\n" + 
					"static void find_standard_deviation(uint16_t H[], float* sigma, uint16_t maxY, uint16_t* ID){\n" + 
					"	float sigma_square = 0;\n" + 
					"	uint16_t mean = H[*ID];\n" + 
					"	for(uint16_t i = 0; i < maxY; i++){\n" + 
					"		sigma_square += (H[i] - mean)*(H[i] - mean);\n" + 
					"	}\n" + 
					"	*sigma = sqrtf(sigma_square / maxY);\n" + 
					"}\n" + 
					"\n" + 
					"// Check the largest value of H and find its index\n" + 
					"// MATLAB:\n" + 
					"// [~, index] = max(H);\n" + 
					"static void find_max_index(uint16_t H[], uint16_t maxY, uint16_t* ID){\n" + 
					"	uint16_t maxH = H[0];\n" + 
					"	for(uint16_t i = 1; i < maxY; i++){\n" + 
					"		if(H[i] > maxH){\n" + 
					"			maxH = H[i];\n" + 
					"			*ID = i;\n" + 
					"		}\n" + 
					"	}\n" + 
					"}\n" + 
					"\n" + 
					"// Do histogram counting\n" + 
					"// MATLAB:\n" + 
					"// H = histc(Y, 1:maxY);\n" + 
					"static void histc(uint16_t H[], uint16_t Y[], uint16_t maxY, uint16_t knearest_factor){\n" + 
					"	memset(H, 0, maxY*sizeof(uint16_t));\n" + 
					"	for (uint16_t i = 0; i < knearest_factor; i++){\n" + 
					"		H[Y[i]]++; // The reason why we have knearest_factor included is because of Y = Y(1:knearest_factor) at below\n" + 
					"	}\n" + 
					"}\n" + 
					"\n" + 
					"// Find max value of Y with knearest_factor as index limit\n" + 
					"// MATLAB:\n" + 
					"// Y = Y(1:knearest_factor);\n" + 
					"// maxY = max(Y);\n" + 
					"static uint16_t find_max_value(uint16_t Y[], uint16_t knearest_factor){\n" + 
					"	uint16_t maxY = Y[0];\n" + 
					"	for(uint16_t i = 1; i < knearest_factor; i++){\n" + 
					"		if(Y[i] > maxY){\n" + 
					"			maxY = Y[i];\n" + 
					"		}\n" + 
					"	}\n" + 
					"	return maxY;\n" + 
					"}\n" + 
					"\n" + 
					"// This is inserted sort method for the first row of Q\n" + 
					"// MATLAB:\n" + 
					"// [~, ind] = sort(Q(1, :));\n" + 
					"// Y = Y(ind);\n" + 
					"static void sort_Y_depending_on_first_row_of_Q(float Q[], uint16_t Y[]) {\n" + 
					"	uint16_t i, j, keyY;\n" + 
					"	float keyQ;\n" + 
					"\n" + 
					"	for (i = 1; i < Pcolumns; i++) {\n" + 
					"		j = i;\n" + 
					"		keyQ = Q[i];\n" + 
					"		keyY = Y[i];\n" + 
					"		while (j > 0 && Q[j - 1] > keyQ) {\n" + 
					"			Q[j] = Q[j - 1];\n" + 
					"			Y[j] = Y[j - 1];\n" + 
					"			j--;\n" + 
					"		}\n" + 
					"		Q[j] = keyQ;\n" + 
					"		Y[j] = keyY;\n" + 
					"	}\n" + 
					"}\n" + 
					"\n" + 
					"// Repeat, substract, power and then sum\n" + 
					"// MATLAB:\n" + 
					"// Q = sum(power(P-repmat(Q, 1, size(P, 2)), 2), 1);\n" + 
					"static void repmat_substract_power_sum(float Q[], const float P[]){\n" + 
					"	// First row of Repeat -> Substract ->Ppower^2 -> Sum\n" + 
					"	float q = *Q; // First column and first row value\n" + 
					"	for(uint16_t j = 0; j < Pcolumns; j++){\n" + 
					"		*Q = *P - q; // Substract\n" + 
					"		*Q = *Q * *Q; // Power^2\n" + 
					"		Q++;\n" + 
					"		P++;\n" + 
					"	}\n" + 
					"\n" + 
					"	// The rest of the rows of Repeat -> Substract -> Power^2 -> Sum\n" + 
					"	for(uint16_t i = 1; i < Prows; i++){\n" + 
					"		q = *Q; // First column value at row i\n" + 
					"		for(uint16_t j = 0; j < Pcolumns; j++){\n" + 
					"			*Q = *P - q; // Substract\n" + 
					"			Q[-Pcolumns*i] += *Q * *Q; //  Power^2 -> Sum to the top row at column index j\n" + 
					"			Q++;\n" + 
					"			P++;\n" + 
					"		}\n" + 
					"	}\n" + 
					"}\n" + 
					"\n" + 
					"// Multiply vector with matrix\n" + 
					"// MATLAB:\n" + 
					"// Q = W*picture;\n" + 
					"static void matrix_vector_multiplication(const float W[], uint8_t picture[], float Q[]){\n" + 
					"	uint16_t Qindex = 0;\n" + 
					"	for(uint16_t i = 0; i < Wrows; i++){\n" + 
					"		Q[Qindex] = 0;\n" + 
					"		for(uint32_t j = 0; j < Wcolumns; j++){\n" + 
					"			Q[Qindex] += *W++ * picture[j];\n" + 
					"		}\n" + 
					"		Qindex += Pcolumns; // Notice that Q is a matrix, but we are using it as a vector\n" + 
					"	}\n" + 
					"}\n" + 
					"\n" + 
					"// Call this function with the picture array and k-factor\n" + 
					"void predict(uint8_t single_column_picture_of_WCOLUMNS_rows[], uint16_t knearest_factor, float* sigma, uint16_t* ID){\n" + 
					"\n" + 
					"	// Security check if we have slected a k-value that excedes Ycolumns\n" + 
					"	if(knearest_factor > Ycolumns){\n" + 
					"		knearest_factor = Ycolumns;\n" + 
					"	}\n" + 
					"\n" + 
					"	// Create Y - We are going to modify Ymat later\n" + 
					"	memcpy(Ymat, Yinit, sizeof(Yinit));\n" + 
					"\n" + 
					"	// Perform k-nearest neighbor now\n" + 
					"	matrix_vector_multiplication(W, single_column_picture_of_WCOLUMNS_rows, Qmat);\n" + 
					"	repmat_substract_power_sum(Qmat, P);\n" + 
					"	sort_Y_depending_on_first_row_of_Q(Qmat, Ymat);\n" + 
					"	uint16_t maxY = find_max_value(Ymat, knearest_factor) + 1;\n" + 
					"	uint16_t H[maxY];\n" + 
					"	histc(H, Ymat, maxY, knearest_factor);\n" + 
					"	find_max_index(H, maxY, ID); // The index of max value of H is the ID\n" + 
					"	find_standard_deviation(H, sigma, maxY, ID); // We use this to check if we have correct identify the picture or not\n" + 
					"\n" + 
					"}";
			functions = functions.replaceAll("WCOLUMNS", String.valueOf(Wcolumns));
			data.write(functions);
			
			// Write C-code
			bw.write(data.toString());
			bw.close();
			logger.info("Model header generated at " + modelPath);
			
			/*
			// Check the standard deviation of H - Large standard deviation is not likely recognizable. Small standard deviation is likely recognizable
			// MATLAB:
			// sigma = std(H);
			static void find_standard_deviation(uint16_t H[], float* sigma, uint16_t maxY, uint16_t* ID){
				float sigma_square = 0;
				uint16_t mean = H[*ID];
				for(uint16_t i = 0; i < maxY; i++){
					sigma_square += (H[i] - mean)*(H[i] - mean);
				}
				*sigma = sqrtf(sigma_square / maxY);
			}

			// Check the largest value of H and find its index
			// MATLAB:
			// [~, index] = max(H);
			static void find_max_index(uint16_t H[], uint16_t maxY, uint16_t* ID){
				uint16_t maxH = H[0];
				for(uint16_t i = 1; i < maxY; i++){
					if(H[i] > maxH){
						maxH = H[i];
						*ID = i;
					}
				}
			}

			// Do histogram counting
			// MATLAB:
			// H = histc(Y, 1:maxY);
			static void histc(uint16_t H[], uint16_t Y[], uint16_t maxY, uint16_t knearest_factor){
				memset(H, 0, maxY*sizeof(uint16_t));
				for (uint16_t i = 0; i < knearest_factor; i++){
					H[Y[i]]++; // The reason why we have knearest_factor included is because of Y = Y(1:knearest_factor) at below
				}
			}

			// Find max value of Y with knearest_factor as index limit
			// MATLAB:
			// Y = Y(1:knearest_factor);
			// maxY = max(Y);
			static uint16_t find_max_value(uint16_t Y[], uint16_t knearest_factor){
				uint16_t maxY = Y[0];
				for(uint16_t i = 1; i < knearest_factor; i++){
					if(Y[i] > maxY){
						maxY = Y[i];
					}
				}
				return maxY;
			}

			// This is inserted sort method for the first row of Q
			// MATLAB:
			// [~, ind] = sort(Q(1, :));
			// Y = Y(ind);
			static void sort_Y_depending_on_first_row_of_Q(float Q[], uint16_t Y[]) {
				uint16_t i, j, keyY;
				float keyQ;

				for (i = 1; i < Pcolumns; i++) {
					j = i;
					keyQ = Q[i];
					keyY = Y[i];
					while (j > 0 && Q[j - 1] > keyQ) {
						Q[j] = Q[j - 1];
						Y[j] = Y[j - 1];
						j--;
					}
					Q[j] = keyQ;
					Y[j] = keyY;
				}
			}

			// Repeat, substract, power and then sum
			// MATLAB:
			// Q = sum(power(P-repmat(Q, 1, size(P, 2)), 2), 1);
			static void repmat_substract_power_sum(float Q[], const float P[]){
				// First row of Repeat -> Substract ->Ppower^2 -> Sum
				float q = *Q; // First column and first row value
				for(uint16_t j = 0; j < Pcolumns; j++){
					*Q = *P - q; // Substract
					*Q = *Q * *Q; // Power^2
					Q++;
					P++;
				}

				// The rest of the rows of Repeat -> Substract -> Power^2 -> Sum
				for(uint16_t i = 1; i < Prows; i++){
					q = *Q; // First column value at row i
					for(uint16_t j = 0; j < Pcolumns; j++){
						*Q = *P - q; // Substract
						Q[-Pcolumns*i] += *Q * *Q; //  Power^2 -> Sum to the top row at column index j
						Q++;
						P++;
					}
				}
			}

			// Multiply vector with matrix
			// MATLAB:
			// Q = W*picture;
			static void matrix_vector_multiplication(const float W[], uint8_t picture[], float Q[]){
				uint16_t Qindex = 0;
				for(uint16_t i = 0; i < Wrows; i++){
					Q[Qindex] = 0;
					for(uint32_t j = 0; j < Wcolumns; j++){
						Q[Qindex] += *W++ * picture[j];
					}
					Qindex += Pcolumns; // Notice that Q is a matrix, but we are using it as a vector
				}
			}

			// Call this function with the picture array and k-factor
			void predict(uint8_t single_column_picture_of_WCOLUMNS_rows[], uint16_t knearest_factor, float* sigma, uint16_t* ID){

				// Security check if we have slected a k-value that excedes Ycolumns
				if(knearest_factor > Ycolumns){
					knearest_factor = Ycolumns;
				}

				// Create Y - We are going to modify Ymat later
				memcpy(Ymat, Yinit, sizeof(Yinit));

				// Perform k-nearest neighbor now
				matrix_vector_multiplication(W, single_column_picture_of_WCOLUMNS_rows, Qmat);
				repmat_substract_power_sum(Qmat, P);
				sort_Y_depending_on_first_row_of_Q(Qmat, Ymat);
				uint16_t maxY = find_max_value(Ymat, knearest_factor) + 1;
				uint16_t H[maxY];
				histc(H, Ymat, maxY, knearest_factor);
				find_max_index(H, maxY, ID); // The index of max value of H is the ID
				find_standard_deviation(H, sigma, maxY, ID); // We use this to check if we have correct identify the picture or not

			}*/
			
		}catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private static void generatePictures(String modelPath, Primitive64Store columnPictures) {
		try {
			// Rename the path
			if(modelPath.contains(".")) 
				modelPath = modelPath.split("\\.")[0] + "_pictures_" + ".h"; 
			else 
				modelPath = modelPath + "_pictures_" + ".h"; 
			
			// Create the file
			File file = new File(modelPath);
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			
			// Get sizes
			long Xcolumns = columnPictures.countColumns();
			long Xrows = columnPictures.countRows();
		
			// Create data now
			StringWriter data = new StringWriter();		
			data.write(getHeaderText(file.getName()));
			
			// Generate #include
			data.write("#include <stdint.h> \n\n");
			
			// Generate the #defines
			data.write("#define Xcolumns " + Xcolumns + "\n");
			data.write("#define Xrows " + Xrows + "\n");
						
			// Write the array matrix
			createArrayMatrix(columnPictures.toRawCopy2D(), Xcolumns, Xrows, "uint8_t Xmat[Xrows*Xcolumns]", data, true);
			
			// Write headerDataCode
			bw.write(data.toString());
			bw.close();
			logger.info("Data pictures generated at " + modelPath);
		}catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private static void generateModel(Model model, String modelPath) {
		try {
			
			// Transpose on W to get faster computational rate for the C code
			model.setW(model.getW().transpose());
			
			// Rename the path
			if(modelPath.contains(".")) 
				modelPath = modelPath.split("\\.")[0] + "_model_" + ".h"; 
			else 
				modelPath = modelPath + "_model_" + ".h";  // "no file extension name e.g .ser is used
			
			// Create the file
			File file = new File(modelPath);
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			
			// Get the sizes
			long Pcolumns = model.getP().countColumns();
			long Prows = model.getP().countRows();
			long Wcolumns = model.getW().countColumns();
			long Wrows = model.getW().countRows();
			long Ycolumns = model.getY().countColumns();
			long Yrows = model.getY().countRows();
			
			// Create data now
			StringWriter data = new StringWriter();
			data.write(getHeaderText(file.getName()));
			
			// Generate #include
			data.write("#include <stdint.h> \n\n");
			
			// Generate the #defines
			data.write("#define Pcolumns " + Pcolumns + "\n");
			data.write("#define Prows " + Prows + "\n");
			data.write("#define Wcolumns " + Wcolumns + "\n");
			data.write("#define Wrows " + Wrows + "\n");
			data.write("#define Ycolumns " + Ycolumns + "\n");
			data.write("#define Yrows " + Yrows + "\n");
			
			// Write the arrays as static const
			createArrayMatrix(model.getP().toRawCopy2D(), Pcolumns, Prows, "float P[Prows*Pcolumns]", data, false);
			createArrayMatrix(model.getW().toRawCopy2D(), Wcolumns, Wrows, "float W[Wrows*Wcolumns]", data, false);
			createArrayMatrix(model.getY().toRawCopy2D(), Ycolumns, Yrows, "uint16_t Yinit[Yrows*Ycolumns]", data, true);
			
			
			// Write code
			data.write("\nvoid predict(uint8_t* single_column_picture_of_" + Wcolumns + "_rows" + ", uint16_t knearest_factor, float* sigma, uint16_t* ID);");
			bw.write(data.toString());
			bw.close();
			logger.info("Model header generated at " + modelPath);
			
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String getHeaderText(String fileName) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
		LocalDateTime now = LocalDateTime.now();  
		String date = "/*\n" + 
				" ============================================================================\n" + 
				" Model        		: " + fileName + "\n" + 
				" Generated by    	: jFaces\n" + 
				" Version     		: 1.0\n" + 
				" Copyright   		: MIT\n" + 
				" Date generated 	: " + dtf.format(now) + "\n" + 
				" ============================================================================\n" + 
				" */\n\n";
		return date;
	}

	private static <T> void createArrayMatrix(double[][] matrix, long columns, long rows, String array, StringWriter ccode, boolean integersOnly) {
		ccode.write("\n");
		String dataType = "static const " + array + " = {";
		ccode.write(dataType);
		for(int i = 0; i < rows; i++) {
			StringWriter line = new StringWriter();
			if(i != 0) {
				line.write("\t\t\t\t\t\t\t\t\t\t\t"); // So the array looks more nicer
			}
			
			for(int j = 0; j < columns; j++) {
				String value;
				if(integersOnly) {
					value = String.valueOf((int) matrix[i][j]);
				}else {
					value = String.valueOf((float) matrix[i][j]);
				}
				
				if(i == rows -1 && j == columns -1)
					line.write(value + "};"); // Last row and last column
				else
					if(j == columns -1)
						line.write(value + ",\n"); // Last column, but not last row
					else
						line.write(value + ","); // Not last column and not last row
			}
			
			ccode.write(line.toString());
		}
		
		ccode.write("\n");
		
	}

}
