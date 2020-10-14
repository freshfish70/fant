package no.traeen.startup;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/* 
Test connection to the database and prints details about the connection
when the application has started.
 */
@ApplicationScoped
public class JdbDatabaseVerifier {

    @Resource(lookup = "jdbc/postgresql")
    private DataSource dataSource;

    public void init(@Observes @Initialized(ApplicationScoped.class) Object init) throws SQLException {
        System.out.println("Verifying connection to PostgreSQL");
        DatabaseMetaData metaData = dataSource.getConnection().getMetaData();
        System.out.println(metaData.getDatabaseProductName());
        System.out.println("Version: " + metaData.getDatabaseMajorVersion() + "." + metaData.getDatabaseMinorVersion());
    }
}