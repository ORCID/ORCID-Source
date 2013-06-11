package org.orcid.core.manager;

public interface StatisticManager {
    public long createHistory();
    public boolean saveStatistic(long id, String name, double value);
    public double getStatistic(long id, String name);
}
