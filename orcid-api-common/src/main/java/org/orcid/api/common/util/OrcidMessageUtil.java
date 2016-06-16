/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.api.common.util;

import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.manager.SourceNameCacheManager;
import org.orcid.jaxb.model.message.Address;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.Affiliations;
import org.orcid.jaxb.model.message.ExternalIdentifier;
import org.orcid.jaxb.model.message.ExternalIdentifiers;
import org.orcid.jaxb.model.message.Funding;
import org.orcid.jaxb.model.message.FundingList;
import org.orcid.jaxb.model.message.Keyword;
import org.orcid.jaxb.model.message.Keywords;
import org.orcid.jaxb.model.message.OrcidActivities;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidSearchResult;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.OtherName;
import org.orcid.jaxb.model.message.OtherNames;
import org.orcid.jaxb.model.message.ResearcherUrl;
import org.orcid.jaxb.model.message.ResearcherUrls;
import org.orcid.jaxb.model.message.SourceName;
import org.orcid.pojo.ajaxForm.PojoUtil;

public class OrcidMessageUtil {
    @Resource
    private SourceNameCacheManager sourceNameCacheManager;
    
    public void setSourceName(OrcidMessage orcidMessage) {
        if(orcidMessage != null) {
            if(orcidMessage.getOrcidProfile() != null) {
                setSourceName(orcidMessage.getOrcidProfile());
            }
            
            if(orcidMessage.getOrcidSearchResults() != null) {
                List<OrcidSearchResult> searchResults = orcidMessage.getOrcidSearchResults().getOrcidSearchResult();
                for(OrcidSearchResult searchResult : searchResults) {
                    if(searchResult.getOrcidProfile() != null) {
                        setSourceName(searchResult.getOrcidProfile());
                    }
                }
            }
        }
    }
    
    public void setSourceName(OrcidProfile orcidProfile) {
        if(orcidProfile != null) {
            if(orcidProfile.getOrcidActivities() != null) {
                OrcidActivities orcidActivities = orcidProfile.getOrcidActivities();
                if(orcidActivities.getAffiliations() != null) {
                   Affiliations affs = orcidActivities.getAffiliations();
                   List<Affiliation> affList = affs.getAffiliation();
                   if(affList != null) {
                       for(Affiliation aff : affList) {
                           if(aff.retrieveSourcePath() != null) {
                               SourceName sourceName = getSourceName(aff.retrieveSourcePath());
                               aff.getSource().setSourceName(sourceName);
                           }
                       }
                   }
                }
                
                if(orcidActivities.getFundings() != null) {
                    FundingList fundingList = orcidActivities.getFundings();
                    List<Funding> fundings = fundingList.getFundings();
                    if(fundings != null) {
                        for(Funding funding : fundings) {
                            if(funding.retrieveSourcePath() != null) {
                                SourceName sourceName = getSourceName(funding.retrieveSourcePath());
                                funding.getSource().setSourceName(sourceName);
                            }
                        }
                    }
                    
                }
                
                if(orcidActivities.getOrcidWorks() != null) {
                    OrcidWorks orcidWorks = orcidActivities.getOrcidWorks();
                    List<OrcidWork> works = orcidWorks.getOrcidWork();
                    if(works != null) {
                        for(OrcidWork work : works) {
                            if(work.retrieveSourcePath() != null) {
                                SourceName sourceName = getSourceName(work.retrieveSourcePath());
                                work.getSource().setSourceName(sourceName);
                            }
                        }
                    }
                }
            }
            if(orcidProfile.getOrcidBio() != null) {
                OrcidBio orcidBio = orcidProfile.getOrcidBio();
                if(orcidBio.getContactDetails() != null) {
                    Address address = orcidBio.getContactDetails().getAddress();
                    if(address != null) {
                        if(address.getSource() != null && address.getSource().retrieveSourcePath() != null) {
                            SourceName sourceName = getSourceName(address.getSource().retrieveSourcePath());
                            address.getSource().setSourceName(sourceName);
                        }
                    }                    
                }
                
                if(orcidBio.getExternalIdentifiers() != null) {
                   ExternalIdentifiers extIds = orcidBio.getExternalIdentifiers();
                   List<ExternalIdentifier> extIdsList = extIds.getExternalIdentifier();
                   if(extIdsList != null) {
                       for(ExternalIdentifier extId : extIdsList) {
                           if(extId.getSource() != null && extId.getSource().retrieveSourcePath() != null) {
                               SourceName sourceName = getSourceName(extId.getSource().retrieveSourcePath());
                               extId.getSource().setSourceName(sourceName);
                           }
                       }
                   }
                }
                
                if(orcidBio.getKeywords() != null) {
                    Keywords keywords = orcidBio.getKeywords();
                    List<Keyword> keywordList = keywords.getKeyword();
                    if(keywordList != null) {
                        for(Keyword keyword : keywordList) {
                            if(keyword.getSource() != null && keyword.getSource().retrieveSourcePath() != null) {
                                SourceName sourceName = getSourceName(keyword.getSource().retrieveSourcePath());
                                keyword.getSource().setSourceName(sourceName);
                            }
                        }
                    }
                }
                
                if(orcidBio.getPersonalDetails() != null) {
                    OtherNames otherNames = orcidBio.getPersonalDetails().getOtherNames();
                    if(otherNames != null) {
                        List<OtherName> otherNameList = otherNames.getOtherName();
                        if(otherNameList != null) {
                            for(OtherName otherName : otherNameList) {
                                if(otherName.getSource() != null && otherName.getSource().retrieveSourcePath() != null) {
                                    SourceName sourceName = getSourceName(otherName.getSource().retrieveSourcePath());
                                    otherName.getSource().setSourceName(sourceName);
                                }
                            }
                        }
                    }
                }
                
                if(orcidBio.getResearcherUrls() != null) {
                    ResearcherUrls rUrls = orcidBio.getResearcherUrls();
                    List<ResearcherUrl> rUrlList = rUrls.getResearcherUrl();
                    if(rUrlList != null) {
                        for(ResearcherUrl rUrl : rUrlList) {
                            SourceName sourceName = getSourceName(rUrl.getSource().retrieveSourcePath());
                            rUrl.getSource().setSourceName(sourceName);
                        }
                    }
                }                                
            }            
        }
    }
    
    private SourceName getSourceName(String sourceId) {
        if(PojoUtil.isEmpty(sourceId)) {
            return null;
        }
        
        String source = sourceNameCacheManager.retrieve(sourceId);
        if(PojoUtil.isEmpty(source)) {
            return null;
        }
        
        return new SourceName(source);        
    }
}
