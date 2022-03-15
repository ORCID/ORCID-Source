package org.orcid.pojo;

import org.orcid.jaxb.model.v3.release.common.Subtitle;
import org.orcid.jaxb.model.v3.release.common.Title;
import org.orcid.jaxb.model.v3.release.common.TranslatedTitle;
import org.orcid.jaxb.model.v3.release.record.WorkContributors;
import org.orcid.jaxb.model.v3.release.record.WorkTitle;
import org.orcid.jaxb.model.v3.release.record.summary.WorkSummary;

import javax.xml.bind.annotation.XmlElement;
import java.math.BigInteger;
import java.util.List;

public class WorkSummaryExtended extends WorkSummary {
    @XmlElement(namespace = "http://www.orcid.org/ns/work")
    protected WorkContributors contributors;

    protected int numberOfContributors;
    protected List<ContributorsRolesAndSequences> contributorsGroupedByOrcid;

    private WorkSummaryExtended(WorkSummaryExtendedBuilder builder) {
        super.setPutCode(builder.putCode.longValue());
        WorkTitle wt = new WorkTitle();
        wt.setTitle(new Title(builder.title));
        wt.setSubtitle(new Subtitle(builder.subtitle));;
        wt.setTranslatedTitle(new TranslatedTitle(builder.translatedTitle));
        super.setTitle(wt);
//        this.wor = builder.workType;
//        this.lastName = builder.lastName;
//        this.age = builder.age;
//        this.phone = builder.phone;
//        this.address = builder.address;
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
        private String externalIdsJson;
        private String publicationYear;
        private String publicationMonth;
        private String publicationDay;
        private String visibility;
        private String displayIndex;
        private String contributors;

        public WorkSummaryExtendedBuilder(BigInteger putCode, String workType, String title) {
            this.putCode = putCode;
            this.workType = workType;
            this.title = title;
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

        public WorkSummaryExtendedBuilder externalIdsJson(String externalIdsJson) {
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

        public WorkSummaryExtendedBuilder displayIndex(String displayIndex) {
            this.displayIndex = displayIndex;
            return this;
        }

        public WorkSummaryExtendedBuilder contributors(String contributors) {
            this.contributors = contributors;
            return this;
        }

        public WorkSummaryExtended build() {
            WorkSummaryExtended user =  new WorkSummaryExtended(this);
            return user;
        }
    }
}


