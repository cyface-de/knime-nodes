package de.cyface.smoothing.algorithm;

public final class TriangularSmoothing implements Algorithm {

	@Override
	public double smooth(double[] window) {
		double smoothedValue = 0.0;
		double denominator = 0.0;
		for(int i=0; i < window.length; i++) {
			double weight = (i-(window.length/2)>0) ? Integer.valueOf(window.length-i).doubleValue() : Integer.valueOf(i+1).doubleValue();
			denominator += weight;
			smoothedValue += weight * window[i];
		}
		return smoothedValue/denominator;
	}

}
