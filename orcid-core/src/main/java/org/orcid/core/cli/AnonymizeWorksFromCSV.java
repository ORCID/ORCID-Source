package org.orcid.core.cli;

import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.core.adapter.v3.JpaJaxbWorkAdapter;
import org.orcid.core.adapter.v3.converter.ContributorsRolesAndSequencesConverter;
import org.orcid.core.cli.anonymize.WorkPojoFromCsv;
import org.orcid.core.manager.ProfileEntityCacheManager;

import org.orcid.core.togglz.OrcidTogglzConfiguration;
import org.orcid.core.utils.v3.ContributorUtils;
import org.orcid.persistence.dao.WorkDao;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.pojo.ContributorsRolesAndSequences;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.togglz.core.context.ContextClassLoaderFeatureManagerProvider;
import org.togglz.core.manager.FeatureManager;
import org.togglz.core.manager.FeatureManagerBuilder;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import org.orcid.jaxb.model.v3.release.record.Work;

public class AnonymizeWorksFromCSV {

    private static final Logger LOG = LoggerFactory.getLogger(AnonymizeWorksFromCSV.class);

    @Option(name = "-oid", usage = "Orcid ID to add the works to")
    private String orcid;

    @Option(name = "-f", usage = "The location of csv file that contains the works for orcid")
    private String workCsvFile;

    @Option(name = "-cid", usage = "The client id to be used as source, if not provided the orcid will be set as source.")
    private String clientId;

    protected WorkDao workDao;

    @Resource(name = "jpaJaxbWorkAdapterV3")
    protected JpaJaxbWorkAdapter jpaJaxbWorkAdapter;

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;
    
    @Resource(name = "contributorUtilsV3")
    private ContributorUtils contributorUtils;

    @Resource
    private ContributorsRolesAndSequencesConverter contributorsRolesAndSequencesConverter;
    
    @Value("${org.orcid.core.work.contributors.ui.max:50}")
    private int maxContributorsForUI;
    

    public static void main(String[] args) throws IOException {
        AnonymizeWorksFromCSV anonymizeFromCsv = new AnonymizeWorksFromCSV();
        CmdLineParser parser = new CmdLineParser(anonymizeFromCsv);
        try {
            parser.parseArgument(args);
            anonymizeFromCsv.validateParameters(parser);
            anonymizeFromCsv.init();

            anonymizeFromCsv.anonymizeWorks();

        } catch (Exception e) {
            LOG.error("Exception when anonymizing  works", e);
            System.err.println(e.getMessage());
        } finally {
            System.exit(0);
        }
    }

    @SuppressWarnings("resource")
    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        workDao = (WorkDao) context.getBean("workDao");
        jpaJaxbWorkAdapter = (JpaJaxbWorkAdapter) context.getBean("jpaJaxbWorkAdapterV3");
        contributorUtils = (ContributorUtils) context.getBean("contributorUtilsV3");
        contributorsRolesAndSequencesConverter=context.getBean(ContributorsRolesAndSequencesConverter.class);
        bootstrapTogglz(context.getBean(OrcidTogglzConfiguration.class));
    }

    private ArrayList<WorkPojoFromCsv> getAllWorks() throws IOException {
        FileReader reader = new FileReader(workCsvFile);
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema schema = CsvSchema.emptySchema().withHeader();
        ObjectReader oReader = csvMapper.reader(WorkPojoFromCsv.class).with(schema);
        ArrayList<WorkPojoFromCsv> worksArray = new ArrayList<WorkPojoFromCsv>();

        MappingIterator<WorkPojoFromCsv> mi = oReader.readValues(reader);
        while (mi.hasNext()) {
            WorkPojoFromCsv current = mi.next();
            worksArray.add(current);
        }

        return worksArray;
    }

    public void validateParameters(CmdLineParser parser) throws CmdLineException {
        if (PojoUtil.isEmpty(orcid)) {
            throw new CmdLineException(parser, "-oid parameter must not be null. A valid orcid is expected.");
        }

        if (PojoUtil.isEmpty(workCsvFile)) {
            throw new CmdLineException(parser, "-f parameter must not be null. Please specify the location of csv file");
        }
    }

    private static void bootstrapTogglz(OrcidTogglzConfiguration togglzConfig) {
        FeatureManager featureManager = new FeatureManagerBuilder().togglzConfig(togglzConfig).build();
        ContextClassLoaderFeatureManagerProvider.bind(featureManager);
    }

    private void anonymizeWorks() throws Exception {
        ArrayList<WorkPojoFromCsv> worksFromCsv = getAllWorks();
        Work work;
        // delete all existent works for orcid provided
        workDao.removeWorks(orcid);

        for (WorkPojoFromCsv workCsv : worksFromCsv) {

            work = workCsv.toAnonymizedWork(orcid);
            
            
            //TODO: Error here for works with contributors, trying to anonymize 'SOFTWARE' but the mapper was expecting 'software'
            WorkEntity workEntity = jpaJaxbWorkAdapter.toWorkEntity(work);
            workEntity.setOrcid(orcid);
            workEntity.setAddedToProfileDate(new Date());
            if (!PojoUtil.isEmpty(clientId)) {
                workEntity.setClientSourceId(clientId);
            } else {
                workEntity.setSourceId(orcid);
            }
            workEntity.setVisibility(work.getVisibility().name());
            if (work.getWorkContributors() != null && work.getWorkContributors().getContributor() != null && work.getWorkContributors().getContributor().size() > 0) {
                if(maxContributorsForUI == 0) {
                    maxContributorsForUI = 50;
                }
                List<ContributorsRolesAndSequences> topContributors = contributorUtils.getContributorsGroupedByOrcid(work.getWorkContributors().getContributor(), maxContributorsForUI);
                if (topContributors.size() > 0) {
                    workEntity.setTopContributorsJson(contributorsRolesAndSequencesConverter.convertTo(topContributors, null));
                }
            } else {
                workEntity.setContributorsJson("{\"contributor\":[]}");
                workEntity.setTopContributorsJson("[]");
            }
            workEntity.setDisplayIndex(workCsv.getDisplayIndex());
            workDao.persist(workEntity);
            workDao.flush();
        }

        return;
    }

}
