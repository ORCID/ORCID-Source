package org.orcid.core.manager.v3.impl;

import java.io.Writer;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.exception.InvalidDisambiguatedOrgException;
import org.orcid.core.manager.v3.OrgManager;
import org.orcid.core.manager.v3.SourceManager;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.jaxb.model.message.Organization;
import org.orcid.jaxb.model.v3.release.common.MultipleOrganizationHolder;
import org.orcid.jaxb.model.v3.release.common.OrganizationHolder;
import org.orcid.persistence.dao.OrgAffiliationRelationDao;
import org.orcid.persistence.dao.OrgDao;
import org.orcid.persistence.dao.OrgDisambiguatedDao;
import org.orcid.persistence.dao.PeerReviewDao;
import org.orcid.persistence.dao.ProfileFundingDao;
import org.orcid.persistence.dao.ResearchResourceDao;
import org.orcid.persistence.jpa.entities.AmbiguousOrgEntity;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.orcid.persistence.jpa.entities.PeerReviewEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileFundingEntity;
import org.orcid.persistence.jpa.entities.ResearchResourceEntity;
import org.orcid.persistence.jpa.entities.ResearchResourceItemEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * 
 * @author Will Simpson
 * 
 */
public class OrgManagerImpl implements OrgManager {

    private static final String[] AMBIGUOUS_ORGS_HEADER = new String[] { "id", "source_orcid", "name", "city", "region", "country", "used_count" };

    private static final String[] DISAMBIGUATED_ORGS_HEADER = new String[] { "id", "source_id", "source_type", "org_type", "name", "city", "region", "country",
            "used_count" };

    private static final int CHUNK_SIZE = 10000;

    @Resource
    private OrgDao orgDao;

    @Resource
    private OrgDisambiguatedDao orgDisambiguatedDao;

    @Resource(name = "sourceManagerV3")
    private SourceManager sourceManager;

    @Resource
    private ProfileFundingDao profileFundingDao;

    @Resource
    private OrgAffiliationRelationDao orgAffiliationRelationDao;

    @Resource
    private ResearchResourceDao researchResourceDao;

    @Resource
    private PeerReviewDao peerReviewDao;

    @Override
    public List<AmbiguousOrgEntity> getAmbiguousOrgs(int firstResult, int maxResults) {
        return orgDao.getAmbiguousOrgs(firstResult, maxResults);
    }

    @Override
    public void writeAmbiguousOrgs(Writer writer) {
        @SuppressWarnings("resource")
        CSVWriter csvWriter = new CSVWriter(writer);
        csvWriter.writeNext(AMBIGUOUS_ORGS_HEADER);
        int firstResult = 0;
        List<AmbiguousOrgEntity> chunk = null;
        do {
            chunk = getAmbiguousOrgs(firstResult, CHUNK_SIZE);
            for (AmbiguousOrgEntity orgEntity : chunk) {
                String[] line = new String[] { String.valueOf(orgEntity.getId()), orgEntity.getSourceOrcid(), orgEntity.getName(), orgEntity.getCity(),
                        orgEntity.getRegion(), orgEntity.getCountry(), String.valueOf(orgEntity.getUsedCount()) };
                csvWriter.writeNext(line);
            }
            firstResult += chunk.size();
        } while (!chunk.isEmpty());

    }

    @Override
    public void writeDisambiguatedOrgs(Writer writer) {
        @SuppressWarnings("resource")
        CSVWriter csvWriter = new CSVWriter(writer);
        csvWriter.writeNext(DISAMBIGUATED_ORGS_HEADER);
        int firstResult = 0;
        List<OrgDisambiguatedEntity> chunk = null;
        do {
            chunk = orgDisambiguatedDao.getChunk(firstResult, CHUNK_SIZE);
            for (OrgDisambiguatedEntity orgEntity : chunk) {
                String[] line = new String[] { String.valueOf(orgEntity.getId()), orgEntity.getSourceId(), orgEntity.getSourceType(), orgEntity.getOrgType(),
                        orgEntity.getName(), orgEntity.getCity(), orgEntity.getRegion(), orgEntity.getCountry(), String.valueOf(orgEntity.getPopularity()) };
                csvWriter.writeNext(line);
            }
            firstResult += chunk.size();
        } while (!chunk.isEmpty());
    }

    @Override
    public List<OrgEntity> getOrgs(String searchTerm, int firstResult, int maxResults) {
        return orgDao.getOrgs(searchTerm, firstResult, maxResults);
    }

    @Override
    public List<OrgEntity> getOrgsByName(String searchTerm) {
        return orgDao.getOrgsByName(searchTerm);
    }

    private OrgEntity matchOrCreateOrg(OrgEntity org) {
        OrgEntity match = orgDao.findByAddressAndDisambiguatedOrg(org.getName(), org.getCity(), org.getRegion(), org.getCountry(), org.getOrgDisambiguated());
        if (match != null) {
            return match;
        }

        SourceEntity entity = sourceManager.retrieveActiveSourceEntity();
        if (entity != null) {
            SourceEntity newEntity = new SourceEntity();
            if (entity.getSourceClient() != null) {
                newEntity.setSourceClient(new ClientDetailsEntity(entity.getSourceClient().getId()));
            }
            if (entity.getSourceProfile() != null) {
                newEntity.setSourceProfile(new ProfileEntity(entity.getSourceProfile().getId()));
            }

            org.setSource(newEntity);
        }

        orgDao.persist(org);
        return org;
    }

    @Override
    public OrgEntity getOrgEntity(OrganizationHolder holder) {
        if (holder == null)
            return null;
        return getOrgEntity(holder.getOrganization());
    }

    private OrgEntity getOrgEntity(org.orcid.jaxb.model.v3.release.common.Organization organization) {
        OrgEntity orgEntity = new OrgEntity();
        orgEntity.setName(organization.getName());
        org.orcid.jaxb.model.v3.release.common.OrganizationAddress address = organization.getAddress();
        orgEntity.setCity(address.getCity());
        orgEntity.setRegion(address.getRegion() != null ? address.getRegion() : "");
        orgEntity.setCountry(address.getCountry().name());

        if (organization.getDisambiguatedOrganization() != null && organization.getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier() != null) {
            // if disambiguated org is present (must be for v3 API, but not UI)
            // it must be valid
            OrgDisambiguatedEntity disambiguatedOrg = orgDisambiguatedDao.findBySourceIdAndSourceType(
                    organization.getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier(),
                    organization.getDisambiguatedOrganization().getDisambiguationSource());
            if (disambiguatedOrg == null) {
                throw new InvalidDisambiguatedOrgException();
            }
            orgEntity.setOrgDisambiguated(disambiguatedOrg);
        }
        return matchOrCreateOrg(orgEntity);
    }

    @Override
    public List<OrgEntity> getOrgEntities(MultipleOrganizationHolder holder) {
        ArrayList<OrgEntity> entities = new ArrayList<OrgEntity>();
        for (org.orcid.jaxb.model.v3.release.common.Organization organization : holder.getOrganization()) {
            entities.add(getOrgEntity(organization));
        }
        return entities;
    }

    @Override
    public OrgEntity getOrgEntity(Organization org) {
        String name = org.getName();
        String city = "";
        String region = "";
        Iso3166Country country = null;
        if (org.getAddress() != null) {
            city = org.getAddress().getCity();
            region = org.getAddress().getRegion();
            country = org.getAddress().getCountry();

        }
        return orgDao.findByNameCityRegionAndCountry(name, city, region, country.name());
    }

    @Override
    public void updateDisambiguatedOrgReferences(Long deprecated, Long replacement) {
        List<OrgEntity> referencingDeprecated = orgDao.findByOrgDisambiguatedId(deprecated);
        List<OrgEntity> referencingReplacement = orgDao.findByOrgDisambiguatedId(replacement);
        OrgDisambiguatedEntity replacementEntity = orgDisambiguatedDao.find(replacement);
        
        if (referencingReplacement == null || referencingReplacement.isEmpty()) {
            // no unique constraint concerns
            orgDao.updateOrgDisambiguatedId(deprecated, replacement);
        } else {
            referencingDeprecated.forEach(toUpdate -> {
                // find potential match already pointing to replacement
                OrgEntity match = orgDao.findByAddressAndDisambiguatedOrg(toUpdate.getName(), toUpdate.getCity(), toUpdate.getRegion(), toUpdate.getCountry(),
                        replacementEntity);
                if (match != null) {
                    // point all entities to match and remove org that we were going to update
                    updateProfileFundingOrgReferences(match, toUpdate.getId());
                    updatePeerReviewOrgReferences(match, toUpdate.getId());
                    updateOrgAffiliationRelationOrgReferences(match, toUpdate.getId());
                    updateResearchResourcesOrgReferences(match, toUpdate.getId());
                    orgDao.remove(toUpdate.getId());
                } else {
                    // no unique constraint concerns
                    toUpdate.setOrgDisambiguated(orgDisambiguatedDao.find(replacement));
                    orgDao.merge(toUpdate);
                }
            });
        }
    }
    
    private void updateResearchResourcesOrgReferences(OrgEntity orgToReference, Long previousOrgIdReference) {
        List<BigInteger> researchResourceIds = researchResourceDao.getResearchResourcesReferencingOrgs(Arrays.asList(previousOrgIdReference));;
        researchResourceIds.forEach(id -> {
            ResearchResourceEntity r = researchResourceDao.find(id.longValue());
            for (int i = 0; i < r.getHosts().size(); i++) {
                if (r.getHosts().get(i) != null && previousOrgIdReference.equals(r.getHosts().get(i).getId())) {
                    r.getHosts().remove(i);
                    r.getHosts().add(i, orgToReference);
                }
            }
            List<ResearchResourceItemEntity> researchResourceItems = r.getResourceItems();
            researchResourceItems.forEach(i -> {
                for (int x = 0; x < i.getHosts().size(); x++) {
                    if (i.getHosts().get(x) != null && previousOrgIdReference.equals(i.getHosts().get(x).getId())) {
                        i.getHosts().remove(x);
                        i.getHosts().add(x, orgToReference);
                    }
                }
            });
            researchResourceDao.merge(r);
        });

    }

    private void updateProfileFundingOrgReferences(OrgEntity orgToReference, Long previousOrgIdReference) {
        List<ProfileFundingEntity> fundings = profileFundingDao.getFundingsReferencingOrgs(Arrays.asList(previousOrgIdReference));
        fundings.forEach(f -> {
            f.setOrg(orgToReference);
            profileFundingDao.merge(f);
        });
    }

    private void updateOrgAffiliationRelationOrgReferences(OrgEntity orgToReference, Long previousOrgIdReference) {
        List<OrgAffiliationRelationEntity> orgAffiliationRelations = orgAffiliationRelationDao.getOrgAffiliationRelationsReferencingOrgs(Arrays.asList(previousOrgIdReference));
        orgAffiliationRelations.forEach(o -> {
            o.setOrg(orgToReference);
            orgAffiliationRelationDao.merge(o);
        });
    }

    private void updatePeerReviewOrgReferences(OrgEntity orgToReference, Long previousOrgIdReference) {
        List<PeerReviewEntity> peerReviews = peerReviewDao.getPeerReviewsReferencingOrgs(Arrays.asList(previousOrgIdReference));
        peerReviews.forEach(p -> {
            p.setOrg(orgToReference);
            peerReviewDao.merge(p);
        });
    }


}
