package Simulation;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Offers some common random distributions
 * @author 
 *
 */
public class Rand {

	/**
 	 * Returns a pseudo random number, uniformly distributed
 	 * @param least lower bound (inclusive)
 	 * @param bound upper bound (exclusive)
 	 * @return the random number
 	 */
	public static double rect(double least, double bound){
		return ThreadLocalRandom.current().nextDouble(least, bound);
	}
	
	/**
 	 * Returns a pseudo random number, exponentially distributed 
 	 * @param lambda mean
 	 * @return the random number
 	 */
	public static double exp(double lambda){
		double u = ThreadLocalRandom.current().nextDouble();
	    return Math.log(1-u)/(-lambda);
	}
	
	/**
 	 * Returns a pseudo random number, normally distributed
 	 * @param mean mean 
 	 * @param stddev standard deviation
 	 * @return the random number
 	 */
	public static double gauss(double mean, double stddev){
		double r = ThreadLocalRandom.current().nextGaussian();
		return r*stddev+mean;
	}
  
}
