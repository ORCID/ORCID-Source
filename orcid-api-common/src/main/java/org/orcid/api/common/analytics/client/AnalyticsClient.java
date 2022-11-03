package org.orcid.api.common.analytics.client;

import org.orcid.api.common.analytics.AnalyticsData;

public interface AnalyticsClient {

    void sendAnalyticsData(AnalyticsData data);
}
