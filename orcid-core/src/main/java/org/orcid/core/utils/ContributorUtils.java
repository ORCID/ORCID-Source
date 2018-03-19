package org.orcid.core.utils;

import org.orcid.core.manager.ActivityManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.jaxb.model.common_v2.Contributor;
import org.orcid.jaxb.model.common_v2.CreditName;
import org.orcid.jaxb.model.record.bulk.BulkElement;
import org.orcid.jaxb.model.record_v2.Funding;
import org.orcid.jaxb.model.record_v2.FundingContributor;
import org.orcid.jaxb.model.record_v2.Work;
import org.orcid.jaxb.model.record_v2.WorkBulk;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;

public class ContributorUtils {

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
