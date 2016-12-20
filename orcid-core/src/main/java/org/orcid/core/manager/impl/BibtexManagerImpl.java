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
package org.orcid.core.manager.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.manager.ActivitiesSummaryManager;
import org.orcid.core.manager.BibtexManager;
import org.orcid.core.manager.DOIManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.WorkManager;
import org.orcid.jaxb.model.common_rc4.Contributor;
import org.orcid.jaxb.model.record.summary_rc4.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_rc4.WorkGroup;
import org.orcid.jaxb.model.record.summary_rc4.WorkSummary;
import org.orcid.jaxb.model.record_rc1.WorkExternalIdentifierType;
import org.orcid.jaxb.model.record_rc4.CitationType;
import org.orcid.jaxb.model.record_rc4.ExternalID;
import org.orcid.jaxb.model.record_rc4.Work;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.RecordNameEntity;

import com.google.common.base.Joiner;

import de.undercouch.citeproc.CSL;
import de.undercouch.citeproc.csl.CSLItemData;
import de.undercouch.citeproc.csl.CSLItemDataBuilder;
import de.undercouch.citeproc.csl.CSLNameBuilder;
import de.undercouch.citeproc.csl.CSLType;

public class BibtexManagerImpl implements BibtexManager{

    @Resource
    private ActivitiesSummaryManager activitiesManager;
    
    @Resource
    private WorkManager workManager;
    
    @Resource
    private ProfileEntityManager profileEntityManager;
    
    @Resource 
    private DOIManager doiManager;
    
    private String bibtexStyle = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n<style xmlns=\"http://purl.org/net/xbiblio/csl\" class=\"in-text\" version=\"1.0\" demote-non-dropping-particle=\"sort-only\" default-locale=\"en-US\">\r\n  <info>\r\n    <title>BibTeX generic citation style</title>\r\n    <id>http://www.zotero.org/styles/bibtex</id>\r\n    <link href=\"http://www.zotero.org/styles/bibtex\" rel=\"self\"/>\r\n    <link href=\"http://www.bibtex.org/\" rel=\"documentation\"/>\r\n    <author>\r\n      <name>Markus Schaffner</name>\r\n    </author>\r\n    <contributor>\r\n      <name>Richard Karnesky</name>\r\n      <email>karnesky+zotero@gmail.com</email>\r\n      <uri>http://arc.nucapt.northwestern.edu/Richard_Karnesky</uri>\r\n    </contributor>\r\n    <category citation-format=\"author-date\"/>\r\n    <category field=\"generic-base\"/>\r\n    <updated>2012-09-14T21:22:32+00:00</updated>\r\n    <rights license=\"http://creativecommons.org/licenses/by-sa/3.0/\">This work is licensed under a Creative Commons Attribution-ShareAlike 3.0 License</rights>\r\n  </info>\r\n  <macro name=\"zotero2bibtexType\">\r\n    <choose>\r\n      <if type=\"bill book graphic legal_case legislation motion_picture report song\" match=\"any\">\r\n        <text value=\"book\"/>\r\n      </if>\r\n      <else-if type=\"chapter paper-conference\" match=\"any\">\r\n        <text value=\"inbook\"/>\r\n      </else-if>\r\n      <else-if type=\"article article-journal article-magazine article-newspaper\" match=\"any\">\r\n        <text value=\"article\"/>\r\n      </else-if>\r\n      <else-if type=\"thesis\" match=\"any\">\r\n        <text value=\"phdthesis\"/>\r\n      </else-if>\r\n      <else-if type=\"manuscript\" match=\"any\">\r\n        <text value=\"unpublished\"/>\r\n      </else-if>\r\n      <else-if type=\"paper-conference\" match=\"any\">\r\n        <text value=\"inproceedings\"/>\r\n      </else-if>\r\n      <else-if type=\"report\" match=\"any\">\r\n        <text value=\"techreport\"/>\r\n      </else-if>\r\n      <else>\r\n        <text value=\"misc\"/>\r\n      </else>\r\n    </choose>\r\n  </macro>\r\n  <macro name=\"citeKey\">\r\n    <group delimiter=\"_\">\r\n <text variable=\"id\"/>\r\n    </group>\r\n  </macro>\r\n  <macro name=\"author-short\">\r\n    <names variable=\"author\">\r\n      <name form=\"short\" delimiter=\"_\" delimiter-precedes-last=\"always\"/>\r\n      <substitute>\r\n        <names variable=\"editor\"/>\r\n        <names variable=\"translator\"/>\r\n        <choose>\r\n          <if type=\"bill book graphic legal_case legislation motion_picture report song\" match=\"any\">\r\n            <text variable=\"title\" form=\"short\"/>\r\n          </if>\r\n          <else>\r\n            <text variable=\"title\" form=\"short\"/>\r\n          </else>\r\n        </choose>\r\n      </substitute>\r\n    </names>\r\n  </macro>\r\n  <macro name=\"issued-year\">\r\n    <date variable=\"issued\">\r\n      <date-part name=\"year\"/>\r\n    </date>\r\n  </macro>\r\n  <macro name=\"issued-month\">\r\n    <date variable=\"issued\">\r\n      <date-part name=\"month\" form=\"short\" strip-periods=\"true\"/>\r\n    </date>\r\n  </macro>\r\n  <macro name=\"author\">\r\n    <names variable=\"author\">\r\n      <name sort-separator=\", \" delimiter=\" and \" delimiter-precedes-last=\"always\" name-as-sort-order=\"all\"/>\r\n      <label form=\"long\" text-case=\"capitalize-first\"/>\r\n    </names>\r\n  </macro>\r\n  <macro name=\"editor-translator\">\r\n    <names variable=\"editor translator\" delimiter=\", \">\r\n      <name sort-separator=\", \" delimiter=\" and \" delimiter-precedes-last=\"always\" name-as-sort-order=\"all\"/>\r\n      <label form=\"long\" text-case=\"capitalize-first\"/>\r\n    </names>\r\n  </macro>\r\n  <macro name=\"title\">\r\n    <text variable=\"title\"/>\r\n  </macro>\r\n  <macro name=\"number\">\r\n    <text variable=\"issue\"/>\r\n    <text variable=\"number\"/>\r\n  </macro>\r\n  <macro name=\"container-title\">\r\n    <choose>\r\n      <if type=\"chapter paper-conference\" match=\"any\">\r\n        <text variable=\"container-title\" prefix=\" booktitle={\" suffix=\"}\"/>\r\n      </if>\r\n      <else>\r\n        <text variable=\"container-title\" prefix=\" journal={\" suffix=\"}\"/>\r\n      </else>\r\n    </choose>\r\n  </macro>\r\n  <macro name=\"publisher\">\r\n    <choose>\r\n      <if type=\"thesis\">\r\n        <text variable=\"publisher\" prefix=\" school={\" suffix=\"}\"/>\r\n      </if>\r\n      <else-if type=\"report\">\r\n        <text variable=\"publisher\" prefix=\" institution={\" suffix=\"}\"/>\r\n      </else-if>\r\n      <else>\r\n        <text variable=\"publisher\" prefix=\" publisher={\" suffix=\"}\"/>\r\n      </else>\r\n    </choose>\r\n  </macro>\r\n  <macro name=\"pages\">\r\n    <text variable=\"page\"/>\r\n  </macro>\r\n  <macro name=\"edition\">\r\n    <text variable=\"edition\"/>\r\n  </macro>\r\n  <citation et-al-min=\"10\" et-al-use-first=\"10\" disambiguate-add-year-suffix=\"true\" disambiguate-add-names=\"false\" disambiguate-add-givenname=\"false\" collapse=\"year\">\r\n    <sort>\r\n      <key macro=\"author\"/>\r\n      <key variable=\"issued\"/>\r\n    </sort>\r\n    <layout delimiter=\"_\">\r\n      <text macro=\"citeKey\"/>\r\n    </layout>\r\n  </citation>\r\n  <bibliography hanging-indent=\"false\" et-al-min=\"10\" et-al-use-first=\"10\">\r\n    <sort>\r\n      <key macro=\"author\"/>\r\n      <key variable=\"issued\"/>\r\n    </sort>\r\n    <layout>\r\n      <text macro=\"zotero2bibtexType\" prefix=\" @\"/>\r\n      <group prefix=\"{\" suffix=\"}\" delimiter=\", \">\r\n        <text macro=\"citeKey\"/>\r\n        <text variable=\"publisher-place\" prefix=\" place={\" suffix=\"}\"/>\r\n        <!--Fix This-->\r\n        <text variable=\"chapter-number\" prefix=\" chapter={\" suffix=\"}\"/>\r\n        <!--Fix This-->\r\n        <text macro=\"edition\" prefix=\" edition={\" suffix=\"}\"/>\r\n        <!--Is this in CSL? <text variable=\"type\" prefix=\" type={\" suffix=\"}\"/>-->\r\n        <text variable=\"collection-title\" prefix=\" series={\" suffix=\"}\"/>\r\n        <text macro=\"title\" prefix=\" title={\" suffix=\"}\"/>\r\n        <text variable=\"volume\" prefix=\" volume={\" suffix=\"}\"/>\r\n        <!--Not in CSL<text variable=\"rights\" prefix=\" rights={\" suffix=\"}\"/>-->\r\n        <text variable=\"ISBN\" prefix=\" ISBN={\" suffix=\"}\"/>\r\n        <text variable=\"ISSN\" prefix=\" ISSN={\" suffix=\"}\"/>\r\n        <!--Not in CSL <text variable=\"LCCN\" prefix=\" callNumber={\" suffix=\"}\"/>-->\r\n        <text variable=\"archive_location\" prefix=\" archiveLocation={\" suffix=\"}\"/>\r\n        <text variable=\"URL\" prefix=\" url={\" suffix=\"}\"/>\r\n        <text variable=\"DOI\" prefix=\" DOI={\" suffix=\"}\"/>\r\n        <text variable=\"abstract\" prefix=\" abstractNote={\" suffix=\"}\"/>\r\n        <text variable=\"note\" prefix=\" note={\" suffix=\"}\"/>\r\n        <text macro=\"number\" prefix=\" number={\" suffix=\"}\"/>\r\n        <text macro=\"container-title\"/>\r\n        <text macro=\"publisher\"/>\r\n        <text macro=\"author\" prefix=\" author={\" suffix=\"}\"/>\r\n        <text macro=\"editor-translator\" prefix=\" editor={\" suffix=\"}\"/>\r\n        <text macro=\"issued-year\" prefix=\" year={\" suffix=\"}\"/>\r\n        <text macro=\"issued-month\" prefix=\" month={\" suffix=\"}\"/>\r\n        <text macro=\"pages\" prefix=\" pages={\" suffix=\"}\"/>\r\n        <text variable=\"collection-title\" prefix=\" collection={\" suffix=\"}\"/>\r\n      </group>\r\n    </layout>\r\n  </bibliography>\r\n</style>";
    
    @Override
    public String generateBibtexReferenceList(String orcid) {
        long last = workManager.getLastModified(orcid);
        ActivitiesSummary summary = activitiesManager.getActivitiesSummary(orcid);
        List<String> citations = new ArrayList<String>();
        if (summary.getWorks()!=null){
            for (WorkGroup group : summary.getWorks().getWorkGroup()){
                WorkSummary workSummary = group.getWorkSummary().get(0);
                Work work = workManager.getWork(orcid, workSummary.getPutCode(), last);
                String bibtex = generateBibtex(orcid,work); 
                if (bibtex != null)
                    citations.add(bibtex);
            }
        }
        
        return Joiner.on(",\n").join(citations);
    }
    
    @Override
    public String generateBibtex(String orcid, Work work){
        //if we have a citation use that
        if (work.getWorkCitation() != null && work.getWorkCitation().getWorkCitationType() != null
                && work.getWorkCitation().getWorkCitationType().equals(CitationType.BIBTEX)) {
               return work.getWorkCitation().getCitation();             
        }
        
        //if we have a DOI, use that
        if (work.getWorkExternalIdentifiers() != null && work.getWorkExternalIdentifiers().getExternalIdentifier() != null){
            String doi = extractID(work, WorkExternalIdentifierType.DOI);
            if (doi != null){
                String bibtex = doiManager.fetchDOIBibtex(doi);
                if (bibtex != null)
                    return bibtex;
            }
        }
        
        //otherwise, use whatever we can
        String creditName = getCreditName(orcid);
        try {
            CSLItemData data = translateFromWorkMetadata(work, creditName);
            return CSL.makeAdhocBibliography(bibtexStyle, "text",data).makeString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;       
        }
        
    }
    
    /**
     * Use the ORCID work metadata to generate a *limited* citation. You'll most
     * likely get a title, doi, url, date and author.
     * 
     * Translates type according to https://docs.google.com/spreadsheets/d/
     * 1h4nTF6DKNEpWcGNQVMDwt0ea09qmkBnkWisxkJE-li4/edit#gid=754644608
     * 
     * Informed by mendley tranforms at
     * http://support.mendeley.com/customer/portal/articles/364144-csl-type-
     * mapping
     * 
     * See also:
     * http://docs.citationstyles.org/en/stable/specification.html#appendix-iii-
     * types http://members.orcid.org/api/supported-work-types datacite and
     * crossref mappings here:
     * https://github.com/lagotto/lagotto/blob/master/config/initializers/
     * constants.rb
     * @param creditName 
     * 
     * @param worktype
     * @return a CSLItemData, default CSLType.ARTICLE if cannot map type
     */
    public CSLItemData translateFromWorkMetadata(Work work, String creditName) {
        CSLItemDataBuilder builder = new CSLItemDataBuilder();
        builder.id(creditName.replace(' ', '_')+work.getPutCode());
        
        builder.title((work.getWorkTitle() != null) ? StringUtils.stripAccents(work.getWorkTitle().getTitle().getContent()) : "No Title");
        String doi = extractID(work, WorkExternalIdentifierType.DOI);
        String url = extractID(work, WorkExternalIdentifierType.URI);
        if (doi != null) {
            builder.DOI(doi);
        }
        if (url != null) {
            builder.URL(url);
        } else if (doi != null) {
            builder.URL("http://doi.org/" + doi);
        } else {
            url = extractID(work, WorkExternalIdentifierType.HANDLE);
            if (url != null) {
                builder.URL(url);
            }
        }

        if (work.getJournalTitle() != null) {
            builder.containerTitle(StringUtils.stripAccents(work.getJournalTitle().getContent()));
        }

        List<String> names = new ArrayList<String>();
        names.add(creditName);
        if (work.getWorkContributors() != null && work.getWorkContributors().getContributor() != null) {
            for (Contributor c : work.getWorkContributors().getContributor()) {
                if (c.getCreditName() != null && c.getCreditName().getContent() != null) {
                    names.add(StringUtils.stripAccents(c.getCreditName().getContent()));
                }
            }
        }
        CSLNameBuilder name = new CSLNameBuilder();
        name.literal(Joiner.on(" and ").skipNulls().join(names));
        builder.author(name.build());

        // TODO: make it work with "Spring", "August", whatever...
        if (work.getPublicationDate() != null) {
            int year = 0;
            int month = 0;
            int day = 0;
            try {
                year = Integer.parseInt(work.getPublicationDate().getYear().getValue());
                month = Integer.parseInt(work.getPublicationDate().getMonth().getValue());
                day = Integer.parseInt(work.getPublicationDate().getDay().getValue());
            } catch (Exception e) {
            }
            if (year > 0 && month > 0 && day > 0) {
                builder.issued(year, month, day);
            } else if (year > 0 && month > 0) {
                builder.issued(year, month);
            } else if (year > 0) {
                builder.issued(year);
            }

        }

        switch (work.getWorkType()) {
        case ARTISTIC_PERFORMANCE:
            break;
        case BOOK:
            builder.type(CSLType.BOOK);
            break;
        case BOOK_CHAPTER:
            builder.type(CSLType.CHAPTER);
            break;
        case BOOK_REVIEW:
            builder.type(CSLType.REVIEW_BOOK);
            break;
        case CONFERENCE_ABSTRACT:
            builder.type(CSLType.PAPER_CONFERENCE);
            break;
        case CONFERENCE_PAPER:
            builder.type(CSLType.PAPER_CONFERENCE);
            break;
        case CONFERENCE_POSTER:
            builder.type(CSLType.PAPER_CONFERENCE);
            break;
        case DATA_SET:
            builder.type(CSLType.DATASET);
            break;
        case DICTIONARY_ENTRY:
            builder.type(CSLType.ENTRY_DICTIONARY);
            break;
        case DISSERTATION:
            builder.type(CSLType.THESIS);
            break;
        case ENCYCLOPEDIA_ENTRY:
            builder.type(CSLType.ENTRY_ENCYCLOPEDIA);
            break;
        case JOURNAL_ARTICLE:
            builder.type(CSLType.ARTICLE_JOURNAL);
            break;
        case MAGAZINE_ARTICLE:
            builder.type(CSLType.ARTICLE_MAGAZINE);
            break;
        case NEWSLETTER_ARTICLE:
            builder.type(CSLType.ARTICLE_NEWSPAPER);
            break;
        case NEWSPAPER_ARTICLE:
            builder.type(CSLType.ARTICLE_NEWSPAPER);
            break;
        case ONLINE_RESOURCE:
            builder.type(CSLType.WEBPAGE);
            break;
        case REPORT:
            builder.type(CSLType.REPORT);
            break;
        case WEBSITE:
            builder.type(CSLType.WEBPAGE);
            break;
        case WORKING_PAPER:
            builder.type(CSLType.ARTICLE);
            break;
        case DISCLOSURE:
        case EDITED_BOOK:
        case INVENTION:
        case JOURNAL_ISSUE:
        case LECTURE_SPEECH:
        case LICENSE:
        case MANUAL:
        case OTHER:
        case PATENT:
        case REGISTERED_COPYRIGHT:
        case RESEARCH_TECHNIQUE:
        case RESEARCH_TOOL:
        case SPIN_OFF_COMPANY:
        case STANDARDS_AND_POLICY:
        case SUPERVISED_STUDENT_PUBLICATION:
        case TECHNICAL_STANDARD:
        case TEST:
        case TRADEMARK:
        case TRANSLATION:
        case UNDEFINED:
        default:
            // TODO: do we want a default type? Datacite defaults to no type.
            // builder.type(CSLType.ARTICLE);
            break;
        }
        return builder.build();
    }
    
    /**
     * Extract a credit name from the profile
     * @param orcid
     * @return
     */
    private String getCreditName(String orcid){
        ProfileEntity entity = profileEntityManager.findByOrcid(orcid);
        String creditName = null;
        RecordNameEntity recordNameEntity = entity.getRecordNameEntity();
        if(recordNameEntity != null) {
            creditName = recordNameEntity.getCreditName();
            if (StringUtils.isBlank(creditName)) {
                creditName = recordNameEntity.getGivenNames();
                String familyName = recordNameEntity.getFamilyName();
                if (StringUtils.isNotBlank(familyName)) {
                    creditName += " " + familyName;
                }
            }
        }
        return creditName;
    }
    
    /**
     * Merges in the DOI from a work into a CSLItemdata (if found and not
     * already present)
     * 
     * @param work
     * @param item
     */
    private String extractID(Work work, WorkExternalIdentifierType type) {
        if (work.getExternalIdentifiers() != null && work.getExternalIdentifiers().getExternalIdentifier() != null
                && work.getExternalIdentifiers().getExternalIdentifier().size() > 0) {
            for (ExternalID id : work.getExternalIdentifiers().getExternalIdentifier()) {
                if (id.getType().equalsIgnoreCase(type.value())) {
                    return id.getValue();
                }
            }
        }
        return null;
    }

}
