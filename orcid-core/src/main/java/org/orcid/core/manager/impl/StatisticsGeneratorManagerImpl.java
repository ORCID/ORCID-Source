package org.orcid.core.manager.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.orcid.core.manager.StatisticsGeneratorManager;
import org.orcid.core.utils.statistics.StatisticsEnum;
import org.orcid.persistence.dao.StatisticsDao;

public class StatisticsGeneratorManagerImpl implements StatisticsGeneratorManager {

    @Resource
    private StatisticsDao statisticsGeneratorDao;

    @Override
    public Map<String, Long> generateStatistics() {        
        Map<String, Long> statistics = new HashMap<String, Long>();        
        statistics.put(StatisticsEnum.KEY_LIVE_IDS.value(), statisticsGeneratorDao.getLiveIds());        
        return statistics;        
    }

}
