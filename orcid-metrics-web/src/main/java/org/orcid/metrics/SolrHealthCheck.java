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

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.SolrPingResponse;
import org.apache.solr.common.SolrException;
import org.springframework.beans.factory.InitializingBean;

import com.yammer.metrics.HealthChecks;
import com.yammer.metrics.core.HealthCheck;

public class SolrHealthCheck extends HealthCheck implements InitializingBean {

    private SolrServer solrServer;

    public SolrHealthCheck(SolrServer solrServer) {
        super("solr");
        this.solrServer = solrServer;
    }

    @Override
    protected Result check() throws Exception {
        try {
            SolrPingResponse response = solrServer.ping();
            int status = response.getStatus();
            if (status == 0) {
                //GraphiteReporter.enable(period, unit, host, port)
                return Result.healthy();
            }
        } catch (SolrException e) {
            return Result.unhealthy("Exception pinging solr: " + e.getMessage());
        }

        return Result.unhealthy("Solr returned a bad status to a ping request");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        HealthChecks.register(this);
    }

}
