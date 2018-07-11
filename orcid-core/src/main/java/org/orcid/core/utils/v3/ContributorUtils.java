package org.orcid.core.utils.v3;

import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.ActivityManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.jaxb.model.record.bulk.BulkElement;
import org.orcid.jaxb.model.v3.rc1.common.Contributor;
import org.orcid.jaxb.model.v3.rc1.common.CreditName;
import org.orcid.jaxb.model.v3.rc1.record.Funding;
import org.orcid.jaxb.model.v3.rc1.record.FundingContributor;
import org.orcid.jaxb.model.v3.rc1.record.Work;
import org.orcid.jaxb.model.v3.rc1.record.WorkBulk;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContributorUtils {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ContributorUtils.class);

    private ProfileEntityCacheManager profileEntityCacheManager;

    private ActivityManager cacheManager;

    private ProfileEntityManager profileEntityManager;

    public void filterContributorPrivateData(Funding funding) {
        if (funding.getContributors() != null && funding.getContributors().getContributor() != null) {
            for (FundingContributor contributor : funding.getContributors().getContributor()) {
                contributor.setContributorEmail(null);
                if (!PojoUtil.isEmpty(contributor.getContributorOrcid())) {
                    String contributorOrcid = contributor.getContributorOrcid().getPath();
                    if (profileEntityManager.orcidExists(contributorOrcid)) {
                        // contributor is an ORCID user - visibility of user's
                        // name in record must be taken into account
                        ProfileEntity profileEntity = profileEntityCacheManager.retrieve(contributorOrcid);
                        String publicContributorCreditName = cacheManager.getPublicCreditName(profileEntity);
                        CreditName creditName = new CreditName(publicContributorCreditName != null ? publicContributorCreditName : "");
                        contributor.setCreditName(creditName);
                    }
                }
            }
        }
    }

    public void filterContributorPrivateData(Work work) {
        if (work.getWorkContributors() != null && work.getWorkContributors().getContributor() != null) {
            for (Contributor contributor : work.getWorkContributors().getContributor()) {
                contributor.setContributorEmail(null);
                if (!PojoUtil.isEmpty(contributor.getContributorOrcid())) {
                    String contributorOrcid = contributor.getContributorOrcid().getPath();
                    try {
                        // contributor is an ORCID user - visibility of user's
                        // name in record must be taken into account
                        ProfileEntity profileEntity = profileEntityCacheManager.retrieve(contributorOrcid);
                        if(profileEntity != null) {
                            String publicContributorCreditName = cacheManager.getPublicCreditName(profileEntity);
                            CreditName creditName = new CreditName(publicContributorCreditName != null ? publicContributorCreditName : "");
                            contributor.setCreditName(creditName);
                        }
                    } catch(Exception e) {
                        //Just ignore adding the contributor name
                        LOGGER.warn("Invalid contributor orcid " + contributorOrcid + " on work with id: " + work.getPutCode());
                    }
                }
            }
        }
    }

    public void filterContributorPrivateData(WorkBulk works) {
        if(works != null) {
            for(BulkElement element : works.getBulk()) {
                if(Work.class.isAssignableFrom(element.getClass())) {
                    filterContributorPrivateData((Work) element);
                }
            }
        }
    }
    
    public void setProfileEntityCacheManager(ProfileEntityCacheManager profileEntityCacheManager) {
        this.profileEntityCacheManager = profileEntityCacheManager;
    }

    public void setCacheManager(ActivityManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void setProfileEntityManager(ProfileEntityManager profileEntityManager) {
        this.profileEntityManager = profileEntityManager;
    }

}
