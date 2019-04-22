package org.orcid.core.utils.v3;

import java.util.List;

import org.orcid.core.manager.SourceNameCacheManager;
import org.orcid.jaxb.model.record.bulk.BulkElement;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.SourceName;
import org.orcid.jaxb.model.v3.release.record.Address;
import org.orcid.jaxb.model.v3.release.record.Addresses;
import org.orcid.jaxb.model.v3.release.record.Email;
import org.orcid.jaxb.model.v3.release.record.Emails;
import org.orcid.jaxb.model.v3.release.record.Keyword;
import org.orcid.jaxb.model.v3.release.record.Keywords;
import org.orcid.jaxb.model.v3.release.record.OtherName;
import org.orcid.jaxb.model.v3.release.record.OtherNames;
import org.orcid.jaxb.model.v3.release.record.Person;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifiers;
import org.orcid.jaxb.model.v3.release.record.PersonalDetails;
import org.orcid.jaxb.model.v3.release.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.release.record.ResearcherUrls;
import org.orcid.jaxb.model.v3.release.record.SourceAware;
import org.orcid.jaxb.model.v3.release.record.Work;
import org.orcid.jaxb.model.v3.release.record.WorkBulk;
import org.orcid.jaxb.model.v3.release.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.release.record.summary.AffiliationGroup;
import org.orcid.jaxb.model.v3.release.record.summary.AffiliationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Affiliations;
import org.orcid.jaxb.model.v3.release.record.summary.EducationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Educations;
import org.orcid.jaxb.model.v3.release.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Employments;
import org.orcid.jaxb.model.v3.release.record.summary.FundingGroup;
import org.orcid.jaxb.model.v3.release.record.summary.FundingSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Fundings;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewDuplicateGroup;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewGroup;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewSummary;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviews;
import org.orcid.jaxb.model.v3.release.record.summary.ResearchResourceGroup;
import org.orcid.jaxb.model.v3.release.record.summary.ResearchResourceSummary;
import org.orcid.jaxb.model.v3.release.record.summary.ResearchResources;
import org.orcid.jaxb.model.v3.release.record.summary.WorkGroup;
import org.orcid.jaxb.model.v3.release.record.summary.WorkSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Works;
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
            for (AffiliationGroup<EducationSummary> group : educations.retrieveGroups()) {
                List<EducationSummary> list = group.getActivities();
                if (list != null) {
                    for (EducationSummary summary : list) {
                        setSourceName(summary);
                    }
                }
            }
        }
        if (as.getEmployments() != null) {
            Employments employments = as.getEmployments();
            for (AffiliationGroup<EmploymentSummary> group : employments.retrieveGroups()) {
                List<EmploymentSummary> list = group.getActivities();
                if (list != null) {
                    for (EmploymentSummary summary : list) {
                        setSourceName(summary);
                    }
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
                    for (PeerReviewDuplicateGroup duplicateGroup : group.getPeerReviewGroup()) {
                        List<PeerReviewSummary> summaryList = duplicateGroup.getPeerReviewSummary();
                        if (summaryList != null) {
                            for (PeerReviewSummary summary : summaryList) {
                                setSourceName(summary);
                            }
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
            for (AffiliationGroup<? extends AffiliationSummary> group : affiliations.retrieveGroups()) {
                for (AffiliationSummary summary : group.getActivities()) {
                    setSourceName(summary);
                }
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
                for (PeerReviewDuplicateGroup duplicateGroup : group.getPeerReviewGroup()) {
                    for (PeerReviewSummary summary : duplicateGroup.getPeerReviewSummary()) {
                        setSourceName(summary);
                    }
                }
            }
        }
    }

    public void setSourceName(ResearchResources rr) {
        if (rr != null) {
            for (ResearchResourceGroup group : rr.getResearchResourceGroup()) {
                for (ResearchResourceSummary summary : group.getResearchResourceSummary()) {
                    setSourceName(summary);
                }
            }
        }
    }
}
