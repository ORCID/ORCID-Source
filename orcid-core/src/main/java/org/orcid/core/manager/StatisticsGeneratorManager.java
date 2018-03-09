package org.orcid.core.manager;

import java.util.Map;


public interface StatisticsGeneratorManager {

    public Map<String, Long> generateStatistics();
}
