package se.danielmartensson.fisherfaces.matlab.octave;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import org.apache.commons.math3.linear.RealMatrix;

public class OctaveEig {

	private static final String EXIT = "exit"; // Octave will print out this command from OctaveEig.m
	private static final String POSITIVE_INFINITY_STRING = "Inf";
	private static final String NEGATIVE_INFINITY_STRING = "-Inf";

	// This is used with Octave 4.4.2
	static public void eig(RealMatrix A, RealMatrix B, RealMatrix V, RealMatrix D) {
		// Write A and B to Octave matrices
		int rows = A.getRowDimension();
		int columns = A.getColumnDimension();
		double[][] matA = A.getData();
		double[][] matB = B.getData();
		try {
			FileWriter input = new FileWriter("AB");
			addData(input, 'A', rows, columns, matA);
			addData(input, 'B', rows, columns, matB);
			input.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Call OctaveEig.m to solve A*V = B*V*D where A and B are symmetrical random
		// matrices
		try {
			String[] commands = { "octave-cli", "OctaveEig.m" };
			ProcessBuilder build = new ProcessBuilder(commands);
			Process process = build.start();
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String s = null;
			while ((s = stdInput.readLine()) != null)
				if (s.contains(EXIT)) // Wait for it...
					break;
			
			/*
			 *  function OctaveEig()
					% Load and perform A*V = B*V*D
					load AB;
					[V, D] = eig(A, B);
					
					% Sort eigenvalues on descended order and change the eigenvectors on the same order as well
					[D, idx] = sort(diag(D), 1, 'descend');
					V = V(:, idx);
					save('VD', 'V', 'D');
					disp('exit') % Exit command for the process builder in java
				end
			 */
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Read VD file
		try {
			BufferedReader output = new BufferedReader(new FileReader("VD"));
			String line;
			int VstartsAtLine = 5; // We start read V a specific row
			int VstopsAtLine = VstartsAtLine + rows - 1;
			int DstartsAtLine = VstopsAtLine + 7;
			int DstopsAtLine = DstartsAtLine + rows - 1;
			int lineCounter = 0;
			int eigenVectorCounter = 0;
			int eigenValuesCounter = 0;
			while ((line = output.readLine()) != null) {
				// Read V matrix - Eigenvectors
				if (lineCounter >= VstartsAtLine && lineCounter <= VstopsAtLine) {
					String[] lineRowString = line.split(" ");
					double[] lineRow = new double[rows];
					int lineRowCounter = 0;
					for (int i = 0; i < lineRowString.length; i++) {
						String value = lineRowString[i];
						if (value.length() > 0) {
							lineRow[lineRowCounter] = Double.parseDouble(value);
							lineRowCounter++;
						}
					}
					V.setRow(eigenVectorCounter, lineRow);
					eigenVectorCounter++;
				}
				// Read D matrix - Eigenvalues
				if (lineCounter >= DstartsAtLine && lineCounter <= DstopsAtLine) {
					String valueString = line.split(" ")[1]; // We have a space at element index 0
					double value = 0;
					if (valueString.equals(POSITIVE_INFINITY_STRING)) {
						value = Double.POSITIVE_INFINITY;
					} else if (valueString.equals(NEGATIVE_INFINITY_STRING)) {
						value = Double.NEGATIVE_INFINITY;
					} else {
						value = Double.valueOf(valueString);
					}
					D.setEntry(eigenValuesCounter, 0, value);
					eigenValuesCounter++;
				}
				lineCounter++;
			}
			output.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	static private void addData(FileWriter input, char matrixName, int rows, int columns, double[][] mat)
			throws IOException {
		input.append("# name: " + matrixName + "\n");
		input.append("# type: matrix\n");
		input.append("# rows: " + rows + "\n");
		input.append("# columns: " + columns + "\n");
		for (int i = 0; i < rows; i++) {
			String dataRow = Arrays.toString(mat[i]); // mat[i][] to string
			input.append(dataRow.replace(",", "").replace("[", "").replace("]", "") + "\n");
		}
		input.append("\n\n"); // Importat with two new empty rows
	}
}
