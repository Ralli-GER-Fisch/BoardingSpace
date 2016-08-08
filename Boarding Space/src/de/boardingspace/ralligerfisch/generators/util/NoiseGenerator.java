package de.boardingspace.ralligerfisch.generators.util;

import java.util.Random;

import de.boardingspace.ralligerfisch.util.Dimension;
import de.boardingspace.ralligerfisch.util.NDimensionalArray;

public class NoiseGenerator {
	private Random randomGenerator = new Random();

	public NoiseGenerator() {
	}

	public NoiseGenerator(Long seed) {
		randomGenerator = new Random(seed);
	}

	public NDimensionalArray<Double> generateFBM(Dimension<Integer> dimension, Float H, Float lacunarity,
			Integer octaves, Noise basis) {
		Float[] exponents = new Float[octaves + 1];
		Float frequency = 1.0f;
		for (Integer i = 0; i <= octaves; i++) {
			exponents[i] = new Float(Math.pow(frequency, -H));
			frequency *= lacunarity;
		}

		NDimensionalArray<Double> retval = new NDimensionalArray<Double>(dimension);

		for (Integer i = 0; i < retval.getLinearSize(); i++) {
			Integer[] coords = retval.linearIndexToCoordinates(i);
			Double[] tmpCoords = new Double[coords.length];
			for(Integer j = 0; j<coords.length;j++){
				tmpCoords[j] = coords[j].doubleValue()*randomGenerator.nextDouble();
			}
			retval.setLinearIndex(
					fBMEvaluate(exponents, H, lacunarity, octaves, basis, tmpCoords), i);
		}
		return retval;
	}

	private Double fBMEvaluate(Float[] exponents, Float H, Float lacunarity, Integer octaves, Noise basis,
			Double... location) {
		Double value = 0.0d;
		Double remainder;
		Integer i;

		// to prevent "cascading" effects
		for (Integer j = 0; j < location.length; j++) {
			location[j] += randomGenerator.nextDouble();
		}

		for (i = 0; i < octaves; i++) {
			value += basis.evaluate(location) * exponents[i];
			for (Integer j = 0; j < location.length; j++) {
				location[j] = location[j] * lacunarity;
			}
		}

		remainder = new Double(octaves - octaves);
		if (remainder != 0)
			value += remainder * basis.evaluate(location) * exponents[i];

		return value;
	}

}
