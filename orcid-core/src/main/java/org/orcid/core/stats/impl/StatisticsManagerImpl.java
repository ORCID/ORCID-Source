package org.orcid.core.stats.impl;

import java.text.NumberFormat;
import java.util.Locale;

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
    private TransactionTemplate transactionTemplate;

    @Resource
    private StatisticsDao statisticsDao;

    @Override
    public void generateStatistics() {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                LOG.info("Generating latests statistics");
                Long liveIds = statisticsDao.calculateLiveIds();
                Long keyId = statisticsDao.createKey();
                statisticsDao.persist(new StatisticValuesEntity(keyId, StatisticsEnum.KEY_LIVE_IDS.value(), liveIds));
                LOG.info("Stats successfully processed");
            }
        });
    }

    @Override
    @Cacheable(value = "live-ids", key = "#locale")
    public String getFormattedLiveIds(Locale locale) {
        Long liveIds = statisticsDao.getLatestLiveIds();
        NumberFormat nf = NumberFormat.getInstance(locale);
        return nf.format(liveIds);
    }
}
