package cz.cuni.mff.xrg.odcs.commons.app.dataunit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openrdf.model.Model;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.*;
import org.openrdf.query.impl.DatasetImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.commons.app.dataunit.inmemory.InMemoryRDFDataUnit;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnit;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnitType;
import cz.cuni.mff.xrg.odcs.rdf.enums.SPARQLQueryType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.InvalidQueryException;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.query.utils.QueryPart;
import cz.cuni.mff.xrg.odcs.rdf.repositories.FileRDFMetadataExtractor;
import cz.cuni.mff.xrg.odcs.rdf.repositories.OrderTupleQueryResultImpl;

/**
 * Abstract class provides common parent methods for RDFDataUnit implementation.
 * 
 * @author Jiri Tomes
 */
public abstract class AbstractRDFDataUnit implements ManagableRdfDataUnit {

    private FileRDFMetadataExtractor fileRDFMetadataExtractor;

    /**
     * Default name for graph using for store RDF data.
     */
    protected static final String DEFAULT_GRAPH_NAME = "http://default";

    /**
     * Logging information about execution of method using openRDF.
     */
    private static final Logger LOG = LoggerFactory.getLogger(AbstractRDFDataUnit.class);

    /**
     * Default used encoding.
     */
    protected final String encode = "UTF-8";

    private String dataUnitName;

    protected URI dataGraph;

    private List<RepositoryConnection> requestedConnections;

    private Thread ownerThread;

    public abstract RepositoryConnection getConnectionInternal() throws RepositoryException;

    public AbstractRDFDataUnit(String dataUnitName, String dataGraph) {
        this.dataUnitName = dataUnitName;
        if (!dataGraph.toLowerCase().startsWith("http://")) {
            dataGraph = "http://" + dataGraph;
        }
        this.dataGraph = new URIImpl(dataGraph);
        LOG.info("Set new data graph - " + this.dataGraph.stringValue());

        this.requestedConnections = new ArrayList<>();
        this.ownerThread = Thread.currentThread();

        this.fileRDFMetadataExtractor = new FileRDFMetadataExtractor(this);
    }

    //DataUnit interface
    @Override
    public DataUnitType getType() {
        return DataUnitType.RDF;
    }

    //DataUnit interface
    @Override
    public boolean isType(DataUnitType dataUnitType) {
        return this.getType().equals(dataUnitType);
    }

    //DataUnit interface
    @Override
    public String getDataUnitName() {
        return dataUnitName;
    }

    //DataUnit interface
    @Override
    public void addAll(DataUnit unit) {
        this.merge(unit);
    }

    //RDFDataUnit interface
    @Override
    public RepositoryConnection getConnection() throws RepositoryException {
        if (!ownerThread.equals(Thread.currentThread())) {
            throw new RuntimeException("Constraint violation, only one thread can access this data unit");
        }

        RepositoryConnection connection = getConnectionInternal();
        requestedConnections.add(connection);
        return connection;
    }

    //RDFDataUnit interface
    @Override
    public URI getDataGraph() {
        return dataGraph;
    }

    //RDFDataUnit interface
    @Override
    public Map<String, List<String>> getRDFMetadataForSubjectURI(
            String subjectURI, List<String> predicates) {
        return this.fileRDFMetadataExtractor.getMetadataForSubject(subjectURI,
                predicates);
    }

    //RDFDataUnit interface
    @Override
    public Map<String, List<String>> getRDFMetadataForFile(String filePath,
            List<String> predicates) {
        return this.fileRDFMetadataExtractor.getMetadataForFilePath(filePath,
                predicates);
    }

    //RDFDataUnit interface
    @Override
    public OrderTupleQueryResultImpl executeOrderSelectQueryAsTuples(
            String orderSelectQuery) throws InvalidQueryException {

        OrderTupleQueryResultImpl result = new OrderTupleQueryResultImpl(
                orderSelectQuery, this);
        return result;
    }

    //ManagableDataUnit interface
    @Override
    public void isReleaseReady() {
        int count = 0;
        for (RepositoryConnection connection : requestedConnections) {
            try {
                if (connection.isOpen()) {
                    count++;
                }
            } catch (RepositoryException ex) {
                try {
                    connection.close();
                } catch (RepositoryException ex1) {
                    LOG.warn("Error when closing connection", ex1);
                    // eat close exception, we cannot do anything clever here
                }
            }
        }

        if (count > 0) {
            LOG.error("{} connections remained opened after DPU execution on graph <{}>, dataUnitName '{}'.", count, this.getDataGraph(), this.getDataUnitName());
        }
    }

    @Override
    //ManagableDataUnit interface
    public void clear() {
        /**
         * Beware! Clean is called from different thread then all other operations (pipeline executor thread).
         * That is the reason why we cannot obtain connection using this.getConnection(), it would throw an Exception.
         * This connection has to be obtained directly from repository and we take care to close it properly.
         */
        RepositoryConnection connection = null;
        try {
            connection = getConnectionInternal();
            connection.clear(dataGraph);
        } catch (RepositoryException ex) {
            throw new RuntimeException("Could not delete repository", ex);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {
                    LOG.warn("Error when closing connection", ex);
                    // eat close exception, we cannot do anything clever here
                }
            }
        }
    }

    //ManagableDataUnit interface
    @Override
    public void release() {
        List<RepositoryConnection> openedConnections = new ArrayList<>();
        for (RepositoryConnection connection : requestedConnections) {
            try {
                if (connection.isOpen()) {
                    openedConnections.add(connection);
                }
            } catch (RepositoryException ex) {
                try {
                    connection.close();
                } catch (RepositoryException ex1) {
                    LOG.warn("Error when closing connection", ex1);
                    // eat close exception, we cannot do anything clever here
                }
            }
        }

        if (!openedConnections.isEmpty()) {
            LOG.error(String.valueOf(openedConnections.size()) + " connections remained opened after DPU execution.");
            for (RepositoryConnection connection : openedConnections) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {
                    LOG.warn("Error when closing connection", ex);
                    // eat close exception, we cannot do anything clever here
                }
            }
        }
    }

    //ManagableDataUnit interface
    @Override
    public void merge(DataUnit otherDataUnit) throws IllegalArgumentException {
        if (!(otherDataUnit instanceof InMemoryRDFDataUnit)) {
            throw new IllegalArgumentException("Incompatible repository type");
        }

        final RDFDataUnit otherRDFDataUnit = (RDFDataUnit) otherDataUnit;
        RepositoryConnection connection = null;
        try {
            connection = getConnection();

            String sourceGraphName = otherRDFDataUnit.getDataGraph().stringValue();
            String targetGraphName = getDataGraph().stringValue();

            LOG.info("Trying to merge {} triples from <{}> to <{}>.",
                    connection.size(otherRDFDataUnit.getDataGraph()), sourceGraphName,
                    targetGraphName);

            String mergeQuery = String.format("ADD <%s> TO <%s>", sourceGraphName,
                    targetGraphName);

            Update update = connection.prepareUpdate(
                    QueryLanguage.SPARQL, mergeQuery);

            update.execute();

            LOG.info("Merged {} triples from <{}> to <{}>.",
                    connection.size(getDataGraph()), sourceGraphName,
                    targetGraphName);
        } catch (MalformedQueryException ex) {
            LOG.error("NOT VALID QUERY: {}", ex);
        } catch (RepositoryException ex) {
            LOG.error(ex.getMessage(), ex);
        } catch (UpdateExecutionException ex) {
            LOG.error(ex.getMessage(), ex);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {
                    LOG.warn("Error when closing connection", ex);
                    // eat close exception, we cannot do anything clever here
                }
            }
        }
    }

    /**
     * For Browsing all data in graph return its size {count of rows}.
     */
    //ManagableRDFDataUnit interface
    @Override
    public long getResultSizeForDataCollection() throws InvalidQueryException {
        final String selectQuery = "SELECT ?x ?y ?z WHERE {?x ?y ?z}";
        return getSizeForSelect(new QueryPart(selectQuery));
    }

    /**
     * For given valid SELECT of CONSTRUCT query return its size {count of rows
     * returns for given query).
     * 
     * @param query
     *            Valid SELECT/CONTRUCT query for asking.
     * @return size for given valid query as long.
     * @throws InvalidQueryException
     *             if query is not valid.
     */
    @Override
    //ManagableRDFDataUnit interface
    public long getResultSizeForQuery(String query) throws InvalidQueryException {

        long size = 0;

        QueryPart queryPart = new QueryPart(query);
        SPARQLQueryType type = queryPart.getSPARQLQueryType();

        switch (type) {
            case SELECT:
                size = getSizeForSelect(queryPart);
                break;
            case CONSTRUCT:
            case DESCRIBE:
                size = getSizeForConstruct(query);
                break;
            case UNKNOWN:
                throw new InvalidQueryException(
                        "Given query: " + query + "have to be SELECT or CONSTRUCT type.");
        }

        return size;

    }

    private long getSizeForConstruct(String constructQuery) throws InvalidQueryException {
        long size = 0;

        RepositoryConnection connection = null;

        try {
            connection = getConnection();

            GraphQuery graphQuery = connection.prepareGraphQuery(
                    QueryLanguage.SPARQL,
                    constructQuery);
            DatasetImpl dataSet = new DatasetImpl();
            dataSet.addDefaultGraph(this.getDataGraph());
            dataSet.addNamedGraph(this.getDataGraph());
            graphQuery.setDataset(dataSet);
            try {
                GraphQueryResult result = graphQuery.evaluate();

                Model model = QueryResults.asModel(result);
                size = model.size();
                result.close();

            } catch (QueryEvaluationException ex) {

                throw new InvalidQueryException(
                        "This query is probably not valid. " + ex
                                .getMessage(),
                        ex);
            }

        } catch (MalformedQueryException ex) {
            throw new InvalidQueryException(
                    "This query is probably not valid as construct query. "
                            + ex.getMessage(), ex);
        } catch (RepositoryException ex) {
            LOG.error("Connection to RDF repository failed. {}",
                    ex.getMessage(), ex);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {
                    LOG.warn(
                            "Failed to close connection to RDF repository while querying. {}",
                            ex.getMessage(), ex);
                }
            }
        }

        return size;
    }

    private long getSizeForSelect(QueryPart queryPart) throws InvalidQueryException {

        final String sizeVar = "selectSize";

        final String sizeQuery = String.format(
                "%s SELECT (count(*) AS ?%s) WHERE {%s}", queryPart
                        .getQueryPrefixes(), sizeVar,
                queryPart.getQueryWithoutPrefixes());
        RepositoryConnection connection = null;
        try {
            connection = getConnection();

            TupleQuery tupleQuery = connection.prepareTupleQuery(
                    QueryLanguage.SPARQL, sizeQuery);
            DatasetImpl dataSet = new DatasetImpl();
            dataSet.addDefaultGraph(this.getDataGraph());
            dataSet.addNamedGraph(this.getDataGraph());
            tupleQuery.setDataset(dataSet);
            try {
                TupleQueryResult tupleResult = tupleQuery.evaluate();
                if (tupleResult.hasNext()) {
                    String selectSize = tupleResult.next()
                            .getValue(sizeVar).stringValue();
                    long resultSize = Long.parseLong(selectSize);

                    tupleResult.close();
                    return resultSize;
                }
                throw new InvalidQueryException(
                        "Query: " + queryPart.getQuery() + " has no bindings for information about its size");
            } catch (QueryEvaluationException ex) {
                throw new InvalidQueryException(
                        "This query is probably not valid. " + ex
                                .getMessage(),
                        ex);
            }

        } catch (MalformedQueryException ex) {
            throw new InvalidQueryException(
                    "This query is probably not valid. "
                            + ex.getMessage(), ex);
        } catch (RepositoryException ex) {
            LOG.error("Connection to RDF repository failed. {}",
                    ex.getMessage(), ex);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {
                    LOG.warn(
                            "Failed to close connection to RDF repository while querying. {}",
                            ex.getMessage(), ex);
                }
            }
        }

        return 0;
    }

}
