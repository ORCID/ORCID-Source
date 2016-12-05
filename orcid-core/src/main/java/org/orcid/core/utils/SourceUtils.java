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
package org.orcid.core.utils;

import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.manager.SourceNameCacheManager;
import org.orcid.jaxb.model.common_rc4.Source;
import org.orcid.jaxb.model.common_rc4.SourceName;
import org.orcid.jaxb.model.record.summary_rc4.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_rc4.EducationSummary;
import org.orcid.jaxb.model.record.summary_rc4.Educations;
import org.orcid.jaxb.model.record.summary_rc4.EmploymentSummary;
import org.orcid.jaxb.model.record.summary_rc4.Employments;
import org.orcid.jaxb.model.record.summary_rc4.FundingGroup;
import org.orcid.jaxb.model.record.summary_rc4.FundingSummary;
import org.orcid.jaxb.model.record.summary_rc4.Fundings;
import org.orcid.jaxb.model.record.summary_rc4.PeerReviewGroup;
import org.orcid.jaxb.model.record.summary_rc4.PeerReviewSummary;
import org.orcid.jaxb.model.record.summary_rc4.PeerReviews;
import org.orcid.jaxb.model.record.summary_rc4.WorkGroup;
import org.orcid.jaxb.model.record.summary_rc4.WorkSummary;
import org.orcid.jaxb.model.record.summary_rc4.Works;
import org.orcid.jaxb.model.record_rc4.Address;
import org.orcid.jaxb.model.record_rc4.Addresses;
import org.orcid.jaxb.model.record_rc4.BulkElement;
import org.orcid.jaxb.model.record_rc4.Email;
import org.orcid.jaxb.model.record_rc4.Emails;
import org.orcid.jaxb.model.record_rc4.Keyword;
import org.orcid.jaxb.model.record_rc4.Keywords;
import org.orcid.jaxb.model.record_rc4.OtherName;
import org.orcid.jaxb.model.record_rc4.OtherNames;
import org.orcid.jaxb.model.record_rc4.Person;
import org.orcid.jaxb.model.record_rc4.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_rc4.PersonExternalIdentifiers;
import org.orcid.jaxb.model.record_rc4.PersonalDetails;
import org.orcid.jaxb.model.record_rc4.ResearcherUrl;
import org.orcid.jaxb.model.record_rc4.ResearcherUrls;
import org.orcid.jaxb.model.record_rc4.SourceAware;
import org.orcid.jaxb.model.record_rc4.Work;
import org.orcid.jaxb.model.record_rc4.WorkBulk;
import org.orcid.pojo.ajaxForm.PojoUtil;

public class SourceUtils {
    @Resource
    private SourceNameCacheManager sourceNameCacheManager;
    
    public void setSourceName(SourceAware sourceAware) {
        if(sourceAware != null) {
            Source source = sourceAware.getSource();
            if(source != null) {
                String sourceId = source.retrieveSourcePath();
                if(!PojoUtil.isEmpty(sourceId)) {
                    String sourceName = sourceNameCacheManager.retrieve(sourceId);
                    if(!PojoUtil.isEmpty(sourceName)) {
                        source.setSourceName(new SourceName(sourceName));
                    } else {
                        source.setSourceName(null);
                    }
                }
            }
        }
    }
    
    public void setSourceName(WorkBulk bulk) {
        if(bulk != null) {
            if(!bulk.getBulk().isEmpty()) {
                for(BulkElement element : bulk.getBulk()) {
                    if(Work.class.isAssignableFrom(element.getClass())) {
                        setSourceName((Work)element);
                    }
                }
            }
        }
    }
    
    public void setSourceName(ActivitiesSummary as) {
        if(as == null) {
            return;
        }
        if(as.getEducations() != null){
            Educations educations = as.getEducations();
            List<EducationSummary> list = educations.getSummaries();
            if(list != null) {
                for(EducationSummary summary : list) {
                    setSourceName(summary);
                }
            }
        }
        if(as.getEmployments() != null){
            Employments employments = as.getEmployments();
            List<EmploymentSummary> list = employments.getSummaries();
            if(list != null) {
                for(EmploymentSummary summary : list) {
                    setSourceName(summary);
                }
            }
        }
        if(as.getFundings() != null){
            Fundings fundings = as.getFundings();
            List<FundingGroup> groups = fundings.getFundingGroup();
            if(groups != null) {
                for(FundingGroup group : groups) {
                    List<FundingSummary> summaryList = group.getFundingSummary();
                    if(summaryList != null) {
                        for(FundingSummary summary : summaryList) {
                            setSourceName(summary);
                        }
                    }
                }
            }
        }
        if(as.getPeerReviews() != null){
            PeerReviews peerReviews = as.getPeerReviews();
            List<PeerReviewGroup> groups = peerReviews.getPeerReviewGroup();
            if(groups != null) {
                for(PeerReviewGroup group : groups) {
                    List<PeerReviewSummary> summaryList = group.getPeerReviewSummary();
                    if(summaryList != null) {
                        for(PeerReviewSummary summary : summaryList) {
                            setSourceName(summary);
                        }
                    }
                }
            }
        }
        if(as.getWorks() != null){
            Works works = as.getWorks();
            List<WorkGroup> groups = works.getWorkGroup();
            if(groups != null) {
                for(WorkGroup group : groups) {
                    List<WorkSummary> summaryList = group.getWorkSummary();
                    if(summaryList != null) {
                        for(WorkSummary summary : summaryList) {
                            setSourceName(summary);
                        }
                    }
                }
            }
        }
    }
    
    public void setSourceName(PersonalDetails personalDetails) {
        if(personalDetails == null) {
            return;
        }
        
        if(personalDetails.getOtherNames() != null) {
            OtherNames otherNames = personalDetails.getOtherNames();
            setSourceName(otherNames);
        }
    }
    
    public void setSourceName(Person person) {
        if(person == null) {
            return;
        }
        
        if(person.getAddresses() != null) {
            Addresses addresses = person.getAddresses();
            setSourceName(addresses); 
        }
        
        if(person.getEmails() != null) {
            Emails emails = person.getEmails();
            setSourceName(emails); 
        }
        
        if(person.getExternalIdentifiers() != null) {
            PersonExternalIdentifiers extIds = person.getExternalIdentifiers();
            setSourceName(extIds); 
        }
        
        if(person.getKeywords() != null) {
            Keywords keywords = person.getKeywords();
            setSourceName(keywords);
        }
        
        if(person.getOtherNames() != null) {
            OtherNames otherNames = person.getOtherNames();
            setSourceName(otherNames);
        }
        
        if(person.getResearcherUrls() != null) {
            ResearcherUrls researcherUrls = person.getResearcherUrls();
            setSourceName(researcherUrls);
        }
    }
    
    public void setSourceName(Addresses addresses) {
        List<Address> list = addresses.getAddress();
        if(list != null) {
            for(SourceAware element : list) {
                setSourceName(element);
            }
        }    
    }
    
    public void setSourceName(Emails emails) {
        List<Email> list = emails.getEmails();
        if(list != null) {
            for(SourceAware element : list) {
                setSourceName(element);
            }
        }
    }
    
    public void setSourceName(PersonExternalIdentifiers extIds) {
        List<PersonExternalIdentifier> list = extIds.getExternalIdentifiers();
        if(list != null) {
            for(SourceAware element : list) {
                setSourceName(element);
            }
        }
    }
    
    public void setSourceName(Keywords keywords) {
        List<Keyword> list = keywords.getKeywords();
        if(list != null) {
            for(SourceAware element : list) {
                setSourceName(element);
            }
        }
    }

    public void setSourceName(OtherNames otherNames) {
        List<OtherName> list = otherNames.getOtherNames();
        if(list != null) {
            for(SourceAware element : list) {
                setSourceName(element);
            }
        }
    }
    
    public void setSourceName(ResearcherUrls researcherUrls) {
        List<ResearcherUrl> list = researcherUrls.getResearcherUrls();
        if(list != null) {
            for(SourceAware element : list) {
                setSourceName(element);
            }
        }
    }
    
    public void setSourceName(Works works) {
        if(works != null) {
            for(WorkGroup group : works.getWorkGroup()) {
                for(WorkSummary summary : group.getWorkSummary()) {
                    setSourceName(summary);
                }                
            }
        }
    }
    
    public void setSourceName(Fundings fundings) {
        if(fundings != null) {
            for(FundingGroup group : fundings.getFundingGroup()) {
                for(FundingSummary summary : group.getFundingSummary()) {
                    setSourceName(summary);
                }                
            }
        }
    }
    
    public void setSourceName(PeerReviews peerReviews) {
        if(peerReviews != null) {
            for(PeerReviewGroup group : peerReviews.getPeerReviewGroup()) {
                for(PeerReviewSummary summary : group.getPeerReviewSummary()) {
                    setSourceName(summary);
                }                
            }
        }
    }
}
