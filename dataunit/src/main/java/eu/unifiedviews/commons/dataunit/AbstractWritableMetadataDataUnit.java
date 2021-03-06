package eu.unifiedviews.commons.dataunit;

import eu.unifiedviews.commons.constants.Ontology;
import eu.unifiedviews.commons.dataunit.core.ConnectionSource;
import eu.unifiedviews.commons.dataunit.core.CoreServiceBus;
import eu.unifiedviews.commons.dataunit.core.FaultTolerant;
import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.MetadataDataUnit;
import eu.unifiedviews.dataunit.WritableMetadataDataUnit;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.URIImpl;
import org.eclipse.rdf4j.query.*;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Base class for dealing with metadata of all data units - metadata for data units is saved in RDF store.
 *
 * @author Michal Klempa
 * @author Škoda Petr
 */
public abstract class AbstractWritableMetadataDataUnit implements WritableMetadataDataUnit, ManagableDataUnit {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractWritableMetadataDataUnit.class);

    //names for the variables in the SPARQL queries projected out
    protected static final String SYMBOLIC_NAME_BINDING = "symbolicName";

    protected static final String PREDICATE_BINDING = "predicate";

    protected static final String OBJECT_BINDING = "object";

    protected static final String WRITE_CONTEXT_BINDING = "writeContext";

    /**
     * First %s stands for FROM clause place.
     */
    protected static final String DUPLICATE_ENTRIES_QUERY = "SELECT DISTINCT ?" + SYMBOLIC_NAME_BINDING + " %s WHERE { "
            + "?subject1 <" + MetadataDataUnit.PREDICATE_SYMBOLIC_NAME + "> ?" + SYMBOLIC_NAME_BINDING + " . "
            + "?subject2 <" + MetadataDataUnit.PREDICATE_SYMBOLIC_NAME + "> ?" + SYMBOLIC_NAME_BINDING + " . "
            + "FILTER ( ?subject1 != ?subject2 ) }";

    /**
     * First %s stands for write graph IRI.
     */
    protected static final String ADD_METADATA_QUERY = "INSERT INTO <%s> { ?s ?" + PREDICATE_BINDING + " ?" + OBJECT_BINDING + " } "
            + "WHERE { "
            + "?s <" + MetadataDataUnit.PREDICATE_SYMBOLIC_NAME + "> ?" + SYMBOLIC_NAME_BINDING + " . "
            + " } ";

    protected static final String CLEAR_QUERY = "WITH <" + Ontology.GRAPH_METADATA + "> DELETE { ?" + WRITE_CONTEXT_BINDING + " ?p ?o } "
            + "WHERE { "
            + "?" + WRITE_CONTEXT_BINDING + " ?p ?o "
            + "} ";

    /**
     * Name of this data unit.
     */
    protected String dataUnitName;

    /**
     * Name of assigned main graph. This graph is used to store information about entries.
     */
    protected IRI writeContext;

    /**
     * Names of read graphs.
     */
    protected Set<IRI> readContexts;

    /**
     * List of all requested connection.
     */
    private final Set<RepositoryConnection> requestedConnections;

    /**
     * Used to generate new entry URIs.
     */
    final AtomicInteger entryCounter = new AtomicInteger(0);

    /**
     * Services from core.
     */
    protected final CoreServiceBus coreServices;

    /**
     * Source of connection.
     */
    protected final ConnectionSource connectionSource;

    /**
     * Fault tolerant service.
     */
    protected final FaultTolerant faultTolerant;

    /**
     * A flag to hold whether this data unit is consumed by multiple inputs (data units in the following DPUs)
     */
    private boolean consumedByMultipleInputs;

    public AbstractWritableMetadataDataUnit(String dataUnitName, String writeContextString,
            CoreServiceBus coreServices) {
        this.dataUnitName = dataUnitName;
        this.writeContext = new URIImpl(writeContextString);
        this.readContexts = new HashSet<>();
        this.readContexts.add(this.writeContext);
        this.requestedConnections = new HashSet<>();
        this.coreServices = coreServices;
        // Load services.
        this.connectionSource = coreServices.getService(ConnectionSource.class);
        this.faultTolerant = coreServices.getService(FaultTolerant.class);
        this.consumedByMultipleInputs = false;
    }

    // MetadataDataUnit interface
    @Override
    public RepositoryConnection getConnection() throws DataUnitException {

        try {
            // Get connection.
            final RepositoryConnection connection = connectionSource.getConnection();

            // To watch if the connection has been properly closed.
            requestedConnections.add(connection);

            return connection;
        } catch (RepositoryException ex) {
            throw new DataUnitException(ex);
        }
    }

    // MetadataDataUnit interface
    @Override
    public Set<IRI> getMetadataGraphnames() throws DataUnitException {
        return readContexts;
    }

    //WritableMetadataDataUnit
    @Override
    public IRI getMetadataWriteGraphname() throws DataUnitException {
        return writeContext;
    }

    //WritableMetadataDataUnit
    @Override
    public void addEntry(final String symbolicName) throws DataUnitException {

        final IRI entrySubject = creatEntitySubject();
        try {
            faultTolerant.execute(new FaultTolerant.Code() {

                @Override
                public void execute(RepositoryConnection connection) throws RepositoryException, DataUnitException {
                    addEntry(entrySubject, symbolicName, connection);
                }
            });
        } catch (RepositoryException ex) {
            throw new DataUnitException("Error when adding entry.", ex);
        }
    }

    //ManagableDataUnit interface
    @Override
    public String getName() {
        return dataUnitName;
    }

    //ManagableDataUnit interface
    @Override
    public void clear() throws DataUnitException {
        /**
         * Beware! Clean is called from different thread then all other operations (pipeline executor thread).
         * That is the reason why we cannot obtain connection using this.getConnection(), it would throw an
         * Exception. This connection has to be obtained directly from repository and we take care to close it
         * properly.
         * No issues, getConnection() returns new connection
         */
        RepositoryConnection connection = null;

        try {
            connection = this.getConnection();
            connection.begin();

            // Delete graph with entries.
            connection.clear(writeContext);
            // Delete records from Ontology.GRAPH_METADATA graph.
            Update query;
            try {
                query = connection.prepareUpdate(QueryLanguage.SPARQL, CLEAR_QUERY);
            } catch (MalformedQueryException ex) {
                throw new DataUnitException(ex);
            }
            query.setBinding(WRITE_CONTEXT_BINDING, writeContext);
            try {
                query.execute();
            } catch (UpdateExecutionException ex) {
                throw new DataUnitException(ex);
            }
            connection.commit();

        } catch (RepositoryException ex) {
            try {
                connection.rollback();
            } catch (RepositoryException e) {
                throw new DataUnitException(e);
            }
            throw new DataUnitException("Could not clear metadata.", ex);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (RepositoryException ex) {
                LOG.warn("Error when closing connection.", ex);
            }
        }
    }

    //ManagableDataUnit interface
    @Override
    public void checkConsistency() throws DataUnitException {
        try {
            checkForDuplicitEntries(readContexts);
        } catch (DataUnitException ex) {
            throw new DataUnitException("Inconsistent state of data unit:" + dataUnitName, ex);
        }
        // Check for open connections.
        closeOpenedConnection();
    }

    //ManagableDataUnit interface
    @Override
    public void release() {
        // Just close the connections.
        closeOpenedConnection();
    }

    //ManagableDataUnit interface
    @Override
    public void merge(DataUnit otherDataUnit) throws IllegalArgumentException, DataUnitException {
        if (!this.getClass().equals(otherDataUnit.getClass())) {
            throw new IllegalArgumentException("Incompatible DataUnit class. This DataUnit is of class " + this.getClass().getCanonicalName() + " and it cannot merge other DataUnit of class " + otherDataUnit.getClass().getCanonicalName() + ".");
        }
        final AbstractWritableMetadataDataUnit otherMetadata = (AbstractWritableMetadataDataUnit) otherDataUnit;
        // What we need to do is just to replicate all symbolic names.
        final Set<IRI> newReadSet = new HashSet<>(this.readContexts.size() + otherMetadata.readContexts.size());
        newReadSet.addAll(this.readContexts);
        newReadSet.addAll(otherMetadata.readContexts);
        checkForDuplicitEntries(newReadSet);
        // Merge read contexts.
        this.readContexts.addAll(otherMetadata.getMetadataGraphnames());
    }

    //ManagableDataUnit interface
    @Override
    public void store() throws DataUnitException {
        // Write context - read and write.
        try {
            faultTolerant.execute(new FaultTolerant.Code() {

                @Override
                public void execute(RepositoryConnection connection) throws RepositoryException, DataUnitException {
                    final ValueFactory valueFactory = connection.getValueFactory();
                    for (IRI context : readContexts) {
                        connection.add(
                                writeContext,
                                valueFactory.createIRI(Ontology.PREDICATE_METADATA_CONTEXT_READ),
                                context,
                                valueFactory.createIRI(Ontology.GRAPH_METADATA));
                    }
                    // Just one write context.
                    connection.add(
                            writeContext,
                            valueFactory.createIRI(Ontology.PREDICATE_METADATA_CONTEXT_WRITE),
                            writeContext,
                            valueFactory.createIRI(Ontology.GRAPH_METADATA));
                    // And entry counter.
                    connection.add(
                            writeContext,
                            valueFactory.createIRI(Ontology.PREDICATE_METADATA_ENTRY_COUNTER),
                            valueFactory.createLiteral(entryCounter.intValue()),
                            valueFactory.createIRI(Ontology.GRAPH_METADATA));
                }
            });
        } catch (RepositoryException ex) {
            throw new DataUnitException("Could not store metadata", ex);
        }
    }

    //ManagableDataUnit interface
    @Override
    public void load() throws DataUnitException {
        // Read context - read and write.
        final IRI metadataSourceGraph = getMetadataWriteGraphname();
        try {
            faultTolerant.execute(new FaultTolerant.Code() {

                @Override
                public void execute(RepositoryConnection connection) throws RepositoryException, DataUnitException {
                    // Clear current structures.
                    readContexts.clear();
                    // Load.
                    final ValueFactory valueFactory = connection.getValueFactory();
                    final RepositoryResult<Statement> result = connection.getStatements(
                            metadataSourceGraph,
                            valueFactory.createIRI(Ontology.PREDICATE_METADATA_CONTEXT_READ),
                            null,
                            false,
                            valueFactory.createIRI(Ontology.GRAPH_METADATA));
                    while (result.hasNext()) {
                        Statement contextStatement = result.next();
                        readContexts.add(valueFactory.createIRI(contextStatement.getObject().stringValue()));
                    }
                    if (result != null) {
                        result.close();
                    }
                    // Get read context - this a little bit strange as we already use this context
                    // to read this data, but nothing bad should happen.
                    final Value writeContextValue = getSingleObject(connection,
                            metadataSourceGraph,
                            valueFactory.createIRI(Ontology.PREDICATE_METADATA_CONTEXT_WRITE),
                            valueFactory.createIRI(Ontology.GRAPH_METADATA));
                    if (writeContextValue instanceof IRI) {
                        writeContext = (IRI) writeContextValue;
                    } else {
                        throw new DataUnitException("Write context must be a IRI!");
                    }
                    // Get entry counter.
                    final Value entryValue = getSingleObject(connection,
                            metadataSourceGraph,
                            valueFactory.createIRI(Ontology.PREDICATE_METADATA_ENTRY_COUNTER),
                            valueFactory.createIRI(Ontology.GRAPH_METADATA));
                    if (entryValue instanceof Literal) {
                        final Literal entryLiteral = (Literal) entryValue;
                        try {
                            entryCounter.set(entryLiteral.intValue());
                        } catch (NumberFormatException ex) {
                            throw new DataUnitException("Entry counter must be an integer!", ex);
                        }
                    } else {
                        throw new DataUnitException("Entry counter must be an integer!");
                    }
                }
            });
        } catch (RepositoryException ex) {
            throw new DataUnitException("Could not load metadata", ex);
        }
    }

    /**
     * @return New unique IRI for an entry.
     */
    protected IRI creatEntitySubject() {
        return connectionSource.getValueFactory().createIRI(
                writeContext
                        + "/entry/"
                        + Integer.toString(entryCounter.incrementAndGet()));
    }

    /**
     * Create a new entry.
     * 
     * @param entrySubject
     * @param symbolicName
     * @param connection
     *            Connection used to add data. Will not be closed.
     * @return Subject of the new entry.
     * @throws org.openrdf.repository.RepositoryException
     * @throws DataUnitException
     */
    protected IRI addEntry(IRI entrySubject, String symbolicName, RepositoryConnection connection) throws RepositoryException, DataUnitException {
        final ValueFactory valueFactory = connection.getValueFactory();
        connection.add(entrySubject,
                valueFactory.createIRI(MetadataDataUnit.PREDICATE_SYMBOLIC_NAME),
                valueFactory.createLiteral(symbolicName),
                getMetadataWriteGraphname());
        return entrySubject;
    }

    /**
     * Close connection that were not closed by the data unit user.
     */
    private void closeOpenedConnection() {
        int count = 0;
        for (RepositoryConnection connection : requestedConnections) {
            try {
                if (connection.isOpen()) {
                    count++;
                    //LOG.error("Connection: is not closed connection opened on:\n{}", requestedConnections.get(connection));
                    try {
                        connection.close();
                        LOG.debug("Connection {} was closed automatically.", connection);
                    } catch (RepositoryException ex1) {
                        LOG.warn("Error when closing connection", ex1);
                    }

                }
            } catch (RepositoryException ex) {
                try {
                    connection.close();
                } catch (RepositoryException ex1) {
                    LOG.warn("Error when closing connection", ex1);
                }
            }
        }

        if (count > 0) {
            LOG.info("{} connections remained opened after DPU execution, dataUnitName '{}'. They were closed automatically after the DPU's execution.", count, this.getName());
        }
    }

    /**
     * Check for duplicity for entry in given graphs.
     * 
     * @param graphs
     */
    private void checkForDuplicitEntries(Set<IRI> graphs) throws DataUnitException {
        // Prepare query.
        final StringBuilder fromClause = new StringBuilder(graphs.size() * 15);
        for (IRI graph : graphs) {
            fromClause.append("FROM <");
            fromClause.append(graph.stringValue());
            fromClause.append(">");
        }
        final String queryWithGraphs = String.format(DUPLICATE_ENTRIES_QUERY, fromClause.toString());
        // Ask query and log result.
        try {
            faultTolerant.execute(new FaultTolerant.Code() {

                @Override
                public void execute(RepositoryConnection connection) throws RepositoryException, DataUnitException {
                    boolean containDuplicity = false;
                    // prepare query.
                    TupleQuery duplicateEntries;
                    try {
                        duplicateEntries = connection.prepareTupleQuery(QueryLanguage.SPARQL, queryWithGraphs);
                    } catch (MalformedQueryException ex) {
                        throw new DataUnitException(ex);
                    }
                    // Execute query.
                    TupleQueryResult result = null;
                    try {
                        result = duplicateEntries.evaluate();
                        while (result.hasNext()) {
                            final BindingSet bindingSet = result.next();
                            final String symbolicName = bindingSet.getValue(SYMBOLIC_NAME_BINDING).stringValue();
                            containDuplicity = true;
                            LOG.error("Duplicate entry found for symbolic name: {}", symbolicName);
                        }
                    } catch (QueryEvaluationException ex) {
                        throw new DataUnitException(ex);
                    } finally {
                        if (result != null) {
                            try {
                                result.close();
                            } catch (QueryEvaluationException ex) {
                                LOG.warn("Error in close", ex);
                            }
                        }
                    }
                    // Throw an exception?
                    if (containDuplicity) {
                        throw new DataUnitException("Duplicate symbolic names found, see logs for more details.");
                    }
                }
            });
        } catch (RepositoryException ex) {
            throw new DataUnitException("Can't check for duplicities.", ex);
        }
    }

    /**
     * Read and return value of a single object.
     * 
     * @param connection
     * @param subject
     * @param predicate
     * @param graph
     * @return Result of given query.
     * @throws RepositoryException
     * @throws DataUnitException
     *             In case that there is more than one object.
     */
    private Value getSingleObject(RepositoryConnection connection, IRI subject, IRI predicate, IRI graph)
            throws RepositoryException, DataUnitException {
        final RepositoryResult<Statement> result = connection.getStatements(
                subject,
                predicate,
                null,
                false,
                graph);
        // Get the first.
        if (result.hasNext()) {
            Value object = result.next().getObject();
            if (result.hasNext()) {
                // More then one record.
                throw new DataUnitException("Multiple matches in graph <" + graph.stringValue() +
                        "> for <" + subject.stringValue() + "> <" + predicate.stringValue() + "> ?o");
            }
            return object;
        }
        if (result != null) {
            result.close();
        }
        throw new DataUnitException("No match in graph <" + graph.stringValue() +
                "> for <" + subject.stringValue() + "> <" + predicate.stringValue() + "> ?o");
    }

    @Override
    public void setConsumedByMultipleInputs(boolean status) {
        this.consumedByMultipleInputs = status;
    }

    @Override
    public boolean isConsumedByMultipleInputs() {
        return consumedByMultipleInputs;
    }

}
