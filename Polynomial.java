public class Polynomial {
	
	double coefficients[];
	
	public Polynomial() {
		coefficients = new double[0];
	}
	
	public Polynomial(double coefficients[]) {
		this.coefficients = coefficients;
	}
	
	public Polynomial add(Polynomial polynomial) {
		double coefficients1[] = this.coefficients;
		double coefficients2[] = polynomial.coefficients;
		
		int length1 = coefficients1.length;
		int length2 = coefficients2.length;
		
		int resultingPolyLength = Math.max(length1, length2);
		double resultingCoefficients[] = new double[resultingPolyLength];
		
		int i = 0;
		for (; i < length1 && i < length2; i++)
			resultingCoefficients[i] = coefficients1[i] + coefficients2[i];
		
		for (; i < length1; i++)
			resultingCoefficients[i] = coefficients1[i];
		
		for (; i < length2; i++)
			resultingCoefficients[i] = coefficients2[i];
	
		return new Polynomial(resultingCoefficients);
	}
	
	public double evaluate(double value) {
		double result = 0;
		for (int i = 0; i < coefficients.length; i++) {
			result += coefficients[i] * Math.pow(value, i);
		}
		return result;
	}
	
	public boolean hasRoot(double value) {
		return evaluate(value) == 0;
	}
}