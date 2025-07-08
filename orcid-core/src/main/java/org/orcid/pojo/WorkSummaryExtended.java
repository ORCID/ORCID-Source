package org.orcid.pojo;

import org.orcid.jaxb.model.common.WorkType;
import org.orcid.jaxb.model.v3.release.common.Contributor;
import org.orcid.jaxb.model.v3.release.common.CreatedDate;
import org.orcid.jaxb.model.v3.release.common.Day;
import org.orcid.jaxb.model.v3.release.common.LastModifiedDate;
import org.orcid.jaxb.model.v3.release.common.Month;
import org.orcid.jaxb.model.v3.release.common.PublicationDate;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.SourceClientId;
import org.orcid.jaxb.model.v3.release.common.SourceName;
import org.orcid.jaxb.model.v3.release.common.SourceOrcid;
import org.orcid.jaxb.model.v3.release.common.Title;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.common.Year;
import org.orcid.jaxb.model.v3.release.record.ExternalIDs;
import org.orcid.jaxb.model.v3.release.record.WorkContributors;
import org.orcid.jaxb.model.v3.release.record.WorkTitle;
import org.orcid.jaxb.model.v3.release.record.summary.WorkSummary;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class WorkSummaryExtended extends WorkSummary {
    @XmlElement(namespace = "http://www.orcid.org/ns/work")
    protected WorkContributors contributors;

    protected int numberOfContributors;
    protected List<ContributorsRolesAndSequences> contributorsGroupedByOrcid;

    private WorkSummaryExtended(WorkSummaryExtendedBuilder builder) {
        super.setPutCode(builder.putCode.longValue());
        if (builder.workType != null) {
            super.setType(WorkType.valueOf(builder.workType));
            WorkTitle wt = new WorkTitle();
            wt.setTitle(new Title(builder.title));
            super.setTitle(wt);
            super.setJournalTitle(new Title(builder.journalTitle));
            super.setExternalIdentifiers(builder.externalIdsJson);
            PublicationDate pd = null;
            if (builder.publicationYear != null) {
                Year year = new Year();
                year.setValue(builder.publicationYear);
                Month month = new Month();
                month.setValue(builder.publicationMonth);
                Day day = new Day();
                day.setValue(builder.publicationDay);
                pd = new PublicationDate(year, month, day);
            }
            super.setPublicationDate(pd);
            super.setVisibility(Visibility.valueOf(builder.visibility));
            super.setDisplayIndex(builder.displayIndex.toString());
            super.setFeaturedDisplayIndex(builder.featuredDisplayIndex.toString());
            Source s;
            if (builder.clientSourceId != null) {
                s = new Source(builder.clientSourceId);
            } else {
                s = new Source(builder.sourceId);
            }
            s.setSourceName(new SourceName(builder.sourceName));
            s.setAssertionOriginOrcid(new SourceOrcid(builder.assertionOriginSourceId));
            s.setAssertionOriginClientId(new SourceClientId(builder.assertionOriginClientSourceId));
            s.setAssertionOriginName(new SourceName(builder.assertionOriginName));
            super.setSource(s);
            super.setCreatedDate(new CreatedDate(builder.createdDate));
            super.setLastModifiedDate(new LastModifiedDate(builder.lastModifiedDate));
        }
        this.contributors = builder.contributors;
        this.contributorsGroupedByOrcid = builder.topContributors;
    }

    public WorkSummaryExtended() { }

    public WorkContributors getContributors() {
        return contributors;
    }

    public void setContributors(WorkContributors contributors) {
        this.contributors = contributors;
    }

    public List<ContributorsRolesAndSequences> getContributorsGroupedByOrcid() {
        return contributorsGroupedByOrcid;
    }

    public void setContributorsGroupedByOrcid(List<ContributorsRolesAndSequences> contributorsGroupedByOrcid) {
        this.contributorsGroupedByOrcid = contributorsGroupedByOrcid;
    }

    public int getNumberOfContributors() {
        return numberOfContributors;
    }

    public void setNumberOfContributors(int numberOfContributors) {
        this.numberOfContributors = numberOfContributors;
    }

    public static class WorkSummaryExtendedBuilder {
        private final BigInteger putCode;
        private String title;
        private String workType;
        private String journalTitle;
        private ExternalIDs externalIdsJson;
        private String publicationYear;
        private String publicationMonth;
        private String publicationDay;
        private String visibility;
        private String sourceId;
        private String clientSourceId;
        private String sourceName;
        private String assertionOriginSourceId;
        private String assertionOriginClientSourceId;
        private String assertionOriginName;
        private XMLGregorianCalendar createdDate;
        private XMLGregorianCalendar lastModifiedDate;
        private BigInteger displayIndex;
        private WorkContributors contributors;
        private List<ContributorsRolesAndSequences> topContributors;
        private BigInteger featuredDisplayIndex;

        public WorkSummaryExtendedBuilder(
                BigInteger putCode, String workType, String title, String sourceId, String clientSourceId,
                Timestamp createdDate, Timestamp lastModifiedDate
                ) {
            this.putCode = putCode;
            this.workType = workType;
            this.title = title;
            this.sourceId = sourceId;
            this.clientSourceId = clientSourceId;
            this.createdDate = addDate(createdDate);
            this.lastModifiedDate = addDate(lastModifiedDate);
        }

        public WorkSummaryExtendedBuilder(BigInteger putCode) {
            this.putCode = putCode;
        }

        public WorkSummaryExtendedBuilder journalTitle(String journalTitle) {
            this.journalTitle = journalTitle;
            return this;
        }

        public WorkSummaryExtendedBuilder externalIdsJson(ExternalIDs externalIdsJson) {
            this.externalIdsJson = externalIdsJson;
            return this;
        }

        public WorkSummaryExtendedBuilder publicationYear(String publicationYear) {
            this.publicationYear = publicationYear;
            return this;
        }

        public WorkSummaryExtendedBuilder publicationMonth(String publicationMonth) {
            this.publicationMonth = publicationMonth;
            return this;
        }

        public WorkSummaryExtendedBuilder publicationDay(String publicationDay) {
            this.publicationDay = publicationDay;
            return this;
        }

        public WorkSummaryExtendedBuilder visibility(String visibility) {
            this.visibility = visibility;
            return this;
        }

        public WorkSummaryExtendedBuilder displayIndex(BigInteger displayIndex) {
            this.displayIndex = displayIndex;
            return this;
        }

        public WorkSummaryExtendedBuilder sourceName(String sourceName) {
            this.sourceName = sourceName;
            return this;
        }

        public WorkSummaryExtendedBuilder assertionOriginName(String assertionOriginName) {
            this.assertionOriginName = assertionOriginName;
            return this;
        }

        public WorkSummaryExtendedBuilder assertionOriginSourceId(String assertionOriginSourceId) {
            this.assertionOriginSourceId = assertionOriginSourceId;
            return this;
        }

        public WorkSummaryExtendedBuilder assertionOriginClientSourceId(String assertionOriginClientSourceId) {
            this.assertionOriginClientSourceId = assertionOriginClientSourceId;
            return this;
        }

        public WorkSummaryExtendedBuilder createdDate(Timestamp createdDate) {
            this.createdDate = addDate(createdDate);
            return this;
        }

        public WorkSummaryExtendedBuilder lastModifiedDate(Timestamp lastModifiedDate) {
            this.lastModifiedDate = addDate(lastModifiedDate);
            return this;
        }

        public WorkSummaryExtendedBuilder contributors(List<WorkContributorsList> workContributorsList) {
            List<Contributor> contributorList = new ArrayList<>();
            workContributorsList.forEach(c -> {
                if (c.getContributor() != null) {
                    contributorList.add(c.getContributor());
                }
            });

            this.contributors = new WorkContributors(contributorList);
            return this;
        }

        public WorkSummaryExtendedBuilder topContributors(List<ContributorsRolesAndSequences> topContributors) {
            this.topContributors = topContributors;
            return this;
        }

        public WorkSummaryExtendedBuilder featuredDisplayIndex(BigInteger featuredDisplayIndex) {
            return this;
        }

        public WorkSummaryExtended build() {
            return new WorkSummaryExtended(this);
        }

        private XMLGregorianCalendar addDate(Timestamp ts) {
            XMLGregorianCalendar cal = null;
            try {
                LocalDateTime ldt = ts.toLocalDateTime();
                cal = DatatypeFactory.newInstance().newXMLGregorianCalendar();
                cal.setYear(ldt.getYear());
                cal.setMonth(ldt.getMonthValue());
                cal.setDay(ldt.getDayOfMonth());
                cal.setHour(ldt.getHour());
                cal.setMinute(ldt.getMinute());
                cal.setSecond(ldt.getSecond());
            } catch (DatatypeConfigurationException e) {
                e.printStackTrace();
            }
            return cal;
        }
    }
}


