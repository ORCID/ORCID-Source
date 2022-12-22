package org.orcid.core.stats.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.orcid.core.stats.StatisticsManager;
import org.orcid.core.utils.statistics.StatisticsEnum;
import org.orcid.persistence.dao.StatisticsDao;
import org.orcid.statistics.jpa.entities.StatisticValuesEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

public class StatisticsManagerImpl implements StatisticsManager {

    private static final Logger LOG = LoggerFactory.getLogger(StatisticsManagerImpl.class);
    
    @Resource
    private TransactionTemplate statisticsTransactionTemplate;
    
    @Resource
    private StatisticsDao statisticsDao;
    
    @Override
    public void generateStatistics() {
        LOG.info("Generating latests statistics");
        Map<String, Long> stats = getLatestStatistics();
        LOG.info("Saving latests statistics");
        saveStatistics(stats);
        LOG.info("Stats successfully processed");
    }
    
    private Map<String, Long> getLatestStatistics() {        
        Map<String, Long> statistics = new HashMap<String, Long>();        
        statistics.put(StatisticsEnum.KEY_LIVE_IDS.value(), statisticsDao.calculateLiveIds());        
        return statistics;        
    }
    
    /**
     * Save a set of statistics to the database
     * 
     * @param statistics
     *          List of statistics to store
     * */
    
    private void saveStatistics(Map<String, Long> statistics) {        
        statisticsTransactionTemplate.execute(new TransactionCallbackWithoutResult() {            
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                Long keyId = statisticsDao.createKey();        
                // Store statistics on database
                for (Map.Entry<String, Long> entry : statistics.entrySet()) {            
                    StatisticValuesEntity newStat = new StatisticValuesEntity(keyId, entry.getKey(), entry.getValue());
                    statisticsDao.persist(newStat);
                }                
            }
        });                       
    }

    @Override
    @Cacheable(value = "delegates-by-receiver", key = "#receiverOrcid.concat('-').concat(#lastModified)")
    public long getLiveIds(Locale locale) {
        return statisticsDao.getLatestLiveIds();
    }
}
