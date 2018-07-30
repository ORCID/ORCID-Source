package org.orcid.core.manager;

import java.util.Map;

/**
 * 
 * @author Will Simpson
 *
 */
public interface StatusManager {

    String OVERALL_OK = "overallOk";
    String READ_ONLY_DB_CONNECTION_OK = "readOnlyDbConnectionOk";
    String DB_CONNECTION_OK = "dbConnectionOk";
    String TOMCAT_UP = "tomcatUp";

    Map<String, Boolean> createStatusMap();
    
}
