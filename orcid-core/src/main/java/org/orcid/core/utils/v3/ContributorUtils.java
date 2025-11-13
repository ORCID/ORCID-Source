package org.orcid.core.utils.v3;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.aop.ProfileLastModifiedAspect;
import org.orcid.core.contributors.roles.credit.CreditRole;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.SourceNameCacheManager;
import org.orcid.core.manager.v3.ActivityManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.jaxb.model.v3.release.common.Contributor;
import org.orcid.jaxb.model.v3.release.common.ContributorAttributes;
import org.orcid.jaxb.model.v3.release.common.CreditName;
import org.orcid.jaxb.model.v3.release.record.Funding;
import org.orcid.jaxb.model.v3.release.record.FundingContributor;
import org.orcid.persistence.dao.WorkDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.OrcidAware;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.pojo.ContributorsRolesAndSequences;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.beans.factory.annotation.Value;

public class ContributorUtils {
    
    private final Integer BATCH_SIZE;
    
    private ActivityManager cacheManager;

    private ProfileEntityManager profileEntityManager;

    protected ProfileLastModifiedAspect profileLastModifiedAspect;
    
    public ContributorUtils(@Value("${org.orcid.contributor.names.batch_size:2500}") Integer batchSize) {
        if(batchSize == null) {
            BATCH_SIZE = 2500;
        } else {
            BATCH_SIZE = batchSize;
        }
    }
    
    public void filterContributorPrivateData(Funding funding) {
        if (funding.getContributors() != null && funding.getContributors().getContributor() != null) {
            for (FundingContributor contributor : funding.getContributors().getContributor()) {
                contributor.setContributorEmail(null);
                if (!PojoUtil.isEmpty(contributor.getContributorOrcid())) {
                    String contributorOrcid = contributor.getContributorOrcid().getPath();
                    if (profileEntityManager.orcidExists(contributorOrcid)) {
                        // contributor is an ORCID user - visibility of user's
                        // name in record must be taken into account                        
                        String publicContributorCreditName = cacheManager.getPublicCreditName(contributorOrcid);
                        CreditName creditName = new CreditName(publicContributorCreditName != null ? publicContributorCreditName : "");
                        contributor.setCreditName(creditName);
                    }
                }
            }
        }
    }

    public void setCacheManager(ActivityManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void setProfileEntityManager(ProfileEntityManager profileEntityManager) {
        this.profileEntityManager = profileEntityManager;
    }

    public void setProfileLastModifiedAspect(ProfileLastModifiedAspect profileLastModifiedAspect) {
        this.profileLastModifiedAspect = profileLastModifiedAspect;
    }

    public List<ContributorsRolesAndSequences> getContributorsGroupedByOrcid(List<Contributor> contributors, Integer maxContributorsForUI) {
        List<ContributorsRolesAndSequences> contributorsRolesAndSequencesList = new ArrayList<>();
        for(Contributor contributor : contributors) {
            // Process the list of contributors till reaching the max, then break
            // We add an extra contributor to display a message in the UI in case than there is more than maxContributorsForUI
            if(contributorsRolesAndSequencesList.size() == maxContributorsForUI + 1) {
                break;
            }
            groupContributorsByOrcid(contributor, contributorsRolesAndSequencesList);
        }
        return contributorsRolesAndSequencesList;
    }

    private void groupContributorsByOrcid(Contributor contributor, List<ContributorsRolesAndSequences> contributorsRolesAndSequencesList) {
        if (contributor.getContributorOrcid() != null) {
            String orcid = contributor.getContributorOrcid().getPath();
            if (!StringUtils.isBlank(orcid)) {
                if (contributorsRolesAndSequencesList.size() > 0) {
                    List<ContributorsRolesAndSequences> c = contributorsRolesAndSequencesList
                            .stream()
                            .filter(contr -> contr.getContributorOrcid() != null && contr.getContributorOrcid().getPath() != null && orcid.equals(contr.getContributorOrcid().getPath()))
                            .collect(Collectors.toList());
                    if (c.size() > 0) {
                        ContributorsRolesAndSequences contributorsRolesAndSequences = c.get(0);
                        ContributorAttributes ca = new ContributorAttributes();
                        if(contributor.getContributorAttributes() != null) {
                            if (contributor.getContributorAttributes().getContributorRole() != null) {
                                ca.setContributorRole(getCreditRole(contributor.getContributorAttributes().getContributorRole()));
                            }
                            if(contributor.getContributorAttributes().getContributorSequence() != null) {
                                ca.setContributorSequence(contributor.getContributorAttributes().getContributorSequence());
                            }
                        }
                        List<ContributorAttributes> rolesAndSequencesList = contributorsRolesAndSequences.getRolesAndSequences();
                        rolesAndSequencesList.add(ca);
                        contributorsRolesAndSequences.setRolesAndSequences(rolesAndSequencesList);
                    } else {
                        addContributorWithNameOrOrcid(contributorsRolesAndSequencesList, contributor);
                    }
                } else {
                    addContributorWithNameOrOrcid(contributorsRolesAndSequencesList, contributor);
                }
            } else {
                addContributorWithNameOrOrcid(contributorsRolesAndSequencesList, contributor);
            }
        } else {
            addContributorWithNameOrOrcid(contributorsRolesAndSequencesList, contributor);
        }
    }

    private void addContributorWithNameOrOrcid(List<ContributorsRolesAndSequences> contributorsRolesAndSequencesList, Contributor contributor) {
        if ((contributor.getContributorOrcid() != null && !"".equals(contributor.getContributorOrcid().getPath())) ||
                (contributor.getCreditName() != null && !"".equals(contributor.getCreditName().getContent()))) {
            contributorsRolesAndSequencesList.add(addContributor(contributor));
        }
    }

    private ContributorsRolesAndSequences addContributor(Contributor contributor) {
        ContributorsRolesAndSequences crs = new ContributorsRolesAndSequences();
        if(contributor == null) {
            return crs;
        }
        if (contributor.getContributorOrcid() != null) {
            crs.setContributorOrcid(contributor.getContributorOrcid());
        }
        if (contributor.getCreditName() != null) {
            crs.setCreditName(contributor.getCreditName());
        }
        if (contributor.getContributorAttributes() != null) {
            ContributorAttributes ca = new ContributorAttributes();
            if (contributor.getContributorAttributes().getContributorRole() != null) {
                ca.setContributorRole(getCreditRole(contributor.getContributorAttributes().getContributorRole()));
            }
            if (contributor.getContributorAttributes().getContributorSequence() != null) {
                ca.setContributorSequence(contributor.getContributorAttributes().getContributorSequence());
            }
            List<ContributorAttributes> rolesAndSequences = new ArrayList<>();
            rolesAndSequences.add(ca);
            crs.setRolesAndSequences(rolesAndSequences);
        }

        return crs;
    }

    public String getCreditRole(String contributorRole) {
        try {
            CreditRole cr = CreditRole.fromValue(contributorRole);
            return cr.getUiValue();
        } catch(IllegalArgumentException e) {
            return contributorRole;
        }
    }

    public String getAssertionOriginOrcid(String clientSourceId, String orcid, Long putCode, ClientDetailsEntityCacheManager clientDetailsEntityCacheManager, WorkDao workDao) {
        String assertionOriginOrcid = null;
        ClientDetailsEntity clientSource = clientDetailsEntityCacheManager.retrieve(clientSourceId);
        if (clientSource.isUserOBOEnabled()) {
            WorkEntity e = workDao.getWork(orcid, putCode);

            String orcidId = null;
            if (e instanceof OrcidAware) {                    
                orcidId = ((OrcidAware) e).getOrcid();
            }
            assertionOriginOrcid = orcidId;
        }
        
        return assertionOriginOrcid;
    }

    public String getSourceName(String sourceId, SourceNameCacheManager sourceNameCacheManager) {
        return sourceNameCacheManager.retrieve(sourceId);
    }
}
