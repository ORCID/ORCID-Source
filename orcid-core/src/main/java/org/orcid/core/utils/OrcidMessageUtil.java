package org.orcid.core.utils;

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
                           setSourceName(aff);
                       }
                   }
                }
                
                if(orcidActivities.getFundings() != null) {
                    FundingList fundingList = orcidActivities.getFundings();
                    List<Funding> fundings = fundingList.getFundings();
                    if(fundings != null) {
                        for(Funding funding : fundings) {
                            setSourceName(funding);
                        }
                    }
                    
                }
                
                if(orcidActivities.getOrcidWorks() != null) {
                    OrcidWorks orcidWorks = orcidActivities.getOrcidWorks();
                    List<OrcidWork> works = orcidWorks.getOrcidWork();
                    if(works != null) {
                        for(OrcidWork work : works) {
                            setSourceName(work);
                        }
                    }
                }
            }
            if(orcidProfile.getOrcidBio() != null) {
                OrcidBio orcidBio = orcidProfile.getOrcidBio();
                if(orcidBio.getContactDetails() != null) {
                    Address address = orcidBio.getContactDetails().getAddress();
                    if(address != null) {
                        setSourceName(address);
                    }                    
                }
                
                if(orcidBio.getExternalIdentifiers() != null) {
                   ExternalIdentifiers extIds = orcidBio.getExternalIdentifiers();
                   List<ExternalIdentifier> extIdsList = extIds.getExternalIdentifier();
                   if(extIdsList != null) {
                       for(ExternalIdentifier extId : extIdsList) {
                           setSourceName(extId);
                       }
                   }
                }
                
                if(orcidBio.getKeywords() != null) {
                    Keywords keywords = orcidBio.getKeywords();
                    List<Keyword> keywordList = keywords.getKeyword();
                    if(keywordList != null) {
                        for(Keyword keyword : keywordList) {
                            setSourceName(keyword);
                        }
                    }
                }
                
                if(orcidBio.getPersonalDetails() != null) {
                    OtherNames otherNames = orcidBio.getPersonalDetails().getOtherNames();
                    if(otherNames != null) {
                        List<OtherName> otherNameList = otherNames.getOtherName();
                        if(otherNameList != null) {
                            for(OtherName otherName : otherNameList) {
                                setSourceName(otherName);
                            }
                        }
                    }
                }
                
                if(orcidBio.getResearcherUrls() != null) {
                    ResearcherUrls rUrls = orcidBio.getResearcherUrls();
                    List<ResearcherUrl> rUrlList = rUrls.getResearcherUrl();
                    if(rUrlList != null) {
                        for(ResearcherUrl rUrl : rUrlList) {
                            setSourceName(rUrl);
                        }
                    }
                }                                
            }            
        }
    }
    
    public void setSourceName(ResearcherUrl element) {
        if(element.getSource() != null && element.getSource().retrieveSourcePath() != null) {
            SourceName sourceName = getSourceName(element.getSource().retrieveSourcePath());
            element.getSource().setSourceName(sourceName);
        }
    }
    
    public void setSourceName(OtherName element) {
        if(element.getSource() != null && element.getSource().retrieveSourcePath() != null) {
            SourceName sourceName = getSourceName(element.getSource().retrieveSourcePath());
            element.getSource().setSourceName(sourceName);
        }
    }
    
    public void setSourceName(Keyword element) {
        if(element.getSource() != null && element.getSource().retrieveSourcePath() != null) {
            SourceName sourceName = getSourceName(element.getSource().retrieveSourcePath());
            element.getSource().setSourceName(sourceName);
        }
    }
    
    public void setSourceName(ExternalIdentifier element) {
        if(element.getSource() != null && element.getSource().retrieveSourcePath() != null) {
            SourceName sourceName = getSourceName(element.getSource().retrieveSourcePath());
            element.getSource().setSourceName(sourceName);
        }
    }
    
    public void setSourceName(Address element) {        
        if(element.getCountry() != null && element.getCountry().getSource() != null && element.getCountry().getSource().retrieveSourcePath() != null) {
            SourceName sourceName = getSourceName(element.getCountry().getSource().retrieveSourcePath());
            element.getCountry().getSource().setSourceName(sourceName);
        }
    }
    
    public void setSourceName(OrcidWork element) {
        if(element.getSource() != null && element.getSource().retrieveSourcePath() != null) {
            SourceName sourceName = getSourceName(element.getSource().retrieveSourcePath());
            element.getSource().setSourceName(sourceName);
        }
    }
    
    public void setSourceName(Funding element) {
        if(element.getSource() != null && element.getSource().retrieveSourcePath() != null) {
            SourceName sourceName = getSourceName(element.getSource().retrieveSourcePath());
            element.getSource().setSourceName(sourceName);
        }
    }
    
    public void setSourceName(Affiliation element) {
        if(element.getSource() != null && element.getSource().retrieveSourcePath() != null) {
            SourceName sourceName = getSourceName(element.getSource().retrieveSourcePath());
            element.getSource().setSourceName(sourceName);
        }
    }           
    
    public SourceName getSourceName(String sourceId) {
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
