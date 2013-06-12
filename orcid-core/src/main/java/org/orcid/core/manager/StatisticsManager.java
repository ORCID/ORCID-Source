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
package org.orcid.core.manager;

import java.util.List;

import org.orcid.persistence.jpa.entities.StatisticKeyEntity;
import org.orcid.persistence.jpa.entities.StatisticValuesEntity;

public interface StatisticsManager {
    //TODO
    public StatisticKeyEntity createKey();
    //TODO
    public StatisticValuesEntity saveStatistic(StatisticKeyEntity id, String name, long value);
    //TODO
    public StatisticValuesEntity getStatistic(StatisticKeyEntity id, String name);
    //TODO
    public List<StatisticValuesEntity> getLatestStatistics();
}
