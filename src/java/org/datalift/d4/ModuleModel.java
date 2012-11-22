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

    /** Binding for the default subject var in SPARQL. */
    protected static final String SB = "s";
    /** Binding for the default predicate var in SPARQL. */
    protected static final String PB = "p";
    /** Binding for the default object var in SPARQL. */
    protected static final String OB = "o";

    /** Default WHERE SPARQL clause to retrieve all classes. */
    private static final String CLASS_WHERE = "{?" + SB + " a ?" + OB + "}";
    /** Default WHERE SPARQL clause to retrieve all predicates. */
    private static final String PREDICATE_WHERE = "{?" + SB + " ?" + PB + " ?" + OB + "}";

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

    /**
     * Retrieves multiple queries results based on a query pattern executed on
     * multiple contexts.
     * @param contexts Contexts on which the query will be executed.
     * @param where Constraints given by the query.
     * @param bind Binding to use to retrieve data.
     * @return Results as a LinkedList of Strings.
     */
    protected final LinkedList<String> getMultipleResults(LinkedList<String> contexts, String where, String bind) {
    	LinkedList<String> ret = new LinkedList<String>();

    	for (String context : contexts) {
    		ret.addAll(selectQuery(writeQuery(context, where, bind), bind));
    	}
    	return ret;
    }

    /**
     * Retrieves all of the classes used inside given contexts.
     * @param contexts The contexts to use.
     * @return A LinkedList of all of the classes used inside the contexts.
     */
    protected final LinkedList<String> getAllClasses(LinkedList<String> contexts) {
		return getMultipleResults(contexts, CLASS_WHERE, OB);
	}

	/**
     * Retrieves all of the predicates used inside given contexts.
     * @param contexts The contexts to use.
     * @return A LinkedList of all of the predicates used inside the contexts.
     */
	protected final LinkedList<String> getAllPredicates(LinkedList<String> contexts) {
		return getMultipleResults(contexts, PREDICATE_WHERE, PB);
	}

	//-------------------------------------------------------------------------
    // Value validation.
    //-------------------------------------------------------------------------

	/**
     * Checks whether a value is empty, eg. "", or the values corresponding
     * to a field that wasn't filled.
     * @param val Value to check.
     * @return True if val is empty.
     */
	protected boolean isEmptyValue(String val) {
    	return val.isEmpty()
    		|| val.equals(getTranslatedResource("field.none"))
    		|| val.equals(getTranslatedResource("field.optional"))
    		|| val.equals(getTranslatedResource("field.mandatory"));
    }

	/**
     * Checks if the given string is numeric. Relies on exceptions being thrown,
     * thus not very effective performance-wise.
     * @param str A possibly numerical string.
     * @return True if str is numeric.
     */
    public static boolean isNumeric(String str) {
    	boolean ret = true;
    	try {
    		Double.parseDouble(str);
    	}
    	catch (NumberFormatException nfe) {
    		ret = false;
    	}
    	return ret;
    }

	/**
     * Checks whether the given sources are different.
     * @param one Source to check.
     * @param two Source to check.
     * @return True if the sources are different.
     */
    protected boolean isDifferentSources(String one, String two) {
    	return !one.equals(two);
    }

	/**
     * Checks whether the given source exists for a given project.
     * @param val Source to find.
     * @param proj Project where to search for the source.
     * @return True if the source exists in the given project.
     */
    protected boolean isValidSource(String val, Project proj) {
    	return !isEmptyValue(val) && getSourcesURIs(proj).contains(val);
    }

    /**
     * Checks whether a value is valid, eg. is inside a list. The value must be
     * trimmed first.
     * @param val Value to check.
     * @param values List where the value must be.
     * @return True if the value is valid.
     */
    protected boolean isValidValue(String val, LinkedList<String> values) {
    	return !val.isEmpty() && values.contains(val);
    }

    /**
     * Checks whether the given class exists inside a given context.
     * @param val Class to find.
     * @param context Context where to search for the class.
     * @return True if the class exists in the given context.
     */
    protected boolean isValidClass(String val, String context) {
    	return !val.isEmpty() && !context.isEmpty()
    		&& isValidValue(val, selectQuery(writeQuery(context, CLASS_WHERE, OB), OB));
    }

    /**
     * Checks whether the given predicate exists inside a given context.
     * @param val Class to find.
     * @param context Context where to search for the predicate.
     * @return True if the predicate exists in the given context.
     */
    protected boolean isValidPredicate(String val, String context) {
    	return !val.isEmpty() && !context.isEmpty()
    		&& isValidValue(val, selectQuery(writeQuery(context, PREDICATE_WHERE, PB), PB));
    }

    //-------------------------------------------------------------------------
    // Launcher management.
    //-------------------------------------------------------------------------
}

