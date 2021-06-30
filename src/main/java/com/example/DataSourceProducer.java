package com.example;

import javax.annotation.Resource;
import javax.annotation.sql.DataSourceDefinition;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.sql.DataSource;

@ApplicationScoped
@DataSourceDefinition(
    name = "java:app/jdbc/reproducer",
    // The app server (Payara) deploys a managed pool for this data source for us.
    // We don't need to deal with this on our own.
    //
    // HINT: PGSimpleDataSource would work too, but as we use a connection pool, go with a javax.sql.ConnectionPoolDataSource
    // HINT: PGXADataSource is unnecessary (no distributed transactions used) and breaks ingest.
    className = "org.postgresql.ds.PGConnectionPoolDataSource",
    
    // BEWARE: as this resource is created before defaults are read from META-INF/microprofile-config.properties,
    // defaults must be provided in this Payara-proprietary manner.
    user = "${MPCONFIG=db.user:user}",
    password = "${MPCONFIG=db.password}",
    url = "jdbc:postgresql://${MPCONFIG=db.host:localhost}:${MPCONFIG=db.port:5432}/${MPCONFIG=db.name:dvn}",
    
    // Set more options via MPCONFIG, including defaults where applicable.
    // TODO: Future versions of Payara (the one following 5.2021.4) will support setting
    //       integer properties like pool size, idle times, etc in a Payara-proprietary way.
    //       See https://github.com/payara/Payara/pull/5272
    properties = {
        // The following options are documented here:
        // https://docs.payara.fish/community/docs/documentation/payara-server/jdbc/advanced-connection-pool-properties.html
        "fish.payara.log-jdbc-calls=${MPCONFIG=db.log-jdbc-calls:false}"
    })
public class DataSourceProducer {
    @Resource(lookup = "java:app/jdbc/reproducer")
    DataSource ds;
    
    @Produces
    public DataSource getDatasource() {
        return ds;
    }
}
