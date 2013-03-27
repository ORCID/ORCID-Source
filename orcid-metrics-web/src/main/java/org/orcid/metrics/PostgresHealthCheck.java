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
package org.orcid.metrics;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.commons.dbutils.DbUtils;
import org.springframework.beans.factory.InitializingBean;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.yammer.metrics.HealthChecks;
import com.yammer.metrics.core.HealthCheck;

/**
 * 
 * @author jamesb
 * 
 */
public class PostgresHealthCheck extends HealthCheck implements InitializingBean {

    private ComboPooledDataSource dataSource;

    public PostgresHealthCheck(ComboPooledDataSource dataSource) {
        super("Postgres Health Check");
        this.dataSource = dataSource;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        HealthChecks.register(this);

    }

    @Override
    protected Result check() throws Exception {
        Connection testConn = null;
        ResultSet rs = null;
        try {
            String testQuery = dataSource.getPreferredTestQuery();
            testConn = dataSource.getConnection(dataSource.getUser(), dataSource.getPassword());
            PreparedStatement ps = testConn.prepareStatement(testQuery);
            rs = ps.executeQuery();

            if (rs.next() && rs.getInt(1) == 1) {
                return Result.healthy();
            }

            return Result.unhealthy("Couldn't connect to Postgres");
        }

        finally {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(testConn);
        }

    }

}
