package org.orcid.core.utils.v3;

import java.util.List;

import org.orcid.core.manager.SourceNameCacheManager;
import org.orcid.jaxb.model.record.bulk.BulkElement;
import org.orcid.jaxb.model.v3.rc1.common.Source;
import org.orcid.jaxb.model.v3.rc1.common.SourceName;
import org.orcid.jaxb.model.v3.rc1.record.Address;
import org.orcid.jaxb.model.v3.rc1.record.Addresses;
import org.orcid.jaxb.model.v3.rc1.record.Email;
import org.orcid.jaxb.model.v3.rc1.record.Emails;
import org.orcid.jaxb.model.v3.rc1.record.Keyword;
import org.orcid.jaxb.model.v3.rc1.record.Keywords;
import org.orcid.jaxb.model.v3.rc1.record.OtherName;
import org.orcid.jaxb.model.v3.rc1.record.OtherNames;
import org.orcid.jaxb.model.v3.rc1.record.Person;
import org.orcid.jaxb.model.v3.rc1.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.rc1.record.PersonExternalIdentifiers;
import org.orcid.jaxb.model.v3.rc1.record.PersonalDetails;
import org.orcid.jaxb.model.v3.rc1.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.rc1.record.ResearcherUrls;
import org.orcid.jaxb.model.v3.rc1.record.SourceAware;
import org.orcid.jaxb.model.v3.rc1.record.Work;
import org.orcid.jaxb.model.v3.rc1.record.WorkBulk;
import org.orcid.jaxb.model.v3.rc1.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.AffiliationSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.Affiliations;
import org.orcid.jaxb.model.v3.rc1.record.summary.EducationSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.Educations;
import org.orcid.jaxb.model.v3.rc1.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.Employments;
import org.orcid.jaxb.model.v3.rc1.record.summary.FundingGroup;
import org.orcid.jaxb.model.v3.rc1.record.summary.FundingSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.Fundings;
import org.orcid.jaxb.model.v3.rc1.record.summary.PeerReviewGroup;
import org.orcid.jaxb.model.v3.rc1.record.summary.PeerReviewSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.PeerReviews;
import org.orcid.jaxb.model.v3.rc1.record.summary.WorkGroup;
import org.orcid.jaxb.model.v3.rc1.record.summary.WorkSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.Works;
import org.orcid.pojo.ajaxForm.PojoUtil;

public class SourceUtils {
    private SourceNameCacheManager sourceNameCacheManager;

    public void setSourceNameCacheManager(SourceNameCacheManager sourceNameCacheManager) {
        this.sourceNameCacheManager = sourceNameCacheManager;
    }

    public void setSourceName(SourceAware sourceAware) {
        if (sourceAware != null) {
            Source source = sourceAware.getSource();
            if (source != null) {
                String sourceId = source.retrieveSourcePath();
                if (!PojoUtil.isEmpty(sourceId)) {
                    String sourceName = sourceNameCacheManager.retrieve(sourceId);
                    if (!PojoUtil.isEmpty(sourceName)) {
                        source.setSourceName(new SourceName(sourceName));
                    } else {
                        source.setSourceName(null);
                    }
                }
            }
        }
    }

    public void setSourceName(WorkBulk bulk) {
        if (bulk != null) {
            if (!bulk.getBulk().isEmpty()) {
                for (BulkElement element : bulk.getBulk()) {
                    if (Work.class.isAssignableFrom(element.getClass())) {
                        setSourceName((Work) element);
                    }
                }
            }
        }
    }

    public void setSourceName(ActivitiesSummary as) {
        if (as == null) {
            return;
        }
        if (as.getEducations() != null) {
            Educations educations = as.getEducations();
            List<EducationSummary> list = educations.getSummaries();
            if (list != null) {
                for (EducationSummary summary : list) {
                    setSourceName(summary);
                }
            }
        }
        if (as.getEmployments() != null) {
            Employments employments = as.getEmployments();
            List<EmploymentSummary> list = employments.getSummaries();
            if (list != null) {
                for (EmploymentSummary summary : list) {
                    setSourceName(summary);
                }
            }
        }
        if (as.getFundings() != null) {
            Fundings fundings = as.getFundings();
            List<FundingGroup> groups = fundings.getFundingGroup();
            if (groups != null) {
                for (FundingGroup group : groups) {
                    List<FundingSummary> summaryList = group.getFundingSummary();
                    if (summaryList != null) {
                        for (FundingSummary summary : summaryList) {
                            setSourceName(summary);
                        }
                    }
                }
            }
        }
        if (as.getPeerReviews() != null) {
            PeerReviews peerReviews = as.getPeerReviews();
            List<PeerReviewGroup> groups = peerReviews.getPeerReviewGroup();
            if (groups != null) {
                for (PeerReviewGroup group : groups) {
                    List<PeerReviewSummary> summaryList = group.getPeerReviewSummary();
                    if (summaryList != null) {
                        for (PeerReviewSummary summary : summaryList) {
                            setSourceName(summary);
                        }
                    }
                }
            }
        }
        if (as.getWorks() != null) {
            Works works = as.getWorks();
            List<WorkGroup> groups = works.getWorkGroup();
            if (groups != null) {
                for (WorkGroup group : groups) {
                    List<WorkSummary> summaryList = group.getWorkSummary();
                    if (summaryList != null) {
                        for (WorkSummary summary : summaryList) {
                            setSourceName(summary);
                        }
                    }
                }
            }
        }
    }

    public void setSourceName(PersonalDetails personalDetails) {
        if (personalDetails == null) {
            return;
        }

        if (personalDetails.getOtherNames() != null) {
            OtherNames otherNames = personalDetails.getOtherNames();
            setSourceName(otherNames);
        }
    }

    public void setSourceName(Person person) {
        if (person == null) {
            return;
        }

        if (person.getAddresses() != null) {
            Addresses addresses = person.getAddresses();
            setSourceName(addresses);
        }

        if (person.getEmails() != null) {
            Emails emails = person.getEmails();
            setSourceName(emails);
        }

        if (person.getExternalIdentifiers() != null) {
            PersonExternalIdentifiers extIds = person.getExternalIdentifiers();
            setSourceName(extIds);
        }

        if (person.getKeywords() != null) {
            Keywords keywords = person.getKeywords();
            setSourceName(keywords);
        }

        if (person.getOtherNames() != null) {
            OtherNames otherNames = person.getOtherNames();
            setSourceName(otherNames);
        }

        if (person.getResearcherUrls() != null) {
            ResearcherUrls researcherUrls = person.getResearcherUrls();
            setSourceName(researcherUrls);
        }
    }

    public void setSourceName(Addresses addresses) {
        List<Address> list = addresses.getAddress();
        if (list != null) {
            for (SourceAware element : list) {
                setSourceName(element);
            }
        }
    }

    public void setSourceName(Emails emails) {
        List<Email> list = emails.getEmails();
        if (list != null) {
            for (SourceAware element : list) {
                setSourceName(element);
            }
        }
    }

    public void setSourceName(PersonExternalIdentifiers extIds) {
        List<PersonExternalIdentifier> list = extIds.getExternalIdentifiers();
        if (list != null) {
            for (SourceAware element : list) {
                setSourceName(element);
            }
        }
    }

    public void setSourceName(Keywords keywords) {
        List<Keyword> list = keywords.getKeywords();
        if (list != null) {
            for (SourceAware element : list) {
                setSourceName(element);
            }
        }
    }

    public void setSourceName(OtherNames otherNames) {
        List<OtherName> list = otherNames.getOtherNames();
        if (list != null) {
            for (SourceAware element : list) {
                setSourceName(element);
            }
        }
    }

    public void setSourceName(ResearcherUrls researcherUrls) {
        List<ResearcherUrl> list = researcherUrls.getResearcherUrls();
        if (list != null) {
            for (SourceAware element : list) {
                setSourceName(element);
            }
        }
    }
    
    public void setSourceName(Affiliations<? extends AffiliationSummary> affiliations) {
        if (affiliations != null) {
            for (AffiliationSummary summary : affiliations.getSummaries()) {
                setSourceName(summary);
            }
        }
    }

    public void setSourceName(Works works) {
        if (works != null) {
            for (WorkGroup group : works.getWorkGroup()) {
                for (WorkSummary summary : group.getWorkSummary()) {
                    setSourceName(summary);
                }
            }
        }
    }

    public void setSourceName(Fundings fundings) {
        if (fundings != null) {
            for (FundingGroup group : fundings.getFundingGroup()) {
                for (FundingSummary summary : group.getFundingSummary()) {
                    setSourceName(summary);
                }
            }
        }
    }

    public void setSourceName(PeerReviews peerReviews) {
        if (peerReviews != null) {
            for (PeerReviewGroup group : peerReviews.getPeerReviewGroup()) {
                for (PeerReviewSummary summary : group.getPeerReviewSummary()) {
                    setSourceName(summary);
                }
            }
        }
    }
}
