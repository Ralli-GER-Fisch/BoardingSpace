package de.boardingspace.ralligerfisch.generators.util;

import java.util.Random;

import de.boardingspace.ralligerfisch.util.Utilities;

public class Noise {
	private static Random randomGenerator = new Random();

	private final static Integer B = 0x100;
	private final static Integer BM = 0xff;
	private final static Integer N = 0x1000;

	private Integer[] p = new Integer[B + B + 2];
	private Double[][] g;
	private Boolean start = true;

	public Noise(Long seed) {
		randomGenerator = new Random(seed);
		start = true;
	}

	private static Double sCurve(Double t) {
		return t * t * (3.0f - 2.0f * t);
	}

	public Double evaluate(Double... location) {
		return noise(location);
	}

	public Double turbulence(Double octaves, Double... coords) {
		Double t = 0.0d;
		for (Double f = 1d; f <= octaves; f *= 2) {
			t += Math.abs(noise((Double[]) Utilities.<Double, Double>scaleArray(coords, f))) / f;
		}
		return t;
	}

	public Double noise(Double... coords) {
		Integer[]	n0 = new Integer[coords.length],
					n1 = new Integer[coords.length];
		Double[]	sn = new Double[coords.length],
					abc = new Double[(int) Math.pow(2, coords.length)],
					q;
		Double t;

		if (start) {
			start = false;
			init(coords.length);
		}

		for (Integer i = 0; i < coords.length; i++) {
			n0[i] = coords[i] > 0.0 ? coords[i].intValue(): coords[i].intValue()-1;
			n1[i] = n0[i] + 1;
			sn[i] = coords[i]-n0[i].doubleValue();
			//sn[i] = sCurve(rn0[i]);
		}

		
		for (Integer i = 0; i < Math.pow(2, coords.length); i++) {
			Integer index = 0;
			String binaryString = String.format("%" + coords.length + "s", Integer.toBinaryString(i)).replace(' ', '0');
			Integer[] thisRN = new Integer[coords.length];
			for(Integer j = 0; j< coords.length;j++){
				switch(binaryString.charAt(j)){
					case '0':
						//index += n0[j];
						thisRN[j] = n0[j];//coords[j];
						break;
					case '1':
						//index += n1[j];
						thisRN[j] = n1[j];//coords[j];
						break;
				}
			}
			//q = g[index];
			abc[i] = (Double) Utilities.dotProduct(thisRN,coords);
		}
		
		Integer depth = 0;
		Double[] abcTMP = abc;
		Double[] curABC;
		while (abcTMP.length >1){
			curABC = new Double[abcTMP.length/2];
			for(Integer i = 0;i<abcTMP.length/2;i++){
				curABC[i] = lerp(sn[depth],abcTMP[i*2],abcTMP[i*2+1]);
			}
			abcTMP = curABC;
			depth++;
		}

		return 1.5d * abcTMP[0];
	}

	public static Double lerp(Double t, Double a, Double b) {
		return (1.0 - t)*a+t*b;//a + t * (b - a);
	}

	private void init(int nrOfDimensions) {
		g = new Double[B + B + 2][nrOfDimensions];

		int i, j, k;

		for (i = 0; i < B; i++) {
			p[i] = i;
			for (j = 0; j < nrOfDimensions; j++)
				g[i][j] = (double) ((random() % (B + B)) - B) / B;
			g[i] = normalize(g[i]);
		}

		for (i = B - 1; i >= 0; i--) {
			k = p[i];
			p[i] = p[j = random() % B];
			p[j] = k;
		}

		for (i = 0; i < B + 2; i++) {
			p[B + i] = p[i];
			for (j = 0; j < nrOfDimensions; j++)
				g[B + i][j] = g[i][j];
		}
	}

	private Double[] normalize(Double[] v) {
		Double s = Math.sqrt(squaredSum(v));
		for (Integer i = 0; i < v.length; i++)
			v[i] = v[i] / s;
		return v;
	}

	private Double squaredSum(Double[] v) {
		Double retval = 0d;
		for (Integer i = 0; i < v.length; i++)
			retval += Math.pow(v[i], 2);
		return retval;
	}

	private static Integer random() {
		return randomGenerator.nextInt() & 0x7fffffff;
	}
}
