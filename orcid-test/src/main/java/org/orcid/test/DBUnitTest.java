/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.AmbiguousTableNameException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.dbunit.util.fileloader.FlatXmlDataFileLoader;
import org.junit.Ignore;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 * Base class for testing using DBUnit.
 * 
 * @author Andrew Walters
 * @modified Declan Newman
 * 
 */

@Ignore
public class DBUnitTest {

    private static final String CONTEXT = "/orcid-persistence-context.xml";
    private static final String[] tables = new String[] { "security_question", "profile", "orcid_social", "profile_event", "work", "researcher_url",
            "given_permission_to", "external_identifier", "email", "email_event", "biography", "record_name", "other_name", "profile_keyword", "profile_patent", "org_disambiguated",
            "org_disambiguated_external_identifier", "org", "org_affiliation_relation", "profile_funding", "funding_external_identifier", "address", "institution",
            "affiliation", "notification", "client_details", "client_secret", "oauth2_token_detail", "custom_email", "webhook", "granted_authority", "orcid_props",
            "peer_review", "peer_review_subject", "shibboleth_account", "group_id_record"};

    private static ApplicationContext context = new ClassPathXmlApplicationContext(CONTEXT);

    public static void initDBUnitData(List<String> flatXMLDataFiles) throws Exception {
        IDatabaseConnection connection = getDBConnection();
        cleanClientSourcedProfiles(connection);
        cleanAll(connection);
        for (String flatXMLDataFile : flatXMLDataFiles) {
            DatabaseOperation.INSERT.execute(connection, getDataSet(flatXMLDataFile));
        }
        connection.close();
    }

    public static void removeDBUnitData(List<String> flatXMLDataFiles) throws Exception {
        IDatabaseConnection connection = getDBConnection();
        cleanClientSourcedProfiles(connection);
        cleanAll(connection);
        connection.close();
    }

    private static void cleanClientSourcedProfiles(IDatabaseConnection connection) throws AmbiguousTableNameException, DatabaseUnitException, SQLException {
        QueryDataSet dataSet = new QueryDataSet(connection);
        dataSet.addTable(
                "profile",
                "SELECT p1.* FROM profile p1 LEFT JOIN client_details c ON c.group_orcid = p1.orcid LEFT JOIN profile p2 ON p1.source_id = p2.source_id WHERE p2.source_id IS NULL AND (c.client_details_id IS NULL OR p1.client_source_id IS NOT NULL)");        
        dataSet.addTable("other_name");
        dataSet.addTable("record_name");
        dataSet.addTable("biography");
        dataSet.addTable("profile_keyword");
        dataSet.addTable("work");
        dataSet.addTable("profile_event");
        dataSet.addTable("researcher_url");
        dataSet.addTable("email");
        dataSet.addTable("email_event");
        dataSet.addTable("external_identifier");
        dataSet.addTable("org");
        dataSet.addTable("org_affiliation_relation");
        dataSet.addTable("peer_review_subject");
        dataSet.addTable("peer_review");
        dataSet.addTable("profile_funding");
        dataSet.addTable("funding_external_identifier");
        dataSet.addTable("webhook");
        dataSet.addTable("oauth2_token_detail");
        dataSet.addTable("notification");
        dataSet.addTable("notification_item");
        dataSet.addTable("given_permission_to");
        dataSet.addTable("subject");
        dataSet.addTable("shibboleth_account");
        dataSet.addTable("group_id_record");
        dataSet.addTable("address");
        DatabaseOperation.DELETE.execute(connection, dataSet);

        QueryDataSet theRest = new QueryDataSet(connection);
        theRest.addTable("profile", "SELECT * FROM profile WHERE source_id IS NOT NULL AND source_id != orcid ORDER BY orcid DESC");
        theRest.addTable("client_details");
        theRest.addTable("client_secret");
        theRest.addTable("custom_email");
        DatabaseOperation.DELETE.execute(connection, theRest);
    }

    private static void cleanAll(IDatabaseConnection connection) throws DatabaseUnitException, SQLException {
        QueryDataSet dataSet = new QueryDataSet(connection);
        for (String table : tables) {
            dataSet.addTable(table);
        }
        DatabaseOperation.DELETE.execute(connection, dataSet);
    }

    public static IDatabaseConnection getDBConnection() throws Exception {
        DriverManagerDataSource dataSource = (DriverManagerDataSource) context.getBean("simpleDataSource");
        Connection jdbcConnection = dataSource.getConnection();
        IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);
        connection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new CustomDataTypeFactory());
        return connection;
    }

    public static IDataSet getDataSet(String flatXMLDataFile) {
        FlatXmlDataFileLoader loader = new FlatXmlDataFileLoader();
        loader.getBuilder().setColumnSensing(true);
        IDataSet ds = loader.load(flatXMLDataFile);
        return ds;
    }

}