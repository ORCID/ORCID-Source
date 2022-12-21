package org.orcid.persistence.dao;

public interface StatisticsGeneratorDao {

    public long calculateLiveIds();

    public long getLatestLiveIds();

}
