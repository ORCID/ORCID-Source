package org.orcid.test;

import static org.junit.Assert.fail;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.dbunit.util.fileloader.FlatXmlDataFileLoader;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.junit.Ignore;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ActiveProfiles;

/**
 * Base class for testing using DBUnit.
 * 
 * @author Andrew Walters
 * @modified Declan Newman
 * 
 */

@Ignore
@ActiveProfiles("unitTests")
public class DBUnitTest {

    private static final String TEST_CORE_CONTEXT = "classpath:test-core-context.xml";
    private static final String TEST_DB_CONTEXT = "classpath:test-db-context.xml";

    /**
     * Every table the fixtures write to. This is the union of the two sweeps this class used to
     * run: the general one, and the "client sourced profiles" one that existed only to delete child
     * rows and client-sourced profiles ahead of it so that foreign keys were satisfied on the way
     * down. Referential integrity is switched off for the duration of the sweep below, so that
     * ordering no longer matters and the two collapse into one pass.
     *
     * Reference data seeded by Liquibase is deliberately absent -- identifier types and the like are
     * asserted by tests such as IdentifierTypeManagerTest and must survive the reset.
     */
    private static final String[] TABLES_TO_CLEAR = new String[] { "profile", "orcid_social", "profile_event", "work", "researcher_url",
            "given_permission_to", "external_identifier", "email", "email_domain", "email_event", "biography", "record_name", "other_name", "profile_keyword", "profile_patent",
            "org_disambiguated", "org_disambiguated_external_identifier", "org", "org_affiliation_relation", "profile_funding", "funding_external_identifier", "address",
            "institution", "affiliation", "notification", "client_details", "client_secret", "oauth2_token_detail", "custom_email", "webhook", "granted_authority",
            "orcid_props", "peer_review", "peer_review_subject", "shibboleth_account", "group_id_record", "invalid_record_data_changes",
            "research_resource", "research_resource_item", "spam", "backup_code", "profile_history_event", "event",
            "research_resource_item_org", "research_resource_org", "profile_email_domain", "notification_item", "subject", "email_frequency",
            "find_my_stuff_history",
            // Rows the old sweep never named because ON DELETE CASCADE removed them when
            // client_details or the authorisation code went. Switching referential integrity off
            // switches the cascades off with it, so they have to be listed. Being explicit is worth
            // more than the brevity: the list now states exactly what a reset clears.
            "client_authorised_grant_type", "client_granted_authority", "client_redirect_uri", "client_resource_id", "client_scope",
            "oauth2_authoriziation_code_detail", "orcidoauth2authoriziationcodedetail_authorities",
            "orcidoauth2authoriziationcodedetail_resourceids", "orcidoauth2authoriziationcodedetail_scopes" };

    private static ApplicationContext context;

    static {
        String ehcacheTestDir = System.getProperty("java.io.tmpdir") + File.separator + "ehcache-tests" + File.separator + UUID.randomUUID();
        System.setProperty("org.orcid.ehcache.dir", ehcacheTestDir);
        System.setProperty("spring.profiles.active", "unitTests");

        try {
            context = loadContext(TEST_CORE_CONTEXT);
        } catch (Exception e) {
            try {
                context = loadContext(TEST_DB_CONTEXT);
            } catch (Exception e2) {
                System.out.println("Initial error loading " + TEST_CORE_CONTEXT + ": ");
                e.printStackTrace();
                System.out.println("\nSecond error loading " + TEST_DB_CONTEXT + ": ");
                e2.printStackTrace();
                fail("Failed to load Spring Application Context for DBUnitTest");
            }
        }
    }

    private static ApplicationContext loadContext(String contextPath) {
        GenericXmlApplicationContext ctx = new GenericXmlApplicationContext();
        ctx.getEnvironment().setActiveProfiles("unitTests");
        ctx.load(contextPath);
        ctx.refresh();
        return ctx;
    }

    public static void initDBUnitData(List<String> flatXMLDataFiles) throws Exception {
        clearCacheManagers();
        IDatabaseConnection connection = getDBConnection();
        cleanAll(connection);
        for (String flatXMLDataFile : flatXMLDataFiles) {
            DatabaseOperation.INSERT.execute(connection, getDataSet(flatXMLDataFile));
        }
        connection.close();
    }

    public static void removeDBUnitData(List<String> flatXMLDataFiles) throws Exception {
        IDatabaseConnection connection = getDBConnection();
        cleanAll(connection);
        connection.close();
    }

    private static void clearCacheManagers() {
        try {
            JCacheCacheManager springCoreCacheManager = (JCacheCacheManager) context.getBean("springCoreCacheManager");
            if (springCoreCacheManager != null) {
                clearCaches(springCoreCacheManager);
            }
            CacheManager coreCacheManager = (CacheManager) context.getBean("coreCacheManager");
            if (coreCacheManager != null) {
                clearCaches(coreCacheManager);
            }
        } catch (NoSuchBeanDefinitionException e) {
            // do nothing
        }
    }

    private static void clearCaches(JCacheCacheManager springCoreCacheManager) {
        for (String cacheName: springCoreCacheManager.getCacheNames()) {
            org.springframework.cache.Cache cache = springCoreCacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
            }
        }
    }

    private static void clearCaches(CacheManager cacheManager) {
        cacheManager.getRuntimeConfiguration().getCacheConfigurations().forEach((alias, config) -> {
            Cache<?, ?> cache = cacheManager.getCache(alias, config.getKeyType(), config.getValueType());
            if (cache != null) {
                cache.clear(); 
            }
        });
    }

    /**
     * Empties every fixture table.
     *
     * This used to run two DBUnit DELETE sweeps, one ordered child-first so foreign keys held as it
     * went. DBUnit implements a DELETE over a QueryDataSet by issuing SELECT * for each table,
     * pulling every row into memory, reading the primary key from JDBC metadata, and then deleting
     * one row at a time; across 86 table visits, twice per test class, that was about 1.2s per class
     * and roughly a quarter of the whole database stage.
     *
     * A batch of DELETE statements does the same job in one round trip each. The ordering the old
     * code preserved is what referential integrity requires, so this switches it off for the
     * duration rather than trying to reproduce it -- profile carries two self-referential foreign
     * keys (sponsor_id and deprecating_admin), both non-deferrable, which is why the old sweep
     * needed an explicit ORDER BY orcid DESC to delete profiles safely at all.
     *
     * HSQLDB specific, which is what these tests run on (jdbc:hsqldb:mem:orcid). On any other engine
     * the SET DATABASE statement fails loudly on the first test rather than degrading quietly.
     */
    private static void cleanAll(IDatabaseConnection connection) throws SQLException {
        Connection jdbcConnection = connection.getConnection();
        try (Statement statement = jdbcConnection.createStatement()) {
            statement.execute("SET DATABASE REFERENTIAL INTEGRITY FALSE");
            try {
                for (String table : TABLES_TO_CLEAR) {
                    statement.addBatch("DELETE FROM " + table);
                }
                statement.executeBatch();
            } finally {
                // Restore it even if a delete fails: leaving integrity off would let a later test
                // write rows the schema forbids and still pass.
                statement.execute("SET DATABASE REFERENTIAL INTEGRITY TRUE");
            }
        }
    }

    public static IDatabaseConnection getDBConnection() throws Exception {
        DriverManagerDataSource dataSource = (DriverManagerDataSource) context.getBean("simpleDataSource");
        Connection jdbcConnection = dataSource.getConnection();
        IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);
        connection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new CustomDataTypeFactory());
        connection.getConfig().setProperty(DatabaseConfig.FEATURE_ALLOW_EMPTY_FIELDS, true);
        return connection;
    }

    public static IDataSet getDataSet(String flatXMLDataFile) {
        FlatXmlDataFileLoader loader = new FlatXmlDataFileLoader();
        loader.getBuilder().setColumnSensing(true);
        IDataSet ds = loader.load(flatXMLDataFile);
        return ds;
    }

}