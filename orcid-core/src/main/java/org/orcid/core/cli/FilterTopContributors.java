package org.orcid.core.cli;

import org.orcid.core.adapter.v3.converter.ContributorsRolesAndSequencesConverter;
import org.orcid.core.adapter.v3.converter.WorkContributorsConverter;
import org.orcid.core.utils.v3.ContributorUtils;
import org.orcid.persistence.dao.WorkDao;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.pojo.ContributorsRolesAndSequences;
import org.orcid.pojo.WorkSummaryExtended;
import org.orcid.pojo.ajaxForm.PojoUtil;
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
    private ContributorsRolesAndSequencesConverter contributorsRolesAndSequencesConverter;
    private static Logger logger = Logger.getLogger(FilterTopContributors.class.getName());
    private static final int MAX_CONTRIBUTORS_FOR_UI = 50;
    private static int batchSize = 1000;
    private static long workId = 0;
    private static String logRoute = null;

    /**
     * Batch filter top contributors from the auto-incrementing workId provided as a parameter up to the number defined in the class.
     *
     * @param {String} workId
     * @param {String} logRoute
     * @param {String} batchSize
     *
     * Examples:
     *      $ java -DworkId=0000 -DlogRoute=/route/to/store/logs -DbatchSize=100 FilterTopContributors.java
     */
    public static void main(String ...args) {
        FilterTopContributors filterTopContributors = new FilterTopContributors();
        filterTopContributors.validateParameters();
        filterTopContributors.initializeLog();
        filterTopContributors.filter();
    }

    private void filter() {
        init();
        List<Object[]> workEntityList = workDao.getWorksStartingFromWorkId(workId, batchSize);
        workEntityList.forEach(this::filterTopContributors);
        if (!workEntityList.isEmpty()) {
            System.out.println("Last workId processed was " + workEntityList.get(workEntityList.size() - 1)[0]);
        }
    }

    private void filterTopContributors(Object[] workObject) {
        WorkEntity workEntity = workDao.find(((BigInteger) workObject[0]).longValue());
        ContributorUtils contributorUtils = new ContributorUtils(0);
        WorkSummaryExtended wse = new WorkSummaryExtended.WorkSummaryExtendedBuilder(((BigInteger) workObject[0]))
                .contributors(workContributorsConverter.getContributorsList(isEmpty(workObject[1])))
                .build();
        List<ContributorsRolesAndSequences> contributors = contributorUtils.getContributorsGroupedByOrcid(wse.getContributors().getContributor(), MAX_CONTRIBUTORS_FOR_UI);
        if (contributors.size() > 0) {
            try {
                workEntity.setTopContributorsJson(contributorsRolesAndSequencesConverter.convertTo(contributors, null));
                workDao.merge(workEntity);
                logger.info(workEntity.getId() + " was processed");
                workDao.flush();
            } catch (Exception e) {
                logger.info(workEntity.getId() + " could not be processed");
                System.exit(0);
            }
        }
    }

    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        workDao = (WorkDao) context.getBean("workDao");
        workContributorsConverter = (WorkContributorsConverter) context.getBean("workContributorsConverter");
        contributorsRolesAndSequencesConverter = (ContributorsRolesAndSequencesConverter) context.getBean("contributorsRolesAndSequencesConverter");
    }

    private void validateParameters() {
        String workIdParameter = System.getProperty("workId");
        String logRouteParameter = System.getProperty("logRoute");
        String batchSizeParameter = System.getProperty("batchSize");

        if (PojoUtil.isEmpty(workIdParameter)) {
            printMessageAndExit("Parameter workId is missing!.");
        } else if (PojoUtil.isEmpty(logRouteParameter)) {
            printMessageAndExit("Parameter logRoute is missing!.");
        } else if (PojoUtil.isEmpty(batchSizeParameter)) {
            printMessageAndExit("Parameter batchSize is missing!.");
        }

        try {
            workId = Long.parseLong(workIdParameter);
        } catch (Exception e) {
            printMessageAndExit("Parameter workId must be a number!");
        }

        try {
            batchSize = Integer.parseInt(batchSizeParameter);
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
            printMessageAndExit("Parameter logRoute is invalid!");
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
