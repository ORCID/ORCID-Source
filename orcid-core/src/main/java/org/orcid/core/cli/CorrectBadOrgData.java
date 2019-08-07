package org.orcid.core.cli;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.persistence.dao.OrgAffiliationRelationDao;
import org.orcid.persistence.dao.OrgDao;
import org.orcid.persistence.dao.PeerReviewDao;
import org.orcid.persistence.dao.ProfileFundingDao;
import org.orcid.persistence.dao.ResearchResourceDao;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.orcid.persistence.jpa.entities.PeerReviewEntity;
import org.orcid.persistence.jpa.entities.ProfileFundingEntity;
import org.orcid.persistence.jpa.entities.ResearchResourceEntity;
import org.orcid.persistence.jpa.entities.ResearchResourceItemEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CorrectBadOrgData {

    private static final Logger LOG = LoggerFactory.getLogger(CorrectBadOrgData.class);

    private static final int BATCH_SIZE = 300;

    @Option(name = "-d", usage = "Dry run", required = false)
    private boolean dryRun;

    @Resource
    private ProfileFundingDao profileFundingDao;

    @Resource
    private PeerReviewDao peerReviewDao;

    @Resource
    private ResearchResourceDao researchResourceDao;

    @Resource
    private OrgAffiliationRelationDao orgAffiliationRelationDao;

    @Resource
    private OrgDao orgDao;

    private List<OrgDetails> duplicateOrgDetails;

    public static void main(String[] args) throws CmdLineException {
        CorrectBadOrgData corrector = new CorrectBadOrgData();
        CmdLineParser parser = new CmdLineParser(corrector);
        parser.parseArgument(args);
        corrector.init();
        corrector.findDuplicateOrgs();
        corrector.correctOrgsAndRemoveDuplicates();
        corrector.makeNullFieldsEmptyStrings();
    }

    private void makeNullFieldsEmptyStrings() {
        if (!dryRun) {
            LOG.info("Changing null fields to empty strings...");
            int numChanged = orgDao.convertNullRegionsToEmptyStrings(BATCH_SIZE);
            while (numChanged > 0) {
                LOG.info("Updated {} regions", numChanged);
                numChanged = orgDao.convertNullRegionsToEmptyStrings(BATCH_SIZE);
            }
            
            numChanged = orgDao.convertNullCitiesToEmptyStrings(BATCH_SIZE);
            while (numChanged > 0) {
                LOG.info("Updated {} cities", numChanged);
                numChanged = orgDao.convertNullCitiesToEmptyStrings(BATCH_SIZE);
            }
            
            numChanged = orgDao.convertNullCountriesToEmptyStrings(BATCH_SIZE);
            while (numChanged > 0) {
                LOG.info("Updated {} countries", numChanged);
                numChanged = orgDao.convertNullCountriesToEmptyStrings(BATCH_SIZE);
            }
            LOG.info("Changed all null fields to empty strings");
        }
    }

    private void correctOrgsAndRemoveDuplicates() {
        LOG.info("Correcting org data...");
        duplicateOrgDetails.forEach(o -> {
            LOG.info("Examining org data {} {} {} {} {}", new Object[] { o.getName(), o.getCity(), o.getRegion(), o.getCountry(), o.getOrgDisambiguatedId() });
            List<Long> orgIds = orgDao.getOrgIdsForDuplicateOrgDetails(o.getName(), o.getCity(), o.getRegion(), o.getCountry(), o.getOrgDisambiguatedId()).stream()
                    .map(i -> i.longValue()).collect(Collectors.toList());
            LOG.info("Found {} orgIds matching data", orgIds.size());
            OrgEntity orgToReference = orgDao.find(orgIds.get(0).longValue());
            LOG.info("Entities will reference org {}", orgToReference.getId());
            updatePeerReviewOrgReferences(orgToReference, orgIds);
            updateOrgAffiliationRelationReferences(orgToReference, orgIds);
            updateProfileFundingReferences(orgToReference, orgIds);
            updateResearchResourcesReferences(orgToReference, orgIds);
            removeOrgs(orgIds.subList(1, orgIds.size()));
        });
    }

    private void removeOrgs(List<Long> ids) {
        LOG.info("{} orgs to remove", ids.size());
        if (!dryRun) {
            LOG.info("Removing orgs...");
            ids.forEach(id -> orgDao.remove(id));
        }
    }

    private void updateResearchResourcesReferences(OrgEntity orgToReference, List<Long> orgIds) {
        LOG.info("Pointing research resources to org {}", orgToReference.getId());
        List<ResearchResourceEntity> researchResources = researchResourceDao.getResearchResourcesReferencingOrgs(orgIds);
        LOG.info("Found {} research resources referencing org", researchResources.size());
        researchResources.forEach(r -> {
            LOG.info("Examining research resource {}...", r.getId());
            List<Long> orgIdsAsLongs = orgIds.stream().map(o -> o.longValue()).collect(Collectors.toList());
            for (int i = 0; i < r.getHosts().size(); i++) {
                if (orgIdsAsLongs.contains(r.getHosts().get(i).getId())) {
                    LOG.info("Found duplicate org {} in research resource hosts", r.getHosts().get(i).getId());
                    r.getHosts().remove(i);
                    r.getHosts().add(i, orgToReference);
                    LOG.info("Replaced duplicate org with {}", orgToReference.getId());
                }
            }
            List<ResearchResourceItemEntity> researchResourceItems = r.getResourceItems();
            researchResourceItems.forEach(i -> {
                for (int x = 0; x < i.getHosts().size(); x++) {
                    if (orgIdsAsLongs.contains(i.getHosts().get(x).getId())) {
                        LOG.info("Found duplicate org {} in research resource item hosts", r.getHosts().get(x).getId());
                        i.getHosts().remove(x);
                        i.getHosts().add(x, orgToReference);
                        LOG.info("Replaced duplicate org with {}", orgToReference.getId());
                    }
                }
            });

            if (!dryRun) {
                researchResourceDao.merge(r);
                LOG.info("Updated research resource {}", r.getId());
            }
        });

    }

    private void updateProfileFundingReferences(OrgEntity orgToReference, List<Long> orgIds) {
        LOG.info("Pointing fundings to org {}", orgToReference.getId());
        List<ProfileFundingEntity> fundings = profileFundingDao.getFundingsReferencingOrgs(orgIds);
        LOG.info("Found {} fundings referencing org", fundings.size());
        fundings.forEach(f -> {
            f.setOrg(orgToReference);
            if (!dryRun) {
                profileFundingDao.merge(f);
                LOG.info("Updated funding {}", f.getId());
            }
        });
    }

    private void updateOrgAffiliationRelationReferences(OrgEntity orgToReference, List<Long> orgIds) {
        LOG.info("Pointing org affiliation relations to org {}", orgToReference.getId());
        List<OrgAffiliationRelationEntity> orgAffiliationRelations = orgAffiliationRelationDao.getOrgAffiliationRelationsReferencingOrgs(orgIds);
        LOG.info("Found {} org affiliation relations referencing org", orgAffiliationRelations.size());
        orgAffiliationRelations.forEach(o -> {
            o.setOrg(orgToReference);
            if (!dryRun) {
                orgAffiliationRelationDao.merge(o);
                LOG.info("Updated affilication relation {}", o.getId());
            }
        });
    }

    private void updatePeerReviewOrgReferences(OrgEntity orgToReference, List<Long> orgIds) {
        LOG.info("Pointing peer reviews to org {}", orgToReference.getId());
        List<PeerReviewEntity> peerReviews = peerReviewDao.getPeerReviewsReferencingOrgs(orgIds);
        LOG.info("Found {} peer reviews referencing org", peerReviews.size());
        peerReviews.forEach(p -> {
            p.setOrg(orgToReference);
            if (!dryRun) {
                LOG.info("Updated peer review {}", p.getId());
                peerReviewDao.merge(p);
            }
        });
    }

    private void findDuplicateOrgs() {
        duplicateOrgDetails = new ArrayList<>();
        LOG.info("Looking for duplicated org data...");
        List<Object[]> duplicateOrgRows = orgDao.findConstraintViolatingDuplicateOrgDetails();
        LOG.info("Found {} duplicated org rows", duplicateOrgRows.size());
        duplicateOrgRows.forEach(o -> {
            String name = (String) o[0];
            String city = (String) o[1];
            String region = (String) o[2];
            String country = (String) o[3];
            Long orgDisambiguatedId = ((BigInteger) o[4]).longValue();
            duplicateOrgDetails.add(new OrgDetails(name, city, region, country, orgDisambiguatedId));
        });
    }

    @SuppressWarnings("resource")
    private void init() {
        LOG.info("Initialising DAOs...");
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        peerReviewDao = (PeerReviewDao) context.getBean("peerReviewDao");
        orgAffiliationRelationDao = (OrgAffiliationRelationDao) context.getBean("orgAffiliationRelationDao");
        orgDao = (OrgDao) context.getBean("orgDao");
        researchResourceDao = (ResearchResourceDao) context.getBean("researchResourceDao");
        profileFundingDao = (ProfileFundingDao) context.getBean("profileFundingDao");
    }

    private class OrgDetails {

        private String name;

        private String city;

        private String region;

        private String country;

        private Long orgDisambiguatedId;

        public OrgDetails(String name, String city, String region, String country, Long orgDisambiguatedId) {
            this.name = name;
            this.city = city;
            this.region = region;
            this.country = country;
            this.orgDisambiguatedId = orgDisambiguatedId;
        }

        public String getName() {
            return name;
        }

        public String getCity() {
            return city;
        }

        public String getRegion() {
            return region;
        }

        public String getCountry() {
            return country;
        }

        public Long getOrgDisambiguatedId() {
            return orgDisambiguatedId;
        }

    }

}
