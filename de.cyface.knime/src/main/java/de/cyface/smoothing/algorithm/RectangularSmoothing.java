package de.cyface.smoothing.algorithm;

import java.util.Arrays;
import java.util.OptionalDouble;

public class RectangularSmoothing implements Algorithm {

	@Override
	public double smooth(double[] window) {
		OptionalDouble smoothedValue = Arrays.stream(window).average();
		return smoothedValue.getAsDouble();
	}

}
