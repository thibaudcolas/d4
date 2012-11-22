package org.datalift.d4.datacube;

/**
 * Represents a statistical observation as a key / value pair.
 * There are other much more complex dataset structures out there but this one is the simplest.
 * @author tcolas
 *
 */
public class Observation {

	/** Dimension of an observation, i.e what's being observed. **/
	private String dimension;
	/** Measure of an observation, i.e the observed value for the given dimension. **/
	private String measure;

	public Observation(String dimension, String measure) {
		this.dimension = dimension;
		this.measure = measure;
	}

	public String getDimension() {
		return dimension;
	}

	public String getMeasure() {
		return measure;
	}
}
