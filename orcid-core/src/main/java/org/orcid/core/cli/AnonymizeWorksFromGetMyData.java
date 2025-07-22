package org.orcid.core.cli;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;

import org.codehaus.jettison.json.JSONException;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.core.adapter.v3.JpaJaxbWorkAdapter;
import org.orcid.core.adapter.v3.converter.ContributorsRolesAndSequencesConverter;
import org.orcid.core.cli.anonymize.AnonymizeText;
import org.orcid.core.cli.anonymize.UnzipFile;

import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.togglz.OrcidTogglzConfiguration;
import org.orcid.core.utils.DisplayIndexCalculatorHelper;
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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import org.orcid.jaxb.model.v3.release.common.SourceOrcid;
import org.orcid.jaxb.model.v3.release.common.Url;
import org.orcid.jaxb.model.v3.release.record.Work;

import org.orcid.core.cli.anonymize.XMLDeserializer;

public class AnonymizeWorksFromGetMyData {

    private static final Logger LOG = LoggerFactory.getLogger(AnonymizeWorksFromCSV.class);

    @Option(name = "-oid", usage = "Orcid ID to add the works to")
    private String orcid;

    @Option(name = "-f", usage = "The location of zip file that contains the works for orcid")
    private String workZipFile;

    @Option(name = "-d", usage = "The location of zip file that contains the works for orcid")
    private String pathUnzipFile;

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
        AnonymizeWorksFromGetMyData anonymizeFromGetMyData = new AnonymizeWorksFromGetMyData();
        CmdLineParser parser = new CmdLineParser(anonymizeFromGetMyData);
        try {
            parser.parseArgument(args);
            anonymizeFromGetMyData.validateParameters(parser);
            anonymizeFromGetMyData.init();
            anonymizeFromGetMyData.anonymizeWorks();
        } catch (Exception e) {
            e.printStackTrace();
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

    private ArrayList<Work> getAllWorks() throws Exception {
        ArrayList<Work> worksArray = new ArrayList<Work>();
        File destDir = new File(pathUnzipFile);
        UnzipFile.unzipFile(pathUnzipFile, workZipFile);

        Iterator it = FileUtils.iterateFiles(new File(pathUnzipFile + "/works"), null, true);
        File workFile = null;
        while (it.hasNext()) {
            workFile = (File) it.next();
            worksArray.add(this.readWorkFromFile(workFile));
        }
        return worksArray;
    }

    public void validateParameters(CmdLineParser parser) throws CmdLineException {
        if (PojoUtil.isEmpty(orcid)) {
            throw new CmdLineException(parser, "-oid parameter must not be null. A valid orcid is expected.");
        }

        if (PojoUtil.isEmpty(workZipFile)) {
            throw new CmdLineException(parser, "-f parameter must not be null. Please specify the location of zip file");
        }

        if (PojoUtil.isEmpty(pathUnzipFile)) {
            throw new CmdLineException(parser, "-d parameter must not be null. Please specify the destination to unzip the file");
        }
    }

    private static void bootstrapTogglz(OrcidTogglzConfiguration togglzConfig) {
        FeatureManager featureManager = new FeatureManagerBuilder().togglzConfig(togglzConfig).build();
        ContextClassLoaderFeatureManagerProvider.bind(featureManager);
    }

    private Work toAnonymizedWork(Work origWork, String orcid) throws MalformedURLException, JSONException {
        Work work = new Work();
        AnonymizeText anonymizeText = new AnonymizeText();


        // Set language
        if (!PojoUtil.isEmpty(origWork.getLanguageCode())) {
            work.setLanguageCode(work.getLanguageCode());
        }

        // Set type
        if (origWork.getWorkType() != null) {
            work.setWorkType(origWork.getWorkType());
        }

        org.orcid.jaxb.model.v3.release.record.WorkTitle workTitle = new org.orcid.jaxb.model.v3.release.record.WorkTitle();
        // Set title
        if (origWork.getWorkTitle() != null) {
            workTitle.setTitle(new org.orcid.jaxb.model.v3.release.common.Title(anonymizeText.anonymizeString(origWork.getWorkTitle().getTitle().getContent())));
        }

        // Set translated title
        if (origWork.getWorkTitle().getTranslatedTitle() != null && !PojoUtil.isEmpty(origWork.getWorkTitle().getTranslatedTitle().getContent())) {
            org.orcid.jaxb.model.v3.release.common.TranslatedTitle translatedTitle = new org.orcid.jaxb.model.v3.release.common.TranslatedTitle();
            translatedTitle.setContent(anonymizeText.anonymizeString(origWork.getWorkTitle().getTranslatedTitle().getContent()));
            translatedTitle.setLanguageCode(origWork.getWorkTitle().getTranslatedTitle().getLanguageCode());
            workTitle.setTranslatedTitle(translatedTitle);
        }

        // Set subtitle
        if (origWork.getWorkTitle().getSubtitle() != null) {
            org.orcid.jaxb.model.v3.release.common.Subtitle subtitle = new org.orcid.jaxb.model.v3.release.common.Subtitle();
            subtitle.setContent(anonymizeText.anonymizeString(origWork.getWorkTitle().getSubtitle().getContent()));
            workTitle.setSubtitle(subtitle);
        }

        work.setWorkTitle(workTitle);

        // Set journal title
        if (origWork.getJournalTitle() != null) {
            work.setJournalTitle(new org.orcid.jaxb.model.v3.release.common.Title(anonymizeText.anonymizeString(origWork.getJournalTitle().getContent())));
        }

        // Set description
        if (!PojoUtil.isEmpty(origWork.getShortDescription())) {
            work.setShortDescription(anonymizeText.anonymizeString(origWork.getShortDescription()));
        }

        // Set url
        if (!PojoUtil.isEmpty(origWork.getUrl())) {
            work.setUrl(new Url(anonymizeText.anonymizeString(origWork.getUrl().getValue())));
        } else {
            work.setUrl(new Url());
        }

        // Set visibility
        if (origWork.getVisibility() != null && origWork.getVisibility() != null) {
            work.setVisibility(origWork.getVisibility());
        }

        // Set country
        if (origWork.getCountry() != null) {
            work.setCountry(origWork.getCountry());
        }

        if (origWork.getPublicationDate() != null) {
            org.orcid.jaxb.model.v3.release.common.Year year = PojoUtil.isEmpty(origWork.getPublicationDate().getYear()) ? null : origWork.getPublicationDate().getYear();
            org.orcid.jaxb.model.v3.release.common.Month month = PojoUtil.isEmpty(origWork.getPublicationDate().getMonth()) ? null
                    : origWork.getPublicationDate().getMonth();
            org.orcid.jaxb.model.v3.release.common.Day day = PojoUtil.isEmpty(origWork.getPublicationDate().getDay()) ? null : origWork.getPublicationDate().getDay();
            work.setPublicationDate(new org.orcid.jaxb.model.v3.release.common.PublicationDate(year, month, day));
        }

        // Set citation
        if (origWork.getWorkCitation() != null) {
            org.orcid.jaxb.model.v3.release.record.Citation citation = new org.orcid.jaxb.model.v3.release.record.Citation();
            if (!PojoUtil.isEmpty(origWork.getWorkCitation().getCitation())) {
                citation.setCitation(anonymizeText.anonymizeString(origWork.getWorkCitation().getCitation()));
            }

            if (origWork.getWorkCitation().getWorkCitationType() != null) {
                citation.setWorkCitationType(origWork.getWorkCitation().getWorkCitationType());
            }
            work.setWorkCitation(citation);
        }

        // Set contributors
        if (origWork.getWorkContributors() != null) {

            work.setWorkContributors(anonymizeText.anonymizeWorkContributors(origWork.getWorkContributors()));

        }

        // Set externalids
        if (origWork.getWorkExternalIdentifiers() != null) {

            work.setWorkExternalIdentifiers(anonymizeText.anonymizeWorkExternalIdentifiers(origWork.getWorkExternalIdentifiers()));
        }

        // Set created date
        work.setCreatedDate(origWork.getCreatedDate());

        // Set last modified
        work.setLastModifiedDate(origWork.getLastModifiedDate());

        org.orcid.jaxb.model.v3.release.common.Source source = new org.orcid.jaxb.model.v3.release.common.Source();
        SourceOrcid srcOrcid = new SourceOrcid(orcid);
        srcOrcid.setPath(orcid);
        source.setSourceOrcid(srcOrcid);
        work.setSource(source);

        return work;
    }

    private Work readWorkFromFile(File workXml) throws JsonParseException, JsonMappingException, IOException {
        XMLDeserializer xmlSerializer = new XMLDeserializer();
        Work work = xmlSerializer.fromXml(workXml, Work.class);
        return work;
    }

    private void anonymizeWorks() throws Exception {

        ArrayList<Work> works = getAllWorks();

        // delete all existent works for orcid provided
        for (WorkEntity workEntity : workDao.getAll()) {
            this.workDao.remove(workEntity.getId());
            this.workDao.flush();
        }

        for (Work orig : works) {
            Work anonymizedWork = toAnonymizedWork(orig, orcid);
            WorkEntity workEntity = jpaJaxbWorkAdapter.toWorkEntity(anonymizedWork);
            workEntity.setId(null);
            workEntity.setOrcid(orcid);
            workEntity.setAddedToProfileDate(new Date());
            if (!PojoUtil.isEmpty(clientId)) {
                workEntity.setClientSourceId(clientId);
            } else {
                workEntity.setSourceId(orcid);
            }
            if (anonymizedWork.getWorkContributors() != null && anonymizedWork.getWorkContributors().getContributor() != null && anonymizedWork.getWorkContributors().getContributor().size() > 0) {
                if(maxContributorsForUI == 0) {
                    maxContributorsForUI = 50;
                }
                List<ContributorsRolesAndSequences> topContributors = contributorUtils.getContributorsGroupedByOrcid(anonymizedWork.getWorkContributors().getContributor(), maxContributorsForUI);
                if (topContributors.size() > 0) {
                    workEntity.setTopContributorsJson(contributorsRolesAndSequencesConverter.convertTo(topContributors, null));
                }
            } else {
                workEntity.setContributorsJson("{\"contributor\":[]}");
                workEntity.setTopContributorsJson("[]");
            }
            workEntity.setVisibility(orig.getVisibility().value());
            workEntity.setFeaturedDisplayIndex(0);
            DisplayIndexCalculatorHelper.setDisplayIndexOnNewEntity(workEntity, false);
            workDao.persist(workEntity);
            workDao.flush();
        }

        return;
    }

}
