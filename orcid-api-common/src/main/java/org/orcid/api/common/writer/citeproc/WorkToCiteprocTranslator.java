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
package org.orcid.api.common.writer.citeproc;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jbibtex.BibTeXDatabase;
import org.jbibtex.ParseException;
import org.orcid.jaxb.model.common_rc2.Contributor;
import org.orcid.jaxb.model.record_rc2.CitationType;
import org.orcid.jaxb.model.record_rc2.ExternalID;
import org.orcid.jaxb.model.record_rc2.ExternalIDType;
import org.orcid.jaxb.model.record_rc2.Work;
import org.springframework.util.ReflectionUtils;

import com.google.common.base.Joiner;

import de.undercouch.citeproc.bibtex.BibTeXConverter;
import de.undercouch.citeproc.csl.CSLItemData;
import de.undercouch.citeproc.csl.CSLItemDataBuilder;
import de.undercouch.citeproc.csl.CSLName;
import de.undercouch.citeproc.csl.CSLNameBuilder;
import de.undercouch.citeproc.csl.CSLType;

public class WorkToCiteprocTranslator {

    private final Field authorField = ReflectionUtils.findField(CSLItemData.class, "author");
    private final Field literalField = ReflectionUtils.findField(CSLName.class, "literal");
    private final Field doiField = ReflectionUtils.findField(CSLItemData.class, "DOI");
    private final Field urlField = ReflectionUtils.findField(CSLItemData.class, "URL");

    /**
     * Turn a work into Citeproc JSON. Uses bibtex citation if present,
     * otherwise attempts to map ORCID metadata
     * 
     * @param work the work to translate
     * @param creditName name to use as author if no bibtex found 
     * @param abreviate if true, will shorten bibtex derived authorship to 200 characters or 20 authors
     * @return the JSON as a String.
     */
    public CSLItemData toCiteproc(Work work, String creditName, boolean abreviate) {
        if (work.getWorkCitation() != null && work.getWorkCitation().getWorkCitationType() != null
                && work.getWorkCitation().getWorkCitationType().equals(CitationType.BIBTEX)) {
            return translateFromBibtexCitation(work, abreviate);
        }
        return translateFromWorkMetadata(work,creditName);
    }

    /**
     * Extract the bibtex and turn into CSL Adds in DOI and URL from metadata if
     * missing Horrible use of reflection to shorten hyperauthorship. It will
     * strip anything above 20 authors down to the primary author and 'et all'.
     * 
     * @param work
     * @param abreviate
     * @return
     */
    private CSLItemData translateFromBibtexCitation(Work work, boolean abreviate) {
        try {
            BibTeXConverter conv = new BibTeXConverter();
            BibTeXDatabase db = conv.loadDatabase(IOUtils.toInputStream(StringUtils.stripAccents(work.getWorkCitation().getCitation())));
            Map<String, CSLItemData> cids = conv.toItemData(db);
            if (cids.size() == 1) {
                CSLItemData item = cids.values().iterator().next();
                // FOR REASONS UNKNOWN, CITEPROC WILL SOMETIMES generate
                // multiple authors not a literal.
                if (abreviate) {
                    if (item.getAuthor().length > 20) {
                        CSLName[] abrev = Arrays.copyOf(item.getAuthor(), 1);
                        abrev[0] = new CSLNameBuilder().literal(abrev[0].getGiven() + " " + abrev[0].getFamily() + " " + "et all.").build();
                        ReflectionUtils.makeAccessible(authorField);
                        ReflectionUtils.setField(authorField, item, abrev);
                    }
                    for (int i = 0; i < item.getAuthor().length; i++) {
                        if (item.getAuthor()[i].getLiteral() != null && item.getAuthor()[i].getLiteral().length() > 200) {
                            ReflectionUtils.makeAccessible(literalField);
                            ReflectionUtils.setField(literalField, item.getAuthor()[i], StringUtils.abbreviate(item.getAuthor()[i].getLiteral(), 200));
                        }
                    }
                }
                if (item.getDOI() == null) {
                    String doi = extractID(work, ExternalIDType.DOI);
                    if (doi != null) {
                        ReflectionUtils.makeAccessible(doiField);
                        ReflectionUtils.setField(doiField, item, doi);
                    }
                }
                if (item.getURL() == null) {
                    if (extractID(work, ExternalIDType.URI) != null) {
                        ReflectionUtils.makeAccessible(urlField);
                        ReflectionUtils.setField(urlField, item, extractID(work, ExternalIDType.URI));
                    } else if (item.getDOI() != null) {
                        ReflectionUtils.makeAccessible(urlField);
                        ReflectionUtils.setField(urlField, item, item.getDOI());
                    } else if (extractID(work, ExternalIDType.HANDLE) != null) {
                        ReflectionUtils.makeAccessible(urlField);
                        ReflectionUtils.setField(urlField, item, extractID(work, ExternalIDType.HANDLE));
                    }
                }
                return item;
            } else
                throw new ParseException("Invalid Citation count");
        } catch (IOException | ParseException e) {
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
    private CSLItemData translateFromWorkMetadata(Work work, String creditName) {
        CSLItemDataBuilder builder = new CSLItemDataBuilder();
        builder.title((work.getWorkTitle() != null) ? StringUtils.stripAccents(work.getWorkTitle().getTitle().getContent()) : "No Title");
        String doi = extractID(work, ExternalIDType.DOI);
        String url = extractID(work, ExternalIDType.URI);
        if (doi != null) {
            builder.DOI(doi);
        }
        if (url != null) {
            builder.URL(url);
        } else if (doi != null) {
            builder.URL("http://doi.org/" + doi);
        } else {
            url = extractID(work, ExternalIDType.HANDLE);
            if (url != null) {
                builder.URL(url);
            }
        }

        if (work.getJournalTitle() != null) {
            builder.containerTitle(StringUtils.stripAccents(work.getJournalTitle().getContent()));
        }

        List<String> names = new ArrayList<String>();
        // TODO: Pass in credit name
        names.add(creditName);
        if (work.getWorkContributors() != null && work.getWorkContributors().getContributor() != null) {
            for (Contributor c : work.getWorkContributors().getContributor()) {
                names.add(StringUtils.stripAccents(c.getCreditName().getContent()));
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
     * Merges in the DOI from a work into a CSLItemdata (if found and not
     * already present)
     * 
     * @param work
     * @param item
     */
    private String extractID(Work work, ExternalIDType type) {
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