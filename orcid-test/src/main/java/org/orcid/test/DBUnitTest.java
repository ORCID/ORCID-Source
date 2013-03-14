/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
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

import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.ext.hsqldb.HsqldbDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.dbunit.util.fileloader.DataFileLoader;
import org.dbunit.util.fileloader.FlatXmlDataFileLoader;
import org.junit.Ignore;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.Connection;
import java.util.List;

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

    public static void initDBUnitData(List<String> flatXMLDataFiles, String primaryKeyFilter) throws Exception {

        IDatabaseConnection connection = getDBConnection();

        // Set the property by passing the new IColumnFilter
        if (primaryKeyFilter != null && primaryKeyFilter.length() > 0) {
            connection.getConfig().setProperty(DatabaseConfig.PROPERTY_PRIMARY_KEY_FILTER, new NullPrimaryKeyFilter(primaryKeyFilter));
        }

        connection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new HsqldbDataTypeFactory());
        for (String flatXMLDataFile : flatXMLDataFiles) {
            DatabaseOperation.CLEAN_INSERT.execute(connection, getDataSet(flatXMLDataFile));
        }
        connection.close();
    }

    public static void removeDBUnitData(List<String> flatXMLDataFiles, String primaryKeyFilter) throws Exception {

        IDatabaseConnection connection = getDBConnection();

        // System.out.println(connection.getConfig().getProperty("http://www.dbunit.org/properties/primaryKeyFilter"));
        // Set the primaryKeyFilter property by passing the new IColumnFilter
        if (primaryKeyFilter != null && primaryKeyFilter.length() > 0) {
            connection.getConfig().setProperty(DatabaseConfig.PROPERTY_PRIMARY_KEY_FILTER, new NullPrimaryKeyFilter(primaryKeyFilter));
        }
        connection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new HsqldbDataTypeFactory());

        for (String flatXMLDataFile : flatXMLDataFiles) {
            DatabaseOperation.DELETE.execute(connection, getDataSet(flatXMLDataFile));
        }
        connection.close();
    }

    public static IDatabaseConnection getDBConnection() throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext(CONTEXT);

        DriverManagerDataSource dataSource = (DriverManagerDataSource) context.getBean("simpleDataSource");

        Connection jdbcConnection = dataSource.getConnection();
        IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);

        return connection;
    }

    public static IDataSet getDataSet(String flatXMLDataFile) {

        FlatXmlDataFileLoader loader = new FlatXmlDataFileLoader();
        loader.getBuilder().setColumnSensing(true);
        IDataSet ds = loader.load(flatXMLDataFile);
        return ds;
    }
}