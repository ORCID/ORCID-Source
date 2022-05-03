package org.orcid.core.cli;

import org.orcid.core.adapter.v3.converter.WorkContributorsConverter;
import org.orcid.core.utils.v3.ContributorUtils;
import org.orcid.jaxb.model.v3.release.common.Contributor;
import org.orcid.jaxb.model.v3.release.record.WorkContributors;
import org.orcid.persistence.dao.WorkDao;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.pojo.WorkSummaryExtended;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class FilterTopContributors {

    private WorkDao workDao;
    private WorkContributorsConverter workContributorsConverter;
    private static Logger logger = Logger.getLogger(FilterTopContributors.class.getName());
    private static final int BATCH_SIZE = 1000;
    private static final int MAX_CONTRIBUTORS_FOR_UI = 50;
    private static long workId = 0;
    private static String logRoute = null;

    /**
     * Batch filter top contributors from the auto-incrementing workId provided as a parameter up to the number defined in the class.
     *
     * @param {String} workId
     * @param {String} logRoute
     *
     * Examples:
     *      $ java -DworkId=0000 -DlogRoute=/route/to/store/logs FilterTopContributors.java
     */
    public static void main(String ...args) {
        FilterTopContributors filterTopContributors = new FilterTopContributors();
        filterTopContributors.validateParameters();
        filterTopContributors.initializeLog();
        filterTopContributors.filter();
    }

    private void filter() {
        init();
        List<Object[]> workEntityList = workDao.getWorksStartingFromWorkId(workId, BATCH_SIZE);
        workEntityList.forEach(this::filterTopContributors);
    }

    private void filterTopContributors(Object[] workObject) {
        WorkEntity workEntity = workDao.find(((BigInteger) workObject[0]).longValue());
        ContributorUtils contributorUtils = new ContributorUtils(0);
        WorkSummaryExtended wse = new WorkSummaryExtended.WorkSummaryExtendedBuilder(((BigInteger) workObject[0]))
                .contributors(workContributorsConverter.getContributorsList(isEmpty(workObject[1])))
                .build();
        List<Contributor> contributors = contributorUtils.filterTopContributors(wse.getContributors().getContributor(), MAX_CONTRIBUTORS_FOR_UI);
        try {
            workEntity.setTopContributorsJson(workContributorsConverter.convertTo(new WorkContributors(contributors), null));
            workDao.merge(workEntity);
            logger.info(workEntity.getId() + " was processed");
            workDao.flush();
        } catch (Exception e) {
            logger.info(workEntity.getId() + " could not be processed");
        }
    }

    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        workDao = (WorkDao) context.getBean("workDao");
        workContributorsConverter = (WorkContributorsConverter) context.getBean("workContributorsConverter");
    }

    private void validateParameters() {
        String workIdParameter = System.getProperty("workId");
        String logRouteParameter = System.getProperty("logRoute");

        if (workIdParameter == null || "".equals(workIdParameter)) {
            printMessageAndExit("Parameter workId is missing!.");
        } else if (logRouteParameter == null || "".equals(logRouteParameter)) {
            printMessageAndExit("Parameter logRoute is missing!.");
        }

        try {
            workId = Long.parseLong(workIdParameter);
        } catch (Exception e) {
            printMessageAndExit("Parameter workId must be a number!");
        }

        logRoute = logRouteParameter;
    }

    private void initializeLog() {
        FileHandler fh;
        SimpleDateFormat spf = new SimpleDateFormat("M-d_HHmm");

        try {
            fh = new FileHandler(logRoute + "/filterTopContributors-" + spf.format(Calendar.getInstance().getTime()) + ".log");
            logger.addHandler(fh);
            logger.setUseParentHandlers(false);
            fh.setFormatter(new SimpleFormatter());
        } catch (Exception e) {
            System.out.println("Parameter logRoute is invalid!");
        }
    }

    private void printMessageAndExit(String message) {
        System.out.println(message);
        System.exit(0);
    }

    private String isEmpty(Object o) {
        if (o != null) {
            return o.toString();
        }
        return null;
    }
}
