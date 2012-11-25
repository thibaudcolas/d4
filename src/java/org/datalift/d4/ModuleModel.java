/*
 * Copyright / LIRMM 2011-2012
 * Contributor(s) : T. Colas, F. Scharffe
 *
 * Contact: thibaud.colas@etud.univ-montp2.fr
 */

package org.datalift.d4;

import java.util.Iterator;
import java.util.LinkedList;

import org.datalift.fwk.Configuration;
import org.datalift.fwk.i18n.PreferredLocales;
import org.datalift.fwk.log.Logger;
import org.datalift.fwk.project.Project;
import org.datalift.fwk.project.Source;
import org.datalift.fwk.project.SparqlSource;
import org.datalift.fwk.project.TransformedRdfSource;
import org.datalift.fwk.rdf.Repository;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

/**
 * An abstract class for some modules, combining default
 * operations and values.
 *
 * @author tcolas
 * @version 071112
 */
public abstract class ModuleModel {

    //-------------------------------------------------------------------------
    // Constants
    //-------------------------------------------------------------------------

    /** Base name of the resource bundle for converter GUI. */
    protected static String GUI_RESOURCES_BUNDLE = ModuleController.GUI_RESOURCES_BUNDLE;

    //-------------------------------------------------------------------------
    // Class members
    //-------------------------------------------------------------------------

    /** Datalift's internal Sesame {@link Repository repository}. **/
    protected static final Repository INTERNAL_REPO = Configuration.getDefault().getInternalRepository();
    /** Datalift's internal Sesame {@link Repository repository} URL. */
    protected static final String INTERNAL_URL = INTERNAL_REPO.getEndpointUrl();
    /** Datalift's logging system. */
    protected static final Logger LOG = Logger.getLogger();

    //-------------------------------------------------------------------------
    // Instance members
    //-------------------------------------------------------------------------

    /** The module name. */
    protected final String moduleName;

    //-------------------------------------------------------------------------
    // Constructors
    //-------------------------------------------------------------------------

    /**
     * Creates a new InterconnectionModel instance.
     * @param module Name of the module.
     */
    public ModuleModel(String module) {
        this.moduleName = module;
    }

    /**
     * Resource getter.
     * @param key The key to retrieve.
     * @return The value of key.
     */
    protected String getTranslatedResource(String key) {
    	return PreferredLocales.get().getBundle(GUI_RESOURCES_BUNDLE, ModuleModel.class).getString(key);
    }

    //-------------------------------------------------------------------------
    // Sources management.
    //-------------------------------------------------------------------------

    /**
     * Checks if a given {@link Source} is valid for our uses.
     * @param src The source to check.
     * @return True if src is {@link TransformedRdfSource} or {@link SparqlSource}.
     */
    protected abstract boolean isValidSource(Source src);

    /**
     * Checks if a {@link Project proj} contains valid RDF sources.
     * @param proj The project to check.
     * @param minvalid The number of RDF sources we want to have.
     * @return True if there are more than number valid sources.
     */
    protected final boolean hasMultipleRDFSources(Project proj, int minvalid) {
    	int cpt = 0;
    	Iterator<Source> sources = proj.getSources().iterator();

    	while (sources.hasNext() && cpt < minvalid) {
    		if (isValidSource(sources.next())) {
    			cpt++;
    		}
    	}
    	return cpt >= minvalid;
    }

    /**
     * Returns all of the URIs (as strings) from the {@link Project project}.
     * @param proj The project to use.
     * @return A LinkedList containing source file's URIs as strings.
     */
    protected final LinkedList<String> getSourcesURIs(Project proj) {
    	LinkedList<String> ret = new LinkedList<String>();

    	for (Source src : proj.getSources()) {
    		if (isValidSource(src)) {
    			ret.add(src.getUri());
    		}
    	}
    	return ret;
    }

    //-------------------------------------------------------------------------
    // Queries management.
    //-------------------------------------------------------------------------

    /**
	 * Tels if the bindings of the results are well-formed.
	 * @param tqr The result of a SPARQL query.
	 * @param bind The result one and only binding.
	 * @return True if the results contains only bind.
	 * @throws QueryEvaluationException Error while closing the result.
	 */
	protected boolean hasCorrectBindingNames(TupleQueryResult tqr, String bind) throws QueryEvaluationException {
		return tqr.getBindingNames().size() == 1 && tqr.getBindingNames().contains(bind);
	}

	/**
	 * Sends and evaluates a SPARQL select query on the data set, then returns
	 * the results (which must be one-column only) as a list of Strings.
	 * @param query The SPARQL query without its prefixes.
	 * @param bind The result one and only binding.
	 * @return The query's result as a list of Strings.
	 */
    protected LinkedList<String> selectQuery(String query, String bind) {
		TupleQuery tq;
		TupleQueryResult tqr;
		LinkedList<String> ret = new LinkedList<String>();

		LOG.debug("Processing query: \"{}\"", query);
		RepositoryConnection cnx = INTERNAL_REPO.newConnection();
		try {
			tq = cnx.prepareTupleQuery(QueryLanguage.SPARQL, query);
			tqr = tq.evaluate();

			if (!hasCorrectBindingNames(tqr, bind)) {
				throw new MalformedQueryException("Wrong query result bindings - " + query);
			}

			while (tqr.hasNext()) {
				ret.add(tqr.next().getValue(bind).stringValue());
			}
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
	    return ret;
	}

    /**
     * Writes a query given a bind to retrieve, a context and a WHERE clause.
     * @param context Context on which the query will be executed.
     * @param where Constraints given by the query.
     * @param bind Binding to use to retrieve data.
     * @return A full query.
     */
    protected String writeQuery(String context, String where, String bind) {
    	String ret = "SELECT DISTINCT ?" + bind
    			+ (context.isEmpty() ? "" : " FROM <" + context + ">")
    			+ " WHERE " + where;
    	return ret;
    }
