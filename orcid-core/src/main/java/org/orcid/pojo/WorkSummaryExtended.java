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
import org.orcid.jaxb.model.v3.release.common.Subtitle;
import org.orcid.jaxb.model.v3.release.common.Title;
import org.orcid.jaxb.model.v3.release.common.TranslatedTitle;
import org.orcid.jaxb.model.v3.release.common.Url;
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
        super.setType(WorkType.valueOf(builder.workType));
        WorkTitle wt = new WorkTitle();
        wt.setTitle(new Title(builder.title));
        wt.setSubtitle(new Subtitle(builder.subtitle));;
        wt.setTranslatedTitle(new TranslatedTitle(builder.translatedTitle));
        super.setTitle(wt);
        super.setUrl(new Url(builder.workUrl));
        super.setJournalTitle(new Title(builder.journalTitle));
        super.setExternalIdentifiers(builder.externalIdsJson);
        Year year = new Year();
        year.setValue(builder.publicationYear);
        Month month = new Month();
        month.setValue(builder.publicationMonth);
        Day day = new Day();
        day.setValue(builder.publicationYear);
        PublicationDate pd = new PublicationDate(year, month, day);
        super.setPublicationDate(pd);
        super.setVisibility(Visibility.valueOf(builder.visibility));
        super.setDisplayIndex(builder.displayIndex.toString());
        Source s = new Source();
        s.setSourceOrcid(new SourceOrcid(builder.sourceId));
        s.setSourceName(new SourceName(builder.clientName));
        s.setSourceClientId(new SourceClientId(builder.clientSourceId));
        super.setSource(s);
        super.setCreatedDate(new CreatedDate(builder.createdDate));
        super.setLastModifiedDate(new LastModifiedDate(builder.lastModifiedDate));
        this.contributors = builder.contributors;
    }

    public WorkSummaryExtended() { }

    public WorkContributors getContributors() {
        return contributors;
    }

//    todo remove Builder Pattern
    public void setContributors(WorkContributors contributors) {
        this.contributors = contributors;
    }

    public List<ContributorsRolesAndSequences> getContributorsGroupedByOrcid() {
        return contributorsGroupedByOrcid;
    }

    //    todo remove Builder Pattern
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
        private String subtitle;
        private String description;
        private String workUrl;
        private String journalTitle;
        private String languageCode;
        private String translatedTitle;
        private String translatedTitleLanguageCode;
        private ExternalIDs externalIdsJson;
        private String publicationYear;
        private String publicationMonth;
        private String publicationDay;
        private String visibility;
        private String sourceId;
        private String clientSourceId;
        private String clientName;
        private XMLGregorianCalendar createdDate;
        private XMLGregorianCalendar lastModifiedDate;
        private BigInteger displayIndex;
        private WorkContributors contributors;

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

        public WorkSummaryExtendedBuilder description(String description) {
            this.description = description;
            return this;
        }

        public WorkSummaryExtendedBuilder workUrl(String workUrl) {
            this.workUrl = workUrl;
            return this;
        }

        public WorkSummaryExtendedBuilder subtitle(String subtitle) {
            this.subtitle = subtitle;
            return this;
        }

        public WorkSummaryExtendedBuilder journalTitle(String journalTitle) {
            this.journalTitle = journalTitle;
            return this;
        }

        public WorkSummaryExtendedBuilder languageCode(String languageCode) {
            this.languageCode = languageCode;
            return this;
        }

        public WorkSummaryExtendedBuilder translatedTitle(String translatedTitle) {
            this.translatedTitle = translatedTitle;
            return this;
        }

        public WorkSummaryExtendedBuilder translatedTitleLanguageCode(String translatedTitleLanguageCode) {
            this.translatedTitleLanguageCode = translatedTitleLanguageCode;
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

        public WorkSummaryExtendedBuilder clientName(String clientName) {
            this.clientName = clientName;
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

        public WorkSummaryExtended build() {
            WorkSummaryExtended user =  new WorkSummaryExtended(this);
            return user;
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


