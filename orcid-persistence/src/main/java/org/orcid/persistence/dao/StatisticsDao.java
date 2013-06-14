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
package org.orcid.persistence.dao;

import java.util.List;

import org.orcid.persistence.jpa.entities.StatisticValuesEntity;
import org.orcid.persistence.jpa.entities.StatisticKeyEntity;

public interface StatisticsDao {
    /**
     * Creates a new statistics key
     * 
     * @return the statistic key object
     * */
    public StatisticKeyEntity createKey();

    /**
     * Get the latest statistics key
     * 
     * @return the latest statistics key
     * */
    public StatisticKeyEntity getLatestKey();

    /**
     * Save an statistics record on database
     * 
     * @param id
     * @param name
     *            the name of the statistic
     * @param value
     *            the statistic value
     * @return the statistic value object
     * */
    public StatisticValuesEntity saveStatistic(StatisticValuesEntity statistic);

    /**
     * Get an statistics object from database
     * 
     * @param id
     * @return the Statistic value object associated with the id
     * */
    public List<StatisticValuesEntity> getStatistic(long id);

    /**
     * Get an statistics object from database
     * 
     * @param id
     * @param name
     * @return the Statistic value object associated with the id and name
     *         parameters
     * */
    public StatisticValuesEntity getStatistic(long id, String name);
}
