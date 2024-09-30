import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;

public class Polynomial {
	double[] coefficients;
	int[] exponents;
	
	public Polynomial() {
		this.coefficients = new double[0];
		this.exponents = new int[0];
	}

	public Polynomial(File file) {
		if (file == null) {
			System.out.println("File is null.");
			this.coefficients = new double[0];
			this.exponents = new int[0];
			return;
		} 

		try {
			BufferedReader input = new BufferedReader(new FileReader(file));
			String line = input.readLine();
	
			if (line != null) {
				String[] equationSegments = line.split("[-+]");
				int length = equationSegments.length;
				double[] coefficients = new double[length];
				int[] exponents = new int[length];
	
				for (int i = 0; i < length; i++) {
					if (equationSegments[i].isEmpty())
						continue;

					String[] term = equationSegments[i].split("x");
					int posNegIndex = line.indexOf(equationSegments[i]) - 1;
					char posNegSymbol;

					if (posNegIndex >= 0)
						posNegSymbol = line.charAt(posNegIndex);
					else
						posNegSymbol = '+';

					if (equationSegments[i].charAt(equationSegments[i].length() - 1) == 'x')
						exponents[i] = 1;
					else if (term.length == 1)
						exponents[i] = 0;
					else {
						exponents[i] = Integer.parseInt(term[1]);
					}

					if (posNegSymbol == '+')
							coefficients[i] = Double.parseDouble(term[0]);
					else
						coefficients[i] = (-1) * Double.parseDouble(term[0]);
				}
				this.coefficients = coefficients;
				this.exponents = exponents;
				
				input.close();
			} else {
				this.coefficients = new double[0];
				this.exponents = new int[0];
			}
		} catch (Exception e) {
			System.out.println(e);

			this.coefficients = new double[0];
			this.exponents = new int[0];
		}
	}
	
	public Polynomial(double[] coefficients, int[] exponents) {
		this.coefficients = coefficients;
		this.exponents = exponents;
	}
	
	public Polynomial add(Polynomial polynomial) {
		if (polynomial == null)
			return this;
		if (polynomial.coefficients.length == 0 && this.coefficients.length == 0)
			return new Polynomial();
		if (polynomial.coefficients.length == 0)
			return this;
		if (this.coefficients.length == 0) {
			polynomial.correctPolynomialVariables();
			return polynomial;
		}

		double[] coefficients1 = this.coefficients;
		double[] coefficients2 = polynomial.coefficients;

		int[] exponents1 = this.exponents;
		int[] exponents2 = polynomial.exponents;

		int largestExponent = Math.max(findLargestExponent(exponents1),
		 									findLargestExponent(exponents2));

		double[] coefficientsTracker = new double[largestExponent + 1];
		
		for (int i = 0; i < coefficients1.length; i++)
			coefficientsTracker[exponents1[i]] += coefficients1[i];
		for (int i = 0; i < coefficients2.length; i++)
			coefficientsTracker[exponents2[i]] += coefficients2[i];

		Polynomial resultingPolynomial = new Polynomial(coefficientsTracker, null);

		resultingPolynomial.correctPolynomialVariables();

		return resultingPolynomial;
	}

	public Polynomial multiply(Polynomial polynomial) {
		if (polynomial == null)
			return new Polynomial();

		double[] coefficients1 = this.coefficients;
		double[] coefficients2 = polynomial.coefficients;

		int[] exponents1 = this.exponents;
		int[] exponents2 = polynomial.exponents;

		int largestExponent = findLargestExponent(exponents1) + findLargestExponent(exponents2);

		Polynomial intermidiatePolynomial = new Polynomial();
		int[] intermediateExponents = new int[largestExponent + 1];
		double[] intermediateCoefficients = new double[largestExponent + 1];

		for (int i = 0; i < coefficients1.length; i++) {
			for (int j = 0; j < coefficients2.length; j++) {
				int exponent = exponents1[i] + exponents2[j];
				double coefficient = coefficients1[i] * coefficients2[j];

				intermediateExponents[exponent] = exponent;
				intermediateCoefficients[exponent] += coefficient;
			}

			intermidiatePolynomial = intermidiatePolynomial.add(new Polynomial(intermediateCoefficients, intermediateExponents));
	
			intermediateExponents = new int[largestExponent + 1];
			intermediateCoefficients = new double[largestExponent + 1];
		}
		return intermidiatePolynomial;
	}
	
	public double evaluate(double value) {
		double result = 0;
		for (int i = 0; i < coefficients.length; i++) {
			result += coefficients[i] * Math.pow(value, exponents[i]);
		}
		return result;
	}
	
	public boolean hasRoot(double value) {
		return evaluate(value) == 0;
	}

	public void saveToFile(String filename) throws Exception {
		BufferedWriter writer = new BufferedWriter(new FileWriter(filename+".txt"));

		StringBuilder text = new StringBuilder();

		for (int i = 0; i < exponents.length; i++) {
			if (coefficients[i] < 0) {
				if (exponents[i] == 0)
					text.append(coefficients[i]);
				else if (exponents[i] == 1)
					text.append(coefficients[i]).append("x");
				else
					text.append(coefficients[i]).append("x").append(exponents[i]);
			} else if (coefficients[i] > 0) {
				if (i != 0) {
					if (exponents[i] == 0)
						text.append("+").append(coefficients[i]);
					else if (exponents[i] == 1)
						text.append("+").append(coefficients[i]).append("x");
					else
						text.append("+").append(coefficients[i]).append("x").append(exponents[i]);
				}
				else {
					if (exponents[i] == 0)
						text.append(coefficients[i]);
					else if (exponents[i] == 1)
						text.append(coefficients[i]).append("x");
					else
						text.append(coefficients[i]).append("x").append(exponents[i]);
				}
			}
		}
		writer.write(text.toString());
		writer.close();
	}

	@Override
	public String toString() {
		return "Coefficients: " + Arrays.toString(coefficients) + ", "
			+ "Exponents: " + Arrays.toString(exponents);
	}

	// Helper Function
	private void correctPolynomialVariables() {
		int resultingPolyLength = countNonZeroCoefficients(coefficients);
			
		double[] updatedCoefficients = new double[resultingPolyLength];
		int[] updatedExponents = new int[resultingPolyLength];

		int p = 0;

		for (int k = 0; k < coefficients.length; k++) {
			if (coefficients[k] != 0) {
				updatedCoefficients[p] = coefficients[k];
				updatedExponents[p] = k;
				p++;
			} 
		}
		coefficients = updatedCoefficients;
		exponents = updatedExponents;
	}

	// Helper Function
	private int countNonZeroCoefficients(double[] coefficients) {
		int count = 0;
		for (double coefficient : coefficients) {
			if (coefficient != 0) {
				count++;
			}
		}
		return count;
	}

	// Helper Function
	private int findLargestExponent(int[] exponents) {
		if (exponents == null)
			return 0;
		
		int largestExponent = exponents[0];

		for (int exponent : exponents)
			largestExponent = Math.max(exponent, largestExponent);
		return largestExponent;
	}
}