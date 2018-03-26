package org.orcid.persistence.liquibase;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import liquibase.change.custom.CustomTaskChange;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CustomChangeException;
import liquibase.exception.DatabaseException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;

/**
 * 
 * @author Will Simpson
 *
 */
public class AdministrativeChangesOptionChangeTask implements CustomTaskChange {

    private static final String UPDATE_SQL = "UPDATE profile set send_administrative_change_notifications = send_change_notifications WHERE orcid = ?";
    private static final String SELECT_SQL = "SELECT orcid FROM profile WHERE send_administrative_change_notifications IS NULL AND send_change_notifications IS NOT NULL LIMIT 1000";
    private static final Logger LOGGER = LoggerFactory.getLogger(AdministrativeChangesOptionChangeTask.class);

    @Override
    public String getConfirmationMessage() {
        return "Populated administrative changes option";
    }

    @Override
    public void setUp() throws SetupException {

    }

    @Override
    public void setFileOpener(ResourceAccessor resourceAccessor) {

    }

    @Override
    public ValidationErrors validate(Database database) {
        return null;
    }

    @Override
    public void execute(Database database) throws CustomChangeException {
        LOGGER.info("Running...");
        final JdbcConnection conn = (JdbcConnection) database.getConnection();
        try (PreparedStatement selectStatement = conn
                .prepareStatement(SELECT_SQL);
                PreparedStatement updateStatement = conn
.prepareStatement(UPDATE_SQL)) {
            boolean done = false;
            conn.setAutoCommit(false);
            while (!done) {
                LOGGER.info("Getting next batch...");
                done = true;
                ResultSet resultsSet = selectStatement.executeQuery();
                while (resultsSet.next()) {
                    done = false;
                    String orcid = resultsSet.getString(1);
                    LOGGER.debug("Processing orcid: {}", orcid);
                    updateStatement.setString(1, orcid);
                    updateStatement.addBatch();
                }
                updateStatement.executeBatch();
                conn.commit();
            }
        } catch (DatabaseException | SQLException e) {
            throw new CustomChangeException("Problem populating administrative changes option", e);
        }
    }
}
