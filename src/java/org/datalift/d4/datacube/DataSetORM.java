package org.datalift.d4.datacube;

import java.util.LinkedList;

import org.datalift.fwk.Configuration;
import org.datalift.fwk.log.Logger;
import org.datalift.fwk.rdf.Repository;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

public class DataSetORM {

	private final String sourceURI;
	private final String lang;
	
	private DataSet dataset;
	
	private static final String uri = "uri";
	private static final String title = "title";

	private static final String description = "description";
	private static final String comment = "comment";
	private static final String publisherName = "publisherName";
	private static final String publisherURL = "publisherURL";
	private static final String depictionURL = "depictionURL";
	private static final String coverageName = "coverageName";
	private static final String coverageURL = "coverageURL";
	private static final String licenseName = "licenseName";
	private static final String formatName = "formatName";
	private static final String sourceName = "sourceName";
	private static final String modificationDate = "modificationDate";
	private static final String releaseDate = "releaseDate";
	private static final String periodicity = "periodicity";
	private static final String unitMeasureName = "unitMeasureName";
	private static final String unitMeasure = "unitMeasure";

	private static final String dimensionName = "dimensionName";
	private static final String dimensionURI = "dimensionURI";
	private static final String measureName = "measureName";
	private static final String measureURI = "measureURI";

	private static final String structureName = "structureName";
	
	/** Datalift's internal Sesame {@link Repository repository}. **/
    protected static final Repository INTERNAL_REPO = Configuration.getDefault().getInternalRepository();
    /** Datalift's internal Sesame {@link Repository repository} URL. */
    protected static final String INTERNAL_URL = INTERNAL_REPO.getEndpointUrl();
    /** Datalift's logging system. */
    protected static final Logger LOG = Logger.getLogger();
	
	public DataSetORM(String sourceURI, String lang) {
		this.sourceURI = sourceURI;
		this.lang = lang;
		
		dataset = new DataSet();
		dataset.setGraph(sourceURI);
	}
	
	public String filterLang(String binding) {
		return "FILTER ( lang(?" + binding + ") = '" + lang + "' ) ";
	}
	
	public DataSet getDataSet() {
		getMetaData();
		getObservations();
		
		return dataset;
	}
	
	private void getMetaData() {
		TupleQuery tq;
		TupleQueryResult tqr;
		
		String prefix = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
				+ "PREFIX sdmx-measure: <http://purl.org/linked-data/sdmx/2009/measure#> "
				+ "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
				+ "PREFIX dct: <http://purl.org/dc/terms/> "
				+ "PREFIX sdmx-attribute: <http://purl.org/linked-data/sdmx/2009/attribute#> "
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#> "
				+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> "
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "PREFIX qb: <http://purl.org/linked-data/cube#> "
				+ "PREFIX sdmx-dimension: <http://purl.org/linked-data/sdmx/2009/dimension#>";
		String bindings = "?" + uri
						+ " ?" + title
						+ " ?" + description
						+ " ?" + comment
						+ " ?" + publisherName
						+ " ?" + publisherURL
						+ " ?" + depictionURL
						+ " ?" + coverageName
						+ " ?" + coverageURL
						+ " ?" + licenseName
						+ " ?" + formatName
						+ " ?" + sourceName
						+ " ?" + modificationDate
						+ " ?" + releaseDate
						+ " ?" + periodicity
						+ " ?" + unitMeasureName
						+ " ?" + unitMeasure
						+ " ?" + dimensionName
						+ " ?" + dimensionURI
						+ " ?" + measureName
						+ " ?" + measureURI
						+ " ?" + structureName;
		String from = "FROM <" + sourceURI + ">";

		String where = "?" + uri + " a qb:DataSet ."
					+ " ?" + uri + " dct:title ?" + title + " ."
					+ " ?" + uri + " dct:description ?" + description + " ."
					+ " ?" + uri + " rdfs:comment ?" + comment + " ."
					+ " ?" + uri + " dct:publisher ?pubtmp ."
					+ " ?pubtmp foaf:name ?" + publisherName + " ."
					+ " ?pubtmp foaf:homepage ?" + publisherURL + " ."
					+ " ?" + uri + " foaf:depiction  ?" + depictionURL + " ."
					+ " ?" + uri + " dct:coverage ?" + coverageURL + " ."
					+ " ?" + coverageURL + " rdfs:label ?" + coverageName + " ."
					+ " ?" + uri + " dct:license ?" + licenseName + " ."
					+ " ?" + uri + " dct:format ?" + formatName + " ."
					+ " ?" + uri + " dct:source ?" + sourceName + " ."
					+ " ?" + uri + " dct:modified ?" + modificationDate + " ."
					+ " ?" + uri + " dct:issued ?" + releaseDate + " ."
					+ " ?" + uri + " dct:accrualPeriodicity ?pertmp ."
					+ " ?pertmp rdfs:label ?" + periodicity + " ."
					+ " ?" + uri + " sdmx-attribute:unitMeasure ?" + unitMeasure + " ."
					+ " ?" + unitMeasure + " rdfs:label ?" + unitMeasureName + " ."
					+ " ?" + uri + " qb:structure ?dsdtmp ."
					+ " ?dsdtmp rdfs:label ?" + structureName + " ."
					+ " ?dsdtmp qb:component ?dimtmp ."
					+ " ?dimtmp qb:dimension ?" + dimensionURI + " ."
					+ " ?dimtmp rdfs:label ?" + dimensionName + " ."
					+ " ?dsdtmp qb:component ?msrtmp ."
					+ " ?msrtmp qb:measure ?" + measureURI + " ."
					+ " ?msrtmp rdfs:label ?" + measureName + " .";
		String filterLang = filterLang(title) + filterLang(description)
						+ filterLang(comment) + filterLang(periodicity)
						+ filterLang(structureName) + filterLang(dimensionName)
						+ filterLang(measureName);
		String query = prefix + " SELECT " + bindings + " " + from + " WHERE { " + where + " " + filterLang + " }";
		
		LOG.debug("Processing query: \"{}\"", query);
		RepositoryConnection cnx = INTERNAL_REPO.newConnection();
		try {
			tq = cnx.prepareTupleQuery(QueryLanguage.SPARQL, query);
			tqr = tq.evaluate();
			
			BindingSet bs = tqr.next();
			dataset.setUri(bs.getValue(uri).stringValue());
			dataset.setTitle(bs.getValue(title).stringValue());
			dataset.setDescription(bs.getValue(description).stringValue());
			dataset.setComment(bs.getValue(comment).stringValue());
			dataset.setPublisherName(bs.getValue(publisherName).stringValue());
			dataset.setPublisherURL(bs.getValue(publisherURL).stringValue());
			dataset.setDepictionURL(bs.getValue(depictionURL).stringValue());
			dataset.setCoverageName(bs.getValue(coverageName).stringValue());
			dataset.setCoverageURL(bs.getValue(coverageURL).stringValue());
			
			dataset.setLicenseName(bs.getValue(licenseName).stringValue());
			dataset.setFormatName(bs.getValue(formatName).stringValue());
			dataset.setSourceName(bs.getValue(sourceName).stringValue());
			
			dataset.setModificationDate(bs.getValue(modificationDate).stringValue());
			dataset.setReleaseDate(bs.getValue(releaseDate).stringValue());
			dataset.setPeriodicity(bs.getValue(periodicity).stringValue());
			dataset.setUnitMeasureName(bs.getValue(unitMeasureName).stringValue());
			dataset.setUnitMeasure(bs.getValue(unitMeasure).stringValue());
			
			dataset.setDimensionName(bs.getValue(dimensionName).stringValue());
			dataset.setDimensionURI(bs.getValue(dimensionURI).stringValue());
			dataset.setMeasureName(bs.getValue(measureName).stringValue());
			dataset.setMeasureURI(bs.getValue(measureURI).stringValue());
			dataset.setStructureName(bs.getValue(structureName).stringValue());
			
			while (tqr.hasNext()) {
				bs = tqr.next();
			}
			
			dataset.setLicenseURL(bs.getValue(licenseName).stringValue());
			dataset.setFormatURL(bs.getValue(formatName).stringValue());
			dataset.setSourceURL(bs.getValue(sourceName).stringValue());
			
		}
		catch (MalformedQueryException e) {
			LOG.fatal("Failed to process query \"{}\":", e, query);
		} catch (QueryEvaluationException e) {
			LOG.fatal("Failed to process query \"{}\":", e, query);
		} catch (RepositoryException e) {
			LOG.fatal("Failed to process query \"{}\":", e, query);
		}
		finally {
		    try { cnx.close(); } catch (Exception e) { /* Ignore... */ }
		}
	}
	
	private void getObservations() {
		TupleQuery tq;
		TupleQueryResult tqr;
		
		String prefix = "PREFIX sdmx-measure: <http://purl.org/linked-data/sdmx/2009/measure#> "
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
				+ "PREFIX qb: <http://purl.org/linked-data/cube#> "
				+ "PREFIX sdmx-dimension: <http://purl.org/linked-data/sdmx/2009/dimension#>";
		String where = "?s a qb:Observation . ?s sdmx-dimension:refArea ?dim . ?s sdmx-measure:obsValue ?obs .";
		String query = prefix + " SELECT ?dim ?obs FROM <" + sourceURI + "> WHERE { " + where + " }";
		
		LOG.debug("Processing query: \"{}\"", query);
		RepositoryConnection cnx = INTERNAL_REPO.newConnection();
		try {
			tq = cnx.prepareTupleQuery(QueryLanguage.SPARQL, query);
			tqr = tq.evaluate();
			
			BindingSet bs;
			LinkedList<Observation> tmp = new LinkedList<Observation>();
			
			while (tqr.hasNext()) {
				bs = tqr.next();
				tmp.add(new Observation(bs.getValue("dim").stringValue(), bs.getValue("obs").stringValue()));
				
			}
			
			dataset.setObservations(tmp);
			
		}
		catch (MalformedQueryException e) {
			LOG.fatal("Failed to process query \"{}\":", e, query);
		} catch (QueryEvaluationException e) {
			LOG.fatal("Failed to process query \"{}\":", e, query);
		} catch (RepositoryException e) {
			LOG.fatal("Failed to process query \"{}\":", e, query);
		}
		finally {
		    try { cnx.close(); } catch (Exception e) { /* Ignore... */ }
		}
	}
 
}
