package org.orcid.core.analytics.client;

import org.orcid.core.analytics.AnalyticsData;

public interface AnalyticsClient {

    void sendAnalyticsData(AnalyticsData data);
}
