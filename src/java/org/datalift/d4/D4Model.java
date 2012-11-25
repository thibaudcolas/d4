/*
 * Copyright / LIRMM 2012
 * Contributor(s) : T. Colas, F. Scharffe
 *
 * Contact: thibaud.colas@etud.univ-montp2.fr
 *
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software. You can use,
 * modify and/or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty and the software's author, the holder of the
 * economic rights, and the successive licensors have only limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading, using, modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean that it is complicated to manipulate, and that also
 * therefore means that it is reserved for developers and experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and, more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */

package org.datalift.d4;

import java.util.LinkedList;

import org.datalift.d4.datacube.DataSet;
import org.datalift.d4.datacube.DataSetORM;
import org.datalift.fwk.project.Project;
import org.datalift.fwk.project.Source;
import org.datalift.fwk.project.Source.SourceType;
import org.datalift.fwk.project.TransformedRdfSource;

/**
 * D4's backend main class.
 * Handles DataSets' retrieval and project compatibility check.
 *
 * @author tcolas
 * @version 071112
 */
public class D4Model extends ModuleModel {
    //-------------------------------------------------------------------------
    // Constructors
    //-------------------------------------------------------------------------

    /**
     * Creates a new D4Model instance.
     * @param name Name of the module.
     */
    public D4Model(String name) {
    	super(name);
    }

    //-------------------------------------------------------------------------
    // Sources management.
    //-------------------------------------------------------------------------

    /**
     * Checks if a given {@link Source} contains valid RDF-structured data
     * AND DataCube data.
     * @param src The source to check.
     * @return True if src is {@link TransformedRdfSource} and contains QB.
     */
    protected boolean isValidSource(Source src) {
    	return src.getType() == SourceType.TransformedRdfSource
    			&& containsDataCube(src);
    }

    /**
     * Checks if a given {@link Source} contains DataCube data.
     * @param src The source to check.
     * @return True if src contains at least one qb:Dataset.
     */
    protected boolean containsDataCube(Source src) {
    	String query = writeQuery(src.getUri(), "{?d a <http://purl.org/linked-data/cube#DataSet>}", "d");
    	LinkedList<String> ret = selectQuery(query, "d");

    	return !ret.isEmpty();
    }

    /**
     * Retrieves all of the DataCube DataSets from a given project's sources.
     */
    public LinkedList<DataSet> getProjectDataSets(Project proj) {
    	LinkedList<DataSet> ret = new LinkedList<DataSet>();
    	DataSetORM datasetMapper;
    	for (Source src : proj.getSources()) {
    		if (isValidSource(src)) {
    			datasetMapper = new DataSetORM(src.getUri(), getTranslatedResource("page.lang"));
    			ret.add(datasetMapper.getDataSet());
    		}
    	}

    	return ret;
    }
}
