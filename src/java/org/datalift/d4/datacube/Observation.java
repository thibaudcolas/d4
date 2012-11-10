package org.datalift.d4.datacube;

/**
 * Represents a statistical observation as a key / value pair.
 * @author tcolas
 *
 */
public class Observation {

	private String dimension;
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
