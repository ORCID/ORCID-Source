package org.orcid.api.filter;

import javax.ws.rs.ext.Provider;

import org.orcid.api.common.filter.AnalyticsFilter;

@Provider
public class PublicApiAnalyticsFilter extends AnalyticsFilter {

    @Override
    public void setIsPublicApi() {
        this.isPublicApi = false;      
    }

}
