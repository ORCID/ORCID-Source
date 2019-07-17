package org.orcid.core.manager.v3.impl;

import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.manager.v3.ActivityManager;
import org.orcid.core.manager.v3.AffiliationsManager;
import org.orcid.core.manager.v3.PeerReviewManager;
import org.orcid.core.manager.v3.ProfileFundingManager;
import org.orcid.core.manager.v3.read_only.impl.ManagerReadOnlyBaseImpl;
import org.orcid.core.utils.RecordNameUtils;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.Affiliation;
import org.orcid.jaxb.model.v3.release.record.Funding;
import org.orcid.jaxb.model.v3.release.record.PeerReview;
import org.orcid.persistence.dao.RecordNameDao;
import org.orcid.persistence.jpa.entities.RecordNameEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;

public class ActivityManagerImpl extends ManagerReadOnlyBaseImpl implements ActivityManager {
    
    @Resource(name = "peerReviewManagerV3")
    private PeerReviewManager peerReviewManager;
    
    @Resource(name = "profileFundingManagerV3")
    private ProfileFundingManager profileFundingManager;    
    
    @Resource(name = "affiliationsManagerV3")
    private AffiliationsManager affiliationsManager;
    
    @Resource(name = "recordNameDaoReadOnly")
    private RecordNameDao recordNameDaoReadOnly;

    public LinkedHashMap<Long, PeerReview> pubPeerReviewsMap(String orcid) {
        List<PeerReview> peerReviews = peerReviewManager.findPeerReviews(orcid);
        LinkedHashMap<Long, PeerReview> peerReviewMap = new LinkedHashMap<>();
        if (peerReviews != null) {
            if (!peerReviews.isEmpty()) {                
                for(PeerReview peerReview : peerReviews) {
                    if(peerReview.getVisibility().equals(Visibility.PUBLIC)) {
                        peerReviewMap.put(peerReview.getPutCode(), peerReview);
                    }
                }
            }
        }
        return peerReviewMap;
    }
    
    public LinkedHashMap<Long, Funding> fundingMap(String orcid) {
    	List<Funding> fundings = profileFundingManager.getFundingList(orcid);
        LinkedHashMap<Long, Funding> fundingMap = new LinkedHashMap<>();
		if (fundings != null) {
			for (Funding funding : fundings) {
				if (funding.getVisibility().equals(Visibility.PUBLIC))
					fundingMap.put(Long.valueOf(funding.getPutCode()), funding);
			}
		}
        return fundingMap;
    }

    public LinkedHashMap<Long, Affiliation> affiliationMap(String orcid) {
        LinkedHashMap<Long, Affiliation> affiliationMap = new LinkedHashMap<>();
        List<Affiliation> affiliations = affiliationsManager.getAffiliations(orcid);
        for (Affiliation affiliation : affiliations) {
            if (Visibility.PUBLIC.equals(affiliation.getVisibility())) {
                affiliationMap.put(affiliation.getPutCode(), affiliation);
            }
        }
        return affiliationMap;
    }

    @Override
    public String getCreditName(String orcid) {
        RecordNameEntity recordName = recordNameDaoReadOnly.getRecordName(orcid, getLastModified(orcid));
        if(recordName != null) {
            if (StringUtils.isNotBlank(recordName.getCreditName())) {
                return recordName.getCreditName();
            } else {
                String givenName = recordName.getGivenNames();
                String familyName = recordName.getFamilyName();
                String composedCreditName = (PojoUtil.isEmpty(givenName) ? "" : givenName) + " " + (PojoUtil.isEmpty(familyName) ? "" : familyName);
                return composedCreditName;
            }
        }
        return null;
    }
    
    @Override
    public String getPublicCreditName(String orcid) {
        RecordNameEntity recordName = recordNameDaoReadOnly.getRecordName(orcid, getLastModified(orcid));
        return RecordNameUtils.getPublicName(recordName);
    }
}
