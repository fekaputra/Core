package cz.cuni.mff.xrg.odcs.commons.app.execution.server;

import java.util.List;

import javax.persistence.TypedQuery;

import org.springframework.transaction.annotation.Transactional;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccessBase;

public class DbExecutionServerImpl extends DbAccessBase<ExecutionServer>implements DbExecutionServer {

    public DbExecutionServerImpl() {
        super(ExecutionServer.class);
    }

    @Override
    public ExecutionServer getExecutionServer(String backendId) {
        final String stringQuery = "SELECT e FROM ExecutionServer e WHERE e.backendId = :backendId";
        TypedQuery<ExecutionServer> query = createTypedQuery(stringQuery);
        query.setParameter("backendId", backendId);
        return execute(query);
    }

    @Override
    public List<ExecutionServer> getAllExecutionServers() {
        final String queryStr = "SELECT e FROM ExecutionServer e";
        return executeList(queryStr);
    }

    @Override
    @Transactional
    public int allocateQueuedExecutionsForBackendByPriority(String backendID, int limit) {
        final String queryStr = "UPDATE exec_pipeline SET backend_id = '%s'"
                + " WHERE id IN (SELECT e.id from exec_pipeline e WHERE e.backend_id IS NULL AND e.status = %d"
                + " ORDER BY e.order_number ASC, e.id ASC LIMIT %d FOR UPDATE)";
        String query = String.format(queryStr,
                backendID,
                0, // = QUEUED
                limit);
        return this.em.createNativeQuery(query).executeUpdate();
    }

}
