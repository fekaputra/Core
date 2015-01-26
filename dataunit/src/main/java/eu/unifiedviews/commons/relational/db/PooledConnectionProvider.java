package eu.unifiedviews.commons.relational.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * {@link PooledConnectionProvider} provides connection to the underlying database
 * It uses Apache DBCP connections pool to serve database connections
 * Number of maximum pooled connections can be configured via {@link DatabaseWrapperConfigIF}
 * 
 * @author Tomas
 */
public class PooledConnectionProvider implements ConnectionProviderIF {

    private DataSource pooledDataSource;

    private ObjectPool<PoolableConnection> connectionPool;

    public PooledConnectionProvider(DatabaseWrapperConfigIF config) throws Exception {
        setupConnectionPool(config);
    }

    private void setupConnectionPool(DatabaseWrapperConfigIF config) throws Exception {
        String databaseURL = config.getDatabaseURL();

        try {
            Class.forName(config.getJDBCDriverName());
        } catch (ClassNotFoundException e) {
            throw new Exception("Failed to load database driver", e);
        }

        Properties props = new Properties();
        props.put("user", config.getUserName());
        props.put("password", config.getPassword());

        ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(databaseURL, props);

        PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, null);

        this.connectionPool =
                new GenericObjectPool<>(poolableConnectionFactory, createConnectionPoolConfig(config));

        poolableConnectionFactory.setPool(this.connectionPool);

        this.pooledDataSource = new PoolingDataSource<>(this.connectionPool);

    }

    private static GenericObjectPoolConfig createConnectionPoolConfig(DatabaseWrapperConfigIF config) {
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxTotal(config.getMaxPooledConnections());
        poolConfig.setMaxIdle(config.getMaxPooledConnections());
        poolConfig.setTestOnBorrow(true);

        return poolConfig;
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection conn = this.pooledDataSource.getConnection();
        conn.setAutoCommit(false);
        return conn;
    }

    @Override
    public void close() {
        try {
            this.connectionPool.close();
        } catch (Exception e) {
            // ignore
        }
    }

}
