package org.orcid.core.manager.impl;

import javax.annotation.Resource;

import org.orcid.core.manager.StatisticManager;
import org.orcid.persistence.dao.StatisticsDao;
import org.springframework.transaction.annotation.Transactional;

public class StatisticManagerImpl implements StatisticManager {

    @Resource 
    StatisticsDao statisticDao;
    
    @Override
    @Transactional
    public long createHistory() {
        return statisticDao.createHistory();
    }

    @Override
    @Transactional
    public boolean saveStatistic(long id, String name, double value) {
        return statisticDao.saveStatistic(id, name, value);
    }

    @Override
    public double getStatistic(long id, String name) {
        // TODO Auto-generated method stub
        return 0;
    }

}
