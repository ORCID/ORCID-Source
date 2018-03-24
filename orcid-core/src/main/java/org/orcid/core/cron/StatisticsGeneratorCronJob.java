package org.orcid.core.cron;

public interface StatisticsGeneratorCronJob {
    /**
     * Cron job that will generate statistics and store them on database 
     * */
    public void generateStatistics();
}
