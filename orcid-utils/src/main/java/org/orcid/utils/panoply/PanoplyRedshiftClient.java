package org.orcid.utils.panoply;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Date;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.orcid.utils.alerting.SlackManager;

@Repository
public class PanoplyRedshiftClient {

    private static final Logger LOG = LoggerFactory.getLogger(PanoplyRedshiftClient.class);

    @Autowired
    @Qualifier("panoplyJdbcTemplate")
    private JdbcTemplate panoplyJdbcTemplate;

    public int addPanoplyDeletedItem(PanoplyDeletedItem item) {
        LOG.debug("Adding deleted item to panoply DB: " + item.toString());
        String sql = "INSERT INTO dw_deleted_items (item_id, orcid, client_source_id, date_deleted, dw_table) VALUES (?, ?, ?, ?, ?)";
        return panoplyJdbcTemplate.update(sql, item.getItemId(), item.getOrcid(), item.getClientSourceId(), new java.sql.Timestamp(new Date().getTime()),
                item.getDwTable());
    }
    
    public int addPanoplyPapiDailyRateExceeded(PanoplyPapiDailyRateExceededItem item) {
        LOG.debug("Adding papi daily rate exceeded item to panoply DB: " + item.toString());
        String sql = "INSERT INTO dw_papi_daily_rate_exceeded (ip_address, orcid, client_id, email, request_date) VALUES (?, ?, ?, ?, ?)";
        return panoplyJdbcTemplate.update(sql, item.getIpAddress(), item.getOrcid(), item.getClientId(), item.getEmail(), item.getRequestDate());
    }
    
}
