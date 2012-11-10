package org.datalift.d4.datacube;

import java.util.LinkedList;

import org.datalift.fwk.log.Logger;

public class DataSet {
	
	private String graph;
	private String uri;
	private String title;
	
	private String nbTriples;
	
	private String description;
	private String comment;
	private String publisherName;
	private String publisherURL;
	private String depictionURL;
	private String coverageName;
	private String coverageURL;
	private String licenseName;
	private String licenseURL;
	private String formatName;
	private String formatURL;
	private String sourceName;
	private String sourceURL;
	private String modificationDate;
	private String releaseDate;
	private String periodicity;
	private String unitMeasureName;
	private String unitMeasure;
	
	private String dimensionName;
	private String dimensionURI;
	private String measureName;
	private String measureURI;
	
	private String extractDim;
	private String extractMeas;
	private String extractDimBis;
	private String extractMeasBis;
	
	private String structureName;
	
	public LinkedList<Observation> observations;
	
	/** Datalift's logging system. */
    protected static final Logger LOG = Logger.getLogger();
	
	public DataSet() {
		observations = new LinkedList<Observation>();
	}

	public String getGraph() {
		return graph;
	}

	public void setGraph(String graph) {
		this.graph = (graph == null || graph.equals("") ? "N/A" : graph);
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = (uri == null || uri.equals("") ? "N/A" : uri);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = (title == null || title.equals("") ? "N/A" : title);
	}
	
	public String getNbTriples() {
		return nbTriples;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = (description == null || description.equals("") ? "N/A" : description);
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = (comment == null || comment.equals("") ? "" : comment);
	}

	public String getPublisherName() {
		return publisherName;
	}

	public void setPublisherName(String publisherName) {
		this.publisherName = (publisherName == null || publisherName.equals("") ? "N/A" : publisherName);
	}

	public String getPublisherURL() {
		return publisherURL;
	}

	public void setPublisherURL(String publisherURL) {
		this.publisherURL = (publisherURL == null || publisherURL.equals("") ? "N/A" : publisherURL);
	}

	public String getDepictionURL() {
		return depictionURL;
	}

	public void setDepictionURL(String depictionURL) {
		this.depictionURL = (depictionURL == null || depictionURL.equals("") ? "http://placekitten.com/g/300/200" : depictionURL);
	}

	public String getCoverageName() {
		return coverageName;
	}

	public void setCoverageName(String coverageName) {
		this.coverageName = (coverageName == null || coverageName.equals("") ? "N/A" : coverageName);
	}

	public String getCoverageURL() {
		return coverageURL;
	}

	public void setCoverageURL(String coverageURL) {
		this.coverageURL = (coverageURL == null || coverageURL.equals("") ? "N/A" : coverageURL);
	}

	public String getLicenseName() {
		return licenseName;
	}

	public void setLicenseName(String licenseName) {
		this.licenseName = (licenseName == null || licenseName.equals("") ? "N/A" : licenseName);
	}

	public String getLicenseURL() {
		return licenseURL;
	}

	public void setLicenseURL(String licenseURL) {
		this.licenseURL = (licenseURL == null || licenseURL.equals("") ? "N/A" : licenseURL);
	}

	public String getFormatName() {
		return formatName;
	}

	public void setFormatName(String formatName) {
		this.formatName = (formatName == null || formatName.equals("") ? "N/A" : formatName);
	}

	public String getFormatURL() {
		return formatURL;
	}

	public void setFormatURL(String formatURL) {
		this.formatURL = (formatURL == null || formatURL.equals("") ? "N/A" : formatURL);
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = (sourceName == null || sourceName.equals("") ? "N/A" : sourceName);
	}

	public String getSourceURL() {
		return sourceURL;
	}

	public void setSourceURL(String sourceURL) {
		this.sourceURL = (sourceURL == null || sourceURL.equals("") ? "N/A" : sourceURL);
	}

	public String getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(String modificationDate) {
		this.modificationDate = (modificationDate == null || modificationDate.equals("") ? "N/A" : modificationDate);
	}

	public String getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(String releaseDate) {
		this.releaseDate = (releaseDate == null || releaseDate.equals("") ? "N/A" : releaseDate);
	}

	public String getPeriodicity() {
		return periodicity;
	}

	public void setPeriodicity(String periodicity) {
		this.periodicity = (periodicity == null || periodicity.equals("") ? "N/A" : periodicity);
	}

	public String getUnitMeasureName() {
		return unitMeasureName;
	}

	public void setUnitMeasureName(String unitMeasureName) {
		this.unitMeasureName = (unitMeasureName == null || unitMeasureName.equals("") ? "N/A" : unitMeasureName);
	}

	public String getUnitMeasure() {
		return unitMeasure;
	}

	public void setUnitMeasure(String unitMeasure) {
		this.unitMeasure = (unitMeasure == null || unitMeasure.equals("") ? "N/A" : unitMeasure);
	}

	public String getDimensionName() {
		return dimensionName;
	}

	public void setDimensionName(String dimensionName) {
		this.dimensionName = (dimensionName == null || dimensionName.equals("") ? "N/A" : dimensionName);
	}

	public String getDimensionURI() {
		return dimensionURI;
	}

	public void setDimensionURI(String dimensionURI) {
		this.dimensionURI = (dimensionURI == null || dimensionURI.equals("") ? "N/A" : dimensionURI);
	}

	public String getMeasureName() {
		return measureName;
	}

	public void setMeasureName(String measureName) {
		this.measureName = (measureName == null || measureName.equals("") ? "N/A" : measureName);
	}

	public String getMeasureURI() {
		return measureURI;
	}

	public void setMeasureURI(String measureURI) {
		this.measureURI = (measureURI == null || measureURI.equals("") ? "N/A" : measureURI);
	}

	public String getStructureName() {
		return structureName;
	}

	public void setStructureName(String structureName) {
		this.structureName = (structureName == null || structureName.equals("") ? "N/A" : structureName);
	}

	public LinkedList<Observation> getObservations() {
		return observations;
	}

	public void setObservations(LinkedList<Observation> observations) {
		this.observations = observations;
		LOG.info(observations);
		extractDim = observations.getFirst().getDimension();
		extractDimBis = observations.getLast().getDimension();
		extractMeas = observations.getFirst().getMeasure();
		extractMeasBis = observations.getLast().getMeasure();
		nbTriples = String.valueOf(observations.size());
	}

	public String getExtractDim() {
		return extractDim;
	}

	public String getExtractMeas() {
		return extractMeas;
	}

	public String getExtractDimBis() {
		return extractDimBis;
	}

	public String getExtractMeasBis() {
		return extractMeasBis;
	}
}
