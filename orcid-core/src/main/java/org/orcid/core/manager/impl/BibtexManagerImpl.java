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
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.util.log.Log;
import org.orcid.core.manager.ActivitiesSummaryManager;
import org.orcid.core.manager.BibtexManager;
import org.orcid.core.manager.DOIManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.WorkManager;
import org.orcid.jaxb.model.common_v2.Contributor;
import org.orcid.jaxb.model.record.summary_v2.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_v2.WorkGroup;
import org.orcid.jaxb.model.record.summary_v2.WorkSummary;
import org.orcid.jaxb.model.record_rc1.WorkExternalIdentifierType;
import org.orcid.jaxb.model.record_v2.CitationType;
import org.orcid.jaxb.model.record_v2.ExternalID;
import org.orcid.jaxb.model.record_v2.Work;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.RecordNameEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableBiMap.Builder;
import com.google.common.collect.ImmutableMap;

public class BibtexManagerImpl implements BibtexManager{

    private static final Logger LOGGER = LoggerFactory.getLogger(BibtexManagerImpl.class);
    
    @Resource
    private ActivitiesSummaryManager activitiesManager;
    
    @Resource
    private WorkManager workManager;
    
    @Resource
    private ProfileEntityManager profileEntityManager;
    
    @Resource 
    private DOIManager doiManager;
    
    private static ImmutableMap<Character,String> escapeW3C = null;
    
    public BibtexManagerImpl(){
        if (escapeW3C == null){
            CsvMapper mapper = new CsvMapper();
            mapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);
            try {
                MappingIterator<String[]> it = mapper.reader(String[].class).readValues(getClass().getResourceAsStream("escape_bibtex.txt"));
                ImmutableMap.Builder<Character,String> builder = new ImmutableMap.Builder<Character,String>();
                while (it.hasNext()){
                   String[] row = it.next();
                   if (row.length == 2)
                       builder.put((char)row[1].trim().charAt(0), row[0]);
                 }
                escapeW3C = builder.build();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }        
        }
    }
    
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
                try{
                    String bibtex = doiManager.fetchDOIBibtex(doi);
                    if (bibtex != null)
                        return bibtex;                    
                }catch (Exception e){
                    //something went wrong at crossref/datacite e.g. 10.1890/1540-9295(2006)004[0244:elsdvs]2.0.co;2
                    //ignore and use our metadata
                    LOGGER.warn("cannot resolve DOI to metadata:"+doi);

                }
            }
        }
        
        //otherwise, use whatever we can
        String creditName = getCreditName(orcid);
        return workToBibtex(work,creditName);
    }
    
    public String workToBibtex(Work work, String creditName){
        StringBuffer out = new StringBuffer();
        switch (work.getWorkType()) {
            case JOURNAL_ARTICLE:
                out.append("@article{");
                break;
            case BOOK:
            case BOOK_CHAPTER:
                out.append("@book{");
                break;
            case CONFERENCE_PAPER:
            case CONFERENCE_ABSTRACT:
            case CONFERENCE_POSTER:
                out.append("@conference{");
                break;
            default:
                out.append("@misc{");
                break;
        }
        //id
        out.append(escapeStringForBibtex(creditName).replace(' ', '_')+work.getPutCode());   
        //title
        out.append(",\ntitle={"+escapeStringForBibtex((work.getWorkTitle() != null) ? work.getWorkTitle().getTitle().getContent() : "No Title")+"}");        
        //journal title
        if (work.getJournalTitle() != null) {
            out.append(",\njournal={"+escapeStringForBibtex(work.getJournalTitle().getContent())+"}");
        }
        //name
        List<String> names = new ArrayList<String>();
        names.add(creditName);
        if (work.getWorkContributors() != null && work.getWorkContributors().getContributor() != null) {
            for (Contributor c : work.getWorkContributors().getContributor()) {
                if (c.getCreditName() != null && c.getCreditName().getContent() != null) {
                    names.add(c.getCreditName().getContent());
                }
            }
        }
        out.append(",\nauthor={"+escapeStringForBibtex(Joiner.on(" and ").skipNulls().join(names))+"}");
        //ids
        String doi = extractID(work, WorkExternalIdentifierType.DOI);
        String url = extractID(work, WorkExternalIdentifierType.URI);
        if (doi != null) {
            out.append(",\ndoi={"+escapeStringForBibtex(doi)+"}");
        }
        if (url != null) {
            out.append(",\nurl={"+escapeStringForBibtex(url)+"}");
        } else if (doi != null) {
            out.append(",\nurl={"+escapeStringForBibtex("http://doi.org/" + doi)+"}");
        } else {
            url = extractID(work, WorkExternalIdentifierType.HANDLE);
            if (url != null) {
                out.append(",\nurl={"+escapeStringForBibtex(url)+"}");
            }
        }
        String isbn = extractID(work, WorkExternalIdentifierType.ISBN);
        if (isbn != null)
            out.append(",\nisbn={"+escapeStringForBibtex(isbn)+"}");
        String issn = extractID(work, WorkExternalIdentifierType.ISSN);              
        if (issn !=null)
            out.append(",\nissn={"+escapeStringForBibtex(issn)+"}");
        
        //year
        if (work.getPublicationDate() != null) {
            int year = 0;
            try {
                year = Integer.parseInt(work.getPublicationDate().getYear().getValue());
            } catch (Exception e) {
            }
            if (year > 0) {
                out.append(",\nyear={"+year+"}");
            }

        }
        out.append("\n}");
        return out.toString();
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
    
    //from https://github.com/datacite/content-resolver/issues/2
    //this is the same as datacite and pangaea
    public final String escapeStringForBibtex(String text) {
        StringBuilder sb=new StringBuilder(text.length());
        boolean nl=false;
        for (int codepoint : text.codePoints().toArray()){
            char ch=(char)codepoint;//text.charAt(i);
            if (ch!=13 && ch!=10 && nl) {
                sb.append("\\\\\n");
                nl=false;
            }
            switch (ch) {
                case '\u00E4': sb.append("{\\\"a}"); break;
                case '\u00F6': sb.append("{\\\"o}"); break;
                case '\u00FC': sb.append("{\\\"u}"); break;
                case '\u00EB': sb.append("{\\\"e}"); break;
                case '\u00EF': sb.append("{\\\"i}"); break;

                case 196: sb.append("{\\\"A}"); break;
                case 214: sb.append("{\\\"O}"); break;
                case 220: sb.append("{\\\"U}"); break;
                case 203: sb.append("{\\\"E}"); break;
                case 207: sb.append("{\\\"I}"); break;

                case 225: sb.append("{\\'a}"); break;
                case 243: sb.append("{\\'o}"); break;
                case 250: sb.append("{\\'u}"); break;
                case 233: sb.append("{\\'e}"); break;
                case 237: sb.append("{\\'i}"); break;

                case 224: sb.append("{\\`a}"); break;
                case 242: sb.append("{\\`o}"); break;
                case 249: sb.append("{\\`u}"); break;
                case 232: sb.append("{\\`e}"); break;
                case 236: sb.append("{\\`i}"); break;

                case 226: sb.append("{\\^a}"); break;
                case 244: sb.append("{\\^o}"); break;
                case 251: sb.append("{\\^u}"); break;
                case 234: sb.append("{\\^e}"); break;
                case 238: sb.append("{\\^i}"); break;

                case 194: sb.append("{\\^A}"); break;
                case 212: sb.append("{\\^O}"); break;
                case 219: sb.append("{\\^U}"); break;
                case 202: sb.append("{\\^E}"); break;
                case 206: sb.append("{\\^I}"); break;

                case 227: sb.append("{\\~a}"); break;
                case 241: sb.append("{\\~n}"); break;
                case 245: sb.append("{\\~o}"); break;

                case 195: sb.append("{\\~A}"); break;
                case 209: sb.append("{\\~N}"); break;
                case 213: sb.append("{\\~O}"); break;

                case '\u00DF': sb.append("{\\ss}"); break;
                case '\u00A0': sb.append('~'); break; // &nbsp;
                case '\u00BA': sb.append("{\\textdegree}"); break;
                case '"': sb.append("{\"}"); break;

                case 13:
                case 10:
                    nl=true;
                    break;

                case '\'':
                case '\u00B4':
                case '`':
                    sb.append("{\'}"); break;

                // simple escapes:
                case '\\':
                case '~':
                case '$':
                case '%':
                case '^':
                case '&':
                case '{':
                case '}':
                case '_':
                    sb.append('\\');
                    sb.append(ch);
                    break;
                default:
                    if (ch<0x80)
                        sb.append(ch);
                    else {
                        String rep = escapeW3C.get(ch);
                        if (rep != null)
                            sb.append(rep);
                        else
                            sb.append("?");
                    }
            }
        }
        return sb.toString();
    }
    
    /*
            private final static Builder<String,Character> builder = new ImmutableBiMap.Builder<String,Character>();
            
            .
            put("\\textexclamdown", '¡').
            put("\\textcent", '¢').
            put("\\textsterling", '£').
            put("\\textcurrency", '¤').
            put("\\textyen", '¥').
            put("\\textbrokenbar", '¦').
            put("\\textsection", '§').
            put("\\textasciidieresis", '¨').
            put("\\textcopyright", '©').
            put("\\textordfeminine", 'ª').
            put("\\guillemotleft", '«').
            put("\\lnot", '¬').
            put("\\-", '­').
            put("\\textregistered", '®').
            put("\\textasciimacron", '¯').
            put("\\textdegree", '°').
            put("\\pm", '±').
            put("\\textasciiacute", '´').
            put("\\mathrm{\\mu}", 'µ').
            put("\\textparagraph", '¶').
            put("\\cdot", '·').
            put("\\c{}", '¸').
            put("\\textordmasculine", 'º').
            put("\\guillemotright", '»').
            put("\\textonequarter", '¼').
            put("\\textonehalf", '½').
            put("\\textthreequarters", '¾').
            put("\\textquestiondown", '¿').
            put("\\`{A}", 'À').
            put("\\'{A}", 'Á').
            put("\\^{A}", 'Â').
            put("\\~{A}", 'Ã').
            put("\\\"{A}", 'Ä').
            put("\\AA", 'Å').
            put("\\AE", 'Æ').
            put("\\c{C}", 'Ç').
            put("\\`{E}", 'È').
            put("\\'{E}", 'É').
            put("\\^{E}", 'Ê').
            put("\\\"{E}", 'Ë').
            put("\\`{I}", 'Ì').
            put("\\'{I}", 'Í').
            put("\\^{I}", 'Î').
            put("\\\"{I}", 'Ï').
            put("\\DH", 'Ð').
            put("\\~{N}", 'Ñ').
            put("\\`{O}", 'Ò').
            put("\\'{O}", 'Ó').
            put("\\^{O}", 'Ô').
            put("\\~{O}", 'Õ').
            put("\\\"{O}", 'Ö').
            put("\\texttimes", '×').
            put("\\O", 'Ø').
            put("\\`{U}", 'Ù').
            put("\\'{U}", 'Ú').
            put("\\^{U}", 'Û').
            put("\\\"{U}", 'Ü').
            put("\\'{Y}", 'Ý').
            put("\\TH", 'Þ').
            put("\\ss", 'ß').
            put("\\`{a}", 'à').
            put("\\'{a}", 'á').
            put("\\^{a}", 'â').
            put("\\~{a}", 'ã').
            put("\\\"{a}", 'ä').
            put("\\aa", 'å').
            put("\\ae", 'æ').
            put("\\c{c}", 'ç').
            put("\\`{e}", 'è').
            put("\\'{e}", 'é').
            put("\\^{e}", 'ê').
            put("\\\"{e}", 'ë').
            put("\\`{\\i}", 'ì').
            put("\\'{\\i}", 'í').
            put("\\^{\\i}", 'î').
            put("\\\"{\\i}", 'ï').
            put("\\dh", 'ð').
            put("\\~{n}", 'ñ').
            put("\\`{o}", 'ò').
            put("\\'{o}", 'ó').
            put("\\^{o}", 'ô').
            put("\\~{o}", 'õ').
            put("\\\"{o}", 'ö').
            put("\\div", '÷').
            put("\\o", 'ø').
            put("\\`{u}", 'ù').
            put("\\'{u}", 'ú').
            put("\\^{u}", 'û').
            put("\\\"{u}", 'ü').
            put("\\'{y}", 'ý').
            put("\\th", 'þ').
            put("\\\"{y}", 'ÿ').
            put("\\={A}", 'Ā').
            put("\\={a}", 'ā').
            put("\\u{A}", 'Ă').
            put("\\u{a}", 'ă').
            put("\\k{A}", 'Ą').
            put("\\k{a}", 'ą').
            put("\\'{C}", 'Ć').
            put("\\'{c}", 'ć').
            put("\\^{C}", 'Ĉ').
            put("\\^{c}", 'ĉ').
            put("\\.{C}", 'Ċ').
            put("\\.{c}", 'ċ').
            put("\\v{C}", 'Č').
            put("\\v{c}", 'č').
            put("\\v{D}", 'Ď').
            put("\\v{d}", 'ď').
            put("\\DJ", 'Đ').
            put("\\dj", 'đ').
            put("\\={E}", 'Ē').
            put("\\={e}", 'ē').
            put("\\u{E}", 'Ĕ').
            put("\\u{e}", 'ĕ').
            put("\\.{E}", 'Ė').
            put("\\.{e}", 'ė').
            put("\\k{E}", 'Ę').
            put("\\k{e}", 'ę').
            put("\\v{E}", 'Ě').
            put("\\v{e}", 'ě').
            put("\\^{G}", 'Ĝ').
            put("\\^{g}", 'ĝ').
            put("\\u{G}", 'Ğ').
            put("\\u{g}", 'ğ').
            put("\\.{G}", 'Ġ').
            put("\\.{g}", 'ġ').
            put("\\c{G}", 'Ģ').
            put("\\c{g}", 'ģ').
            put("\\^{H}", 'Ĥ').
            put("\\^{h}", 'ĥ').
            put("\\Elzxh", 'ħ').
            put("\\~{I}", 'Ĩ').
            put("\\~{\\i}", 'ĩ').
            put("\\={I}", 'Ī').
            put("\\={\\i}", 'ī').
            put("\\u{I}", 'Ĭ').
            put("\\u{\\i}", 'ĭ').
            put("\\k{I}", 'Į').
            put("\\k{i}", 'į').
            put("\\.{I}", 'İ').
            put("\\i", 'ı').
            put("\\^{J}", 'Ĵ').
            put("\\^{\\j}", 'ĵ').
            put("\\c{K}", 'Ķ').
            put("\\c{k}", 'ķ').
            put("\\'{L}", 'Ĺ').
            put("\\'{l}", 'ĺ').
            put("\\c{L}", 'Ļ').
            put("\\c{l}", 'ļ').
            put("\\v{L}", 'Ľ').
            put("\\v{l}", 'ľ').
            put("\\L", 'Ł').
            put("\\l", 'ł').
            put("\\'{N}", 'Ń').
            put("\\'{n}", 'ń').
            put("\\c{N}", 'Ņ').
            put("\\c{n}", 'ņ').
            put("\\v{N}", 'Ň').
            put("\\v{n}", 'ň').
            put("\\NG", 'Ŋ').
            put("\\ng", 'ŋ').
            put("\\={O}", 'Ō').
            put("\\={o}", 'ō').
            put("\\u{O}", 'Ŏ').
            put("\\u{o}", 'ŏ').
            put("\\H{O}", 'Ő').
            put("\\H{o}", 'ő').
            put("\\OE", 'Œ').
            put("\\oe", 'œ').
            put("\\'{R}", 'Ŕ').
            put("\\'{r}", 'ŕ').
            put("\\c{R}", 'Ŗ').
            put("\\c{r}", 'ŗ').
            put("\\v{R}", 'Ř').
            put("\\v{r}", 'ř').
            put("\\'{S}", 'Ś').
            put("\\'{s}", 'ś').
            put("\\^{S}", 'Ŝ').
            put("\\^{s}", 'ŝ').
            put("\\c{S}", 'Ş').
            put("\\c{s}", 'ş').
            put("\\v{S}", 'Š').
            put("\\v{s}", 'š').
            put("\\c{T}", 'Ţ').
            put("\\c{t}", 'ţ').
            put("\\v{T}", 'Ť').
            put("\\v{t}", 'ť').
            put("\\~{U}", 'Ũ').
            put("\\~{u}", 'ũ').
            put("\\={U}", 'Ū').
            put("\\={u}", 'ū').
            put("\\u{U}", 'Ŭ').
            put("\\u{u}", 'ŭ').
            put("\\r{U}", 'Ů').
            put("\\r{u}", 'ů').
            put("\\H{U}", 'Ű').
            put("\\H{u}", 'ű').
            put("\\k{U}", 'Ų').
            put("\\k{u}", 'ų').
            put("\\^{W}", 'Ŵ').
            put("\\^{w}", 'ŵ').
            put("\\^{Y}", 'Ŷ').
            put("\\^{y}", 'ŷ').
            put("\\\"{Y}", 'Ÿ').
            put("\\'{Z}", 'Ź').
            put("\\'{z}", 'ź').
            put("\\.{Z}", 'Ż').
            put("\\.{z}", 'ż').
            put("\\v{Z}", 'Ž').
            put("\\v{z}", 'ž').
            put("\\texthvlig", 'ƕ').
            put("\\textnrleg", 'ƞ').
            put("\\eth", 'ƪ').
            put("\\textdoublepipe", 'ǂ').
            put("\\'{g}", 'ǵ').
            put("\\Elztrna", 'ɐ').
            put("\\Elztrnsa", 'ɒ').
            put("\\Elzopeno", 'ɔ').
            put("\\Elzrtld", 'ɖ').
            put("\\Elzschwa", 'ə').
            put("\\varepsilon", 'ɛ').
            put("\\Elzpgamma", 'ɣ').
            put("\\Elzpbgam", 'ɤ').
            put("\\Elztrnh", 'ɥ').
            put("\\Elzbtdl", 'ɬ').
            put("\\Elzrtll", 'ɭ').
            put("\\Elztrnm", 'ɯ').
            put("\\Elztrnmlr", 'ɰ').
            put("\\Elzltlmr", 'ɱ').
            put("\\Elzltln", 'ɲ').
            put("\\Elzrtln", 'ɳ').
            put("\\Elzclomeg", 'ɷ').
            put("\\textphi", 'ɸ').
            put("\\Elztrnr", 'ɹ').
            put("\\Elztrnrl", 'ɺ').
            put("\\Elzrttrnr", 'ɻ').
            put("\\Elzrl", 'ɼ').
            put("\\Elzrtlr", 'ɽ').
            put("\\Elzfhr", 'ɾ').
            put("\\Elzrtls", 'ʂ').
            put("\\Elzesh", 'ʃ').
            put("\\Elztrnt", 'ʇ').
            put("\\Elzrtlt", 'ʈ').
            put("\\Elzpupsil", 'ʊ').
            put("\\Elzpscrv", 'ʋ').
            put("\\Elzinvv", 'ʌ').
            put("\\Elzinvw", 'ʍ').
            put("\\Elztrny", 'ʎ').
            put("\\Elzrtlz", 'ʐ').
            put("\\Elzyogh", 'ʒ').
            put("\\Elzglst", 'ʔ').
            put("\\Elzreglst", 'ʕ').
            put("\\Elzinglst", 'ʖ').
            put("\\textturnk", 'ʞ').
            put("\\Elzdyogh", 'ʤ').
            put("\\Elztesh", 'ʧ').
            put("\\textasciicaron", 'ˇ').
            put("\\Elzverts", 'ˈ').
            put("\\Elzverti", 'ˌ').
            put("\\Elzlmrk", 'ː').
            put("\\Elzhlmrk", 'ˑ').
            put("\\Elzsbrhr", '˒').
            put("\\Elzsblhr", '˓').
            put("\\Elzrais", '˔').
            put("\\Elzlow", '˕').
            put("\\textasciibreve", '˘').
            put("\\textperiodcentered", '˙').
            put("\\r{}", '˚').
            put("\\k{}", '˛').
            put("\\texttildelow", '˜').
            put("\\H{}", '˝').
            put("\\tone{55}", '˥').
            put("\\tone{44}", '˦').
            put("\\tone{33}", '˧').
            put("\\tone{22}", '˨').
            put("\\tone{11}", '˩').
            put("\\`", '̀').
            put("\\'", '́').
            put("\\^", '̂').
            put("\\~", '̃').
            put("\\=", '̄').
            put("\\u", '̆').
            put("\\.", '̇').
            put("\\\"", '̈').
            put("\\r", '̊').
            put("\\H", '̋').
            put("\\v", '̌').
            put("\\cyrchar\\C", '̏').
            put("\\Elzpalh", '̡').
            put("\\Elzrh", '̢').
            put("\\c", '̧').
            put("\\k", '̨').
            put("\\Elzsbbrg", '̪').
            put("\\Elzxl", '̵').
            put("\\Elzbar", '̶').
            put("\\'{H}", 'Ή').
            put("\\'{}{I}", 'Ί').
            put("\\'{}O", 'Ό').
            put("\\mathrm{'Y}", 'Ύ').
            put("\\mathrm{'\\Omega}", 'Ώ').
            put("\\acute{\\ddot{\\iota}}", 'ΐ').
            put("\\Alpha", 'Α').
            put("\\Beta", 'Β').
            put("\\Gamma", 'Γ').
            put("\\Delta", 'Δ').
            put("\\Epsilon", 'Ε').
            put("\\Zeta", 'Ζ').
            put("\\Eta", 'Η').
            put("\\Theta", 'Θ').
            put("\\Iota", 'Ι').
            put("\\Kappa", 'Κ').
            put("\\Lambda", 'Λ').
            put("\\Xi", 'Ξ').
            put("\\Pi", 'Π').
            put("\\Rho", 'Ρ').
            put("\\Sigma", 'Σ').
            put("\\Tau", 'Τ').
            put("\\Upsilon", 'Υ').
            put("\\Phi", 'Φ').
            put("\\Chi", 'Χ').
            put("\\Psi", 'Ψ').
            put("\\Omega", 'Ω').
            put("\\mathrm{\\ddot{I}}", 'Ϊ').
            put("\\mathrm{\\ddot{Y}}", 'Ϋ').
            put("\\'{$\\alpha$}", 'ά').
            put("\\acute{\\epsilon}", 'έ').
            put("\\acute{\\eta}", 'ή').
            put("\\acute{\\iota}", 'ί').
            put("\\acute{\\ddot{\\upsilon}}", 'ΰ').
            put("\\alpha", 'α').
            put("\\beta", 'β').
            put("\\gamma", 'γ').
            put("\\delta", 'δ').
            put("\\epsilon", 'ε').
            put("\\zeta", 'ζ').
            put("\\eta", 'η').
            put("\\texttheta", 'θ').
            put("\\iota", 'ι').
            put("\\kappa", 'κ').
            put("\\lambda", 'λ').
            put("\\mu", 'μ').
            put("\\nu", 'ν').
            put("\\xi", 'ξ').
            put("\\pi", 'π').
            put("\\rho", 'ρ').
            put("\\varsigma", 'ς').
            put("\\sigma", 'σ').
            put("\\tau", 'τ').
            put("\\upsilon", 'υ').
            put("\\varphi", 'φ').
            put("\\chi", 'χ').
            put("\\psi", 'ψ').
            put("\\omega", 'ω').
            put("\\ddot{\\iota}", 'ϊ').
            put("\\ddot{\\upsilon}", 'ϋ').
            put("\\acute{\\upsilon}", 'ύ').
            put("\\acute{\\omega}", 'ώ').
            put("\\Pisymbol{ppi022}{87}", 'ϐ').
            put("\\textvartheta", 'ϑ').
            put("\\phi", 'ϕ').
            put("\\varpi", 'ϖ').
            put("\\Stigma", 'Ϛ').
            put("\\Digamma", 'Ϝ').
            put("\\digamma", 'ϝ').
            put("\\Koppa", 'Ϟ').
            put("\\Sampi", 'Ϡ').
            put("\\varkappa", 'ϰ').
            put("\\varrho", 'ϱ').
            put("\\textTheta", 'ϴ').
            put("\\backepsilon", '϶').
            put("\\cyrchar\\CYRYO", 'Ё').
            put("\\cyrchar\\CYRDJE", 'Ђ').
            put("\\cyrchar{\\'\\CYRG}", 'Ѓ').
            put("\\cyrchar\\CYRIE", 'Є').
            put("\\cyrchar\\CYRDZE", 'Ѕ').
            put("\\cyrchar\\CYRII", 'І').
            put("\\cyrchar\\CYRYI", 'Ї').
            put("\\cyrchar\\CYRJE", 'Ј').
            put("\\cyrchar\\CYRLJE", 'Љ').
            put("\\cyrchar\\CYRNJE", 'Њ').
            put("\\cyrchar\\CYRTSHE", 'Ћ').
            put("\\cyrchar{\\'\\CYRK}", 'Ќ').
            put("\\cyrchar\\CYRUSHRT", 'Ў').
            put("\\cyrchar\\CYRDZHE", 'Џ').
            put("\\cyrchar\\CYRA", 'А').
            put("\\cyrchar\\CYRB", 'Б').
            put("\\cyrchar\\CYRV", 'В').
            put("\\cyrchar\\CYRG", 'Г').
            put("\\cyrchar\\CYRD", 'Д').
            put("\\cyrchar\\CYRE", 'Е').
            put("\\cyrchar\\CYRZH", 'Ж').
            put("\\cyrchar\\CYRZ", 'З').
            put("\\cyrchar\\CYRI", 'И').
            put("\\cyrchar\\CYRISHRT", 'Й').
            put("\\cyrchar\\CYRK", 'К').
            put("\\cyrchar\\CYRL", 'Л').
            put("\\cyrchar\\CYRM", 'М').
            put("\\cyrchar\\CYRN", 'Н').
            put("\\cyrchar\\CYRO", 'О').
            put("\\cyrchar\\CYRP", 'П').
            put("\\cyrchar\\CYRR", 'Р').
            put("\\cyrchar\\CYRS", 'С').
            put("\\cyrchar\\CYRT", 'Т').
            put("\\cyrchar\\CYRU", 'У').
            put("\\cyrchar\\CYRF", 'Ф').
            put("\\cyrchar\\CYRH", 'Х').
            put("\\cyrchar\\CYRC", 'Ц').
            put("\\cyrchar\\CYRCH", 'Ч').
            put("\\cyrchar\\CYRSH", 'Ш').
            put("\\cyrchar\\CYRSHCH", 'Щ').
            put("\\cyrchar\\CYRHRDSN", 'Ъ').
            put("\\cyrchar\\CYRERY", 'Ы').
            put("\\cyrchar\\CYRSFTSN", 'Ь').
            put("\\cyrchar\\CYREREV", 'Э').
            put("\\cyrchar\\CYRYU", 'Ю').
            put("\\cyrchar\\CYRYA", 'Я').
            put("\\cyrchar\\cyra", 'а').
            put("\\cyrchar\\cyrb", 'б').
            put("\\cyrchar\\cyrv", 'в').
            put("\\cyrchar\\cyrg", 'г').
            put("\\cyrchar\\cyrd", 'д').
            put("\\cyrchar\\cyre", 'е').
            put("\\cyrchar\\cyrzh", 'ж').
            put("\\cyrchar\\cyrz", 'з').
            put("\\cyrchar\\cyri", 'и').
            put("\\cyrchar\\cyrishrt", 'й').
            put("\\cyrchar\\cyrk", 'к').
            put("\\cyrchar\\cyrl", 'л').
            put("\\cyrchar\\cyrm", 'м').
            put("\\cyrchar\\cyrn", 'н').
            put("\\cyrchar\\cyro", 'о').
            put("\\cyrchar\\cyrp", 'п').
            put("\\cyrchar\\cyrr", 'р').
            put("\\cyrchar\\cyrs", 'с').
            put("\\cyrchar\\cyrt", 'т').
            put("\\cyrchar\\cyru", 'у').
            put("\\cyrchar\\cyrf", 'ф').
            put("\\cyrchar\\cyrh", 'х').
            put("\\cyrchar\\cyrc", 'ц').
            put("\\cyrchar\\cyrch", 'ч').
            put("\\cyrchar\\cyrsh", 'ш').
            put("\\cyrchar\\cyrshch", 'щ').
            put("\\cyrchar\\cyrhrdsn", 'ъ').
            put("\\cyrchar\\cyrery", 'ы').
            put("\\cyrchar\\cyrsftsn", 'ь').
            put("\\cyrchar\\cyrerev", 'э').
            put("\\cyrchar\\cyryu", 'ю').
            put("\\cyrchar\\cyrya", 'я').
            put("\\cyrchar\\cyryo", 'ё').
            put("\\cyrchar\\cyrdje", 'ђ').
            put("\\cyrchar{\\'\\cyrg}", 'ѓ').
            put("\\cyrchar\\cyrie", 'є').
            put("\\cyrchar\\cyrdze", 'ѕ').
            put("\\cyrchar\\cyrii", 'і').
            put("\\cyrchar\\cyryi", 'ї').
            put("\\cyrchar\\cyrje", 'ј').
            put("\\cyrchar\\cyrlje", 'љ').
            put("\\cyrchar\\cyrnje", 'њ').
            put("\\cyrchar\\cyrtshe", 'ћ').
            put("\\cyrchar{\\'\\cyrk}", 'ќ').
            put("\\cyrchar\\cyrushrt", 'ў').
            put("\\cyrchar\\cyrdzhe", 'џ').
            put("\\cyrchar\\CYROMEGA", 'Ѡ').
            put("\\cyrchar\\cyromega", 'ѡ').
            put("\\cyrchar\\CYRYAT", 'Ѣ').
            put("\\cyrchar\\CYRIOTE", 'Ѥ').
            put("\\cyrchar\\cyriote", 'ѥ').
            put("\\cyrchar\\CYRLYUS", 'Ѧ').
            put("\\cyrchar\\cyrlyus", 'ѧ').
            put("\\cyrchar\\CYRIOTLYUS", 'Ѩ').
            put("\\cyrchar\\cyriotlyus", 'ѩ').
            put("\\cyrchar\\CYRBYUS", 'Ѫ').
            put("\\cyrchar\\CYRIOTBYUS", 'Ѭ').
            put("\\cyrchar\\cyriotbyus", 'ѭ').
            put("\\cyrchar\\CYRKSI", 'Ѯ').
            put("\\cyrchar\\cyrksi", 'ѯ').
            put("\\cyrchar\\CYRPSI", 'Ѱ').
            put("\\cyrchar\\cyrpsi", 'ѱ').
            put("\\cyrchar\\CYRFITA", 'Ѳ').
            put("\\cyrchar\\CYRIZH", 'Ѵ').
            put("\\cyrchar\\CYRUK", 'Ѹ').
            put("\\cyrchar\\cyruk", 'ѹ').
            put("\\cyrchar\\CYROMEGARND", 'Ѻ').
            put("\\cyrchar\\cyromegarnd", 'ѻ').
            put("\\cyrchar\\CYROMEGATITLO", 'Ѽ').
            put("\\cyrchar\\cyromegatitlo", 'ѽ').
            put("\\cyrchar\\CYROT", 'Ѿ').
            put("\\cyrchar\\cyrot", 'ѿ').
            put("\\cyrchar\\CYRKOPPA", 'Ҁ').
            put("\\cyrchar\\cyrkoppa", 'ҁ').
            put("\\cyrchar\\cyrthousands", '҂').
            put("\\cyrchar\\cyrhundredthousands", '҈').
            put("\\cyrchar\\cyrmillions", '҉').
            put("\\cyrchar\\CYRSEMISFTSN", 'Ҍ').
            put("\\cyrchar\\cyrsemisftsn", 'ҍ').
            put("\\cyrchar\\CYRRTICK", 'Ҏ').
            put("\\cyrchar\\cyrrtick", 'ҏ').
            put("\\cyrchar\\CYRGUP", 'Ґ').
            put("\\cyrchar\\cyrgup", 'ґ').
            put("\\cyrchar\\CYRGHCRS", 'Ғ').
            put("\\cyrchar\\cyrghcrs", 'ғ').
            put("\\cyrchar\\CYRGHK", 'Ҕ').
            put("\\cyrchar\\cyrghk", 'ҕ').
            put("\\cyrchar\\CYRZHDSC", 'Җ').
            put("\\cyrchar\\cyrzhdsc", 'җ').
            put("\\cyrchar\\CYRZDSC", 'Ҙ').
            put("\\cyrchar\\cyrzdsc", 'ҙ').
            put("\\cyrchar\\CYRKDSC", 'Қ').
            put("\\cyrchar\\cyrkdsc", 'қ').
            put("\\cyrchar\\CYRKVCRS", 'Ҝ').
            put("\\cyrchar\\cyrkvcrs", 'ҝ').
            put("\\cyrchar\\CYRKHCRS", 'Ҟ').
            put("\\cyrchar\\cyrkhcrs", 'ҟ').
            put("\\cyrchar\\CYRKBEAK", 'Ҡ').
            put("\\cyrchar\\cyrkbeak", 'ҡ').
            put("\\cyrchar\\CYRNDSC", 'Ң').
            put("\\cyrchar\\cyrndsc", 'ң').
            put("\\cyrchar\\CYRNG", 'Ҥ').
            put("\\cyrchar\\cyrng", 'ҥ').
            put("\\cyrchar\\CYRPHK", 'Ҧ').
            put("\\cyrchar\\cyrphk", 'ҧ').
            put("\\cyrchar\\CYRABHHA", 'Ҩ').
            put("\\cyrchar\\cyrabhha", 'ҩ').
            put("\\cyrchar\\CYRSDSC", 'Ҫ').
            put("\\cyrchar\\cyrsdsc", 'ҫ').
            put("\\cyrchar\\CYRTDSC", 'Ҭ').
            put("\\cyrchar\\cyrtdsc", 'ҭ').
            put("\\cyrchar\\CYRY", 'Ү').
            put("\\cyrchar\\cyry", 'ү').
            put("\\cyrchar\\CYRYHCRS", 'Ұ').
            put("\\cyrchar\\cyryhcrs", 'ұ').
            put("\\cyrchar\\CYRHDSC", 'Ҳ').
            put("\\cyrchar\\cyrhdsc", 'ҳ').
            put("\\cyrchar\\CYRTETSE", 'Ҵ').
            put("\\cyrchar\\cyrtetse", 'ҵ').
            put("\\cyrchar\\CYRCHRDSC", 'Ҷ').
            put("\\cyrchar\\cyrchrdsc", 'ҷ').
            put("\\cyrchar\\CYRCHVCRS", 'Ҹ').
            put("\\cyrchar\\cyrchvcrs", 'ҹ').
            put("\\cyrchar\\CYRSHHA", 'Һ').
            put("\\cyrchar\\cyrshha", 'һ').
            put("\\cyrchar\\CYRABHCH", 'Ҽ').
            put("\\cyrchar\\cyrabhch", 'ҽ').
            put("\\cyrchar\\CYRABHCHDSC", 'Ҿ').
            put("\\cyrchar\\cyrabhchdsc", 'ҿ').
            put("\\cyrchar\\CYRpalochka", 'Ӏ').
            put("\\cyrchar\\CYRKHK", 'Ӄ').
            put("\\cyrchar\\cyrkhk", 'ӄ').
            put("\\cyrchar\\CYRNHK", 'Ӈ').
            put("\\cyrchar\\cyrnhk", 'ӈ').
            put("\\cyrchar\\CYRCHLDSC", 'Ӌ').
            put("\\cyrchar\\cyrchldsc", 'ӌ').
            put("\\cyrchar\\CYRAE", 'Ӕ').
            put("\\cyrchar\\cyrae", 'ӕ').
            put("\\cyrchar\\CYRSCHWA", 'Ә').
            put("\\cyrchar\\cyrschwa", 'ә').
            put("\\cyrchar\\CYRABHDZE", 'Ӡ').
            put("\\cyrchar\\cyrabhdze", 'ӡ').
            put("\\cyrchar\\CYROTLD", 'Ө').
            put("\\cyrchar\\cyrotld", 'ө').
            put("\\hspace{0.6em}", ' ').
            put("\\hspace{1em}", ' ').
            put("\\hspace{0.33em}", ' ').
            put("\\hspace{0.25em}", ' ').
            put("\\hspace{0.166em}", ' ').
            put("\\hphantom{0}", ' ').
            put("\\hphantom{,}", ' ').
            put("\\hspace{0.167em}", ' ').
            put("\\mkern1mu", ' ').
            put("\\textendash", '–').
            put("\\textemdash", '—').
            put("\\rule{1em}{1pt}", '―').
            put("\\Vert", '‖').
            put("\\Elzreapos", '‛').
            put("\\textquotedblleft", '“').
            put("\\textquotedblright", '”').
            put("\\textdagger", '†').
            put("\\textdaggerdbl", '‡').
            put("\\textbullet", '•').
            put("\\ldots", '…').
            put("\\textperthousand", '‰').
            put("\\textpertenthousand", '‱').
            put("\\backprime", '‵').
            put("\\guilsinglleft", '‹').
            put("\\guilsinglright", '›').
            put("\\mkern4mu", ' ').
            put("\\nolinebreak", '⁠').
            put("\\ensuremath{\\Elzpes}", '₧').
            put("\\mbox{\\texteuro}", '€').
            put("\\dddot", '⃛').
            put("\\ddddot", '⃜').
            put("\\mathbb{C}", 'ℂ').
            put("\\mathscr{g}", 'ℊ').
            put("\\mathscr{H}", 'ℋ').
            put("\\mathfrak{H}", 'ℌ').
            put("\\mathbb{H}", 'ℍ').
            put("\\hslash", 'ℏ').
            put("\\mathscr{I}", 'ℐ').
            put("\\mathfrak{I}", 'ℑ').
            put("\\mathscr{L}", 'ℒ').
            put("\\mathscr{l}", 'ℓ').
            put("\\mathbb{N}", 'ℕ').
            put("\\cyrchar\\textnumero", '№').
            put("\\wp", '℘').
            put("\\mathbb{P}", 'ℙ').
            put("\\mathbb{Q}", 'ℚ').
            put("\\mathscr{R}", 'ℛ').
            put("\\mathfrak{R}", 'ℜ').
            put("\\mathbb{R}", 'ℝ').
            put("\\Elzxrat", '℞').
            put("\\texttrademark", '™').
            put("\\mathbb{Z}", 'ℤ').
            put("\\mho", '℧').
            put("\\mathfrak{Z}", 'ℨ').
            put("\\ElsevierGlyph{2129}", '℩').
            put("\\mathscr{B}", 'ℬ').
            put("\\mathfrak{C}", 'ℭ').
            put("\\mathscr{e}", 'ℯ').
            put("\\mathscr{E}", 'ℰ').
            put("\\mathscr{F}", 'ℱ').
            put("\\mathscr{M}", 'ℳ').
            put("\\mathscr{o}", 'ℴ').
            put("\\aleph", 'ℵ').
            put("\\beth", 'ℶ').
            put("\\gimel", 'ℷ').
            put("\\daleth", 'ℸ').
            put("\\textfrac{1}{3}", '⅓').
            put("\\textfrac{2}{3}", '⅔').
            put("\\textfrac{1}{5}", '⅕').
            put("\\textfrac{2}{5}", '⅖').
            put("\\textfrac{3}{5}", '⅗').
            put("\\textfrac{4}{5}", '⅘').
            put("\\textfrac{1}{6}", '⅙').
            put("\\textfrac{5}{6}", '⅚').
            put("\\textfrac{1}{8}", '⅛').
            put("\\textfrac{3}{8}", '⅜').
            put("\\textfrac{5}{8}", '⅝').
            put("\\textfrac{7}{8}", '⅞').
            put("\\leftarrow", '←').
            put("\\uparrow", '↑').
            put("\\rightarrow", '→').
            put("\\downarrow", '↓').
            put("\\leftrightarrow", '↔').
            put("\\updownarrow", '↕').
            put("\\nwarrow", '↖').
            put("\\nearrow", '↗').
            put("\\searrow", '↘').
            put("\\swarrow", '↙').
            put("\\nleftarrow", '↚').
            put("\\nrightarrow", '↛').
            put("\\arrowwaveleft", '↜').
            put("\\arrowwaveright", '↝').
            put("\\twoheadleftarrow", '↞').
            put("\\twoheadrightarrow", '↠').
            put("\\leftarrowtail", '↢').
            put("\\rightarrowtail", '↣').
            put("\\mapsto", '↦').
            put("\\hookleftarrow", '↩').
            put("\\hookrightarrow", '↪').
            put("\\looparrowleft", '↫').
            put("\\looparrowright", '↬').
            put("\\leftrightsquigarrow", '↭').
            put("\\nleftrightarrow", '↮').
            put("\\Lsh", '↰').
            put("\\Rsh", '↱').
            put("\\ElsevierGlyph{21B3}", '↳').
            put("\\curvearrowleft", '↶').
            put("\\curvearrowright", '↷').
            put("\\circlearrowleft", '↺').
            put("\\circlearrowright", '↻').
            put("\\leftharpoonup", '↼').
            put("\\leftharpoondown", '↽').
            put("\\upharpoonright", '↾').
            put("\\upharpoonleft", '↿').
            put("\\rightharpoonup", '⇀').
            put("\\rightharpoondown", '⇁').
            put("\\downharpoonright", '⇂').
            put("\\downharpoonleft", '⇃').
            put("\\rightleftarrows", '⇄').
            put("\\dblarrowupdown", '⇅').
            put("\\leftrightarrows", '⇆').
            put("\\leftleftarrows", '⇇').
            put("\\upuparrows", '⇈').
            put("\\rightrightarrows", '⇉').
            put("\\downdownarrows", '⇊').
            put("\\leftrightharpoons", '⇋').
            put("\\rightleftharpoons", '⇌').
            put("\\nLeftarrow", '⇍').
            put("\\nLeftrightarrow", '⇎').
            put("\\nRightarrow", '⇏').
            put("\\Leftarrow", '⇐').
            put("\\Uparrow", '⇑').
            put("\\Rightarrow", '⇒').
            put("\\Downarrow", '⇓').
            put("\\Leftrightarrow", '⇔').
            put("\\Updownarrow", '⇕').
            put("\\Lleftarrow", '⇚').
            put("\\Rrightarrow", '⇛').
            put("\\rightsquigarrow", '⇝').
            put("\\DownArrowUpArrow", '⇵').
            put("\\forall", '∀').
            put("\\complement", '∁').
            put("\\partial", '∂').
            put("\\exists", '∃').
            put("\\nexists", '∄').
            put("\\varnothing", '∅').
            put("\\nabla", '∇').
            put("\\in", '∈').
            put("\\not\\in", '∉').
            put("\\ni", '∋').
            put("\\not\\ni", '∌').
            put("\\prod", '∏').
            put("\\coprod", '∐').
            put("\\sum", '∑').
            put("\\mp", '∓').
            put("\\dotplus", '∔').
            put("\\setminus", '∖').
            put("\\circ", '∘').
            put("\\bullet", '∙').
            put("\\surd", '√').
            put("\\propto", '∝').
            put("\\infty", '∞').
            put("\\rightangle", '∟').
            put("\\angle", '∠').
            put("\\measuredangle", '∡').
            put("\\sphericalangle", '∢').
            put("\\mid", '∣').
            put("\\nmid", '∤').
            put("\\parallel", '∥').
            put("\\nparallel", '∦').
            put("\\wedge", '∧').
            put("\\vee", '∨').
            put("\\cap", '∩').
            put("\\cup", '∪').
            put("\\int", '∫').
            put("\\int\\!\\int", '∬').
            put("\\int\\!\\int\\!\\int", '∭').
            put("\\oint", '∮').
            put("\\surfintegral", '∯').
            put("\\volintegral", '∰').
            put("\\clwintegral", '∱').
            put("\\ElsevierGlyph{2232}", '∲').
            put("\\ElsevierGlyph{2233}", '∳').
            put("\\therefore", '∴').
            put("\\because", '∵').
            put("\\Colon", '∷').
            put("\\ElsevierGlyph{2238}", '∸').
            put("\\mathbin{{:}\\!\\!{-}\\!\\!{:}}", '∺').
            put("\\homothetic", '∻').
            put("\\sim", '∼').
            put("\\backsim", '∽').
            put("\\lazysinv", '∾').
            put("\\wr", '≀').
            put("\\not\\sim", '≁').
            put("\\ElsevierGlyph{2242}", '≂').
            put("\\simeq", '≃').
            put("\\not\\simeq", '≄').
            put("\\cong", '≅').
            put("\\approxnotequal", '≆').
            put("\\not\\cong", '≇').
            put("\\approx", '≈').
            put("\\not\\approx", '≉').
            put("\\approxeq", '≊').
            put("\\tildetrpl", '≋').
            put("\\allequal", '≌').
            put("\\asymp", '≍').
            put("\\Bumpeq", '≎').
            put("\\bumpeq", '≏').
            put("\\doteq", '≐').
            put("\\doteqdot", '≑').
            put("\\fallingdotseq", '≒').
            put("\\risingdotseq", '≓').
            put("\\eqcirc", '≖').
            put("\\circeq", '≗').
            put("\\estimates", '≙').
            put("\\ElsevierGlyph{225A}", '≚').
            put("\\starequal", '≛').
            put("\\triangleq", '≜').
            put("\\ElsevierGlyph{225F}", '≟').
            put("\\not =", '≠').
            put("\\equiv", '≡').
            put("\\not\\equiv", '≢').
            put("\\leq", '≤').
            put("\\geq", '≥').
            put("\\leqq", '≦').
            put("\\geqq", '≧').
            put("\\lneqq", '≨').
            put("\\gneqq", '≩').
            put("\\ll", '≪').
            put("\\gg", '≫').
            put("\\between", '≬').
            put("\\not\\kern-0.3em\\times", '≭').
            put("\\not<", '≮').
            put("\\not>", '≯').
            put("\\not\\leq", '≰').
            put("\\not\\geq", '≱').
            put("\\lessequivlnt", '≲').
            put("\\greaterequivlnt", '≳').
            put("\\ElsevierGlyph{2274}", '≴').
            put("\\ElsevierGlyph{2275}", '≵').
            put("\\lessgtr", '≶').
            put("\\gtrless", '≷').
            put("\\notlessgreater", '≸').
            put("\\notgreaterless", '≹').
            put("\\prec", '≺').
            put("\\succ", '≻').
            put("\\preccurlyeq", '≼').
            put("\\succcurlyeq", '≽').
            put("\\precapprox", '≾').
            put("\\succapprox", '≿').
            put("\\not\\prec", '⊀').
            put("\\not\\succ", '⊁').
            put("\\subset", '⊂').
            put("\\supset", '⊃').
            put("\\not\\subset", '⊄').
            put("\\not\\supset", '⊅').
            put("\\subseteq", '⊆').
            put("\\supseteq", '⊇').
            put("\\not\\subseteq", '⊈').
            put("\\not\\supseteq", '⊉').
            put("\\subsetneq", '⊊').
            put("\\supsetneq", '⊋').
            put("\\uplus", '⊎').
            put("\\sqsubset", '⊏').
            put("\\sqsupset", '⊐').
            put("\\sqsubseteq", '⊑').
            put("\\sqsupseteq", '⊒').
            put("\\sqcap", '⊓').
            put("\\sqcup", '⊔').
            put("\\oplus", '⊕').
            put("\\ominus", '⊖').
            put("\\otimes", '⊗').
            put("\\oslash", '⊘').
            put("\\odot", '⊙').
            put("\\circledcirc", '⊚').
            put("\\circledast", '⊛').
            put("\\circleddash", '⊝').
            put("\\boxplus", '⊞').
            put("\\boxminus", '⊟').
            put("\\boxtimes", '⊠').
            put("\\boxdot", '⊡').
            put("\\vdash", '⊢').
            put("\\dashv", '⊣').
            put("\\top", '⊤').
            put("\\perp", '⊥').
            put("\\truestate", '⊧').
            put("\\forcesextra", '⊨').
            put("\\Vdash", '⊩').
            put("\\Vvdash", '⊪').
            put("\\VDash", '⊫').
            put("\\nvdash", '⊬').
            put("\\nvDash", '⊭').
            put("\\nVdash", '⊮').
            put("\\nVDash", '⊯').
            put("\\vartriangleleft", '⊲').
            put("\\vartriangleright", '⊳').
            put("\\trianglelefteq", '⊴').
            put("\\trianglerighteq", '⊵').
            put("\\original", '⊶').
            put("\\image", '⊷').
            put("\\multimap", '⊸').
            put("\\hermitconjmatrix", '⊹').
            put("\\intercal", '⊺').
            put("\\veebar", '⊻').
            put("\\rightanglearc", '⊾').
            put("\\ElsevierGlyph{22C0}", '⋀').
            put("\\ElsevierGlyph{22C1}", '⋁').
            put("\\bigcap", '⋂').
            put("\\bigcup", '⋃').
            put("\\diamond", '⋄').
            put("\\star", '⋆').
            put("\\divideontimes", '⋇').
            put("\\bowtie", '⋈').
            put("\\ltimes", '⋉').
            put("\\rtimes", '⋊').
            put("\\leftthreetimes", '⋋').
            put("\\rightthreetimes", '⋌').
            put("\\backsimeq", '⋍').
            put("\\curlyvee", '⋎').
            put("\\curlywedge", '⋏').
            put("\\Subset", '⋐').
            put("\\Supset", '⋑').
            put("\\Cap", '⋒').
            put("\\Cup", '⋓').
            put("\\pitchfork", '⋔').
            put("\\lessdot", '⋖').
            put("\\gtrdot", '⋗').
            put("\\verymuchless", '⋘').
            put("\\verymuchgreater", '⋙').
            put("\\lesseqgtr", '⋚').
            put("\\gtreqless", '⋛').
            put("\\curlyeqprec", '⋞').
            put("\\curlyeqsucc", '⋟').
            put("\\not\\sqsubseteq", '⋢').
            put("\\not\\sqsupseteq", '⋣').
            put("\\Elzsqspne", '⋥').
            put("\\lnsim", '⋦').
            put("\\gnsim", '⋧').
            put("\\precedesnotsimilar", '⋨').
            put("\\succnsim", '⋩').
            put("\\ntriangleleft", '⋪').
            put("\\ntriangleright", '⋫').
            put("\\ntrianglelefteq", '⋬').
            put("\\ntrianglerighteq", '⋭').
            put("\\vdots", '⋮').
            put("\\cdots", '⋯').
            put("\\upslopeellipsis", '⋰').
            put("\\downslopeellipsis", '⋱').
            put("\\barwedge", '⌅').
            put("\\varperspcorrespond", '⌆').
            put("\\lceil", '⌈').
            put("\\rceil", '⌉').
            put("\\lfloor", '⌊').
            put("\\rfloor", '⌋').
            put("\\recorder", '⌕').
            put("\\mathchar\"2208", '⌖').
            put("\\ulcorner", '⌜').
            put("\\urcorner", '⌝').
            put("\\llcorner", '⌞').
            put("\\lrcorner", '⌟').
            put("\\frown", '⌢').
            put("\\smile", '⌣').
            put("\\ElsevierGlyph{E838}", '⌽').
            put("\\Elzdlcorn", '⎣').
            put("\\lmoustache", '⎰').
            put("\\rmoustache", '⎱').
            put("\\textvisiblespace", '␣').
            put("\\ding{172}", '①').
            put("\\ding{173}", '②').
            put("\\ding{174}", '③').
            put("\\ding{175}", '④').
            put("\\ding{176}", '⑤').
            put("\\ding{177}", '⑥').
            put("\\ding{178}", '⑦').
            put("\\ding{179}", '⑧').
            put("\\ding{180}", '⑨').
            put("\\ding{181}", '⑩').
            put("\\circledS", 'Ⓢ').
            put("\\Elzdshfnc", '┆').
            put("\\Elzsqfnw", '┙').
            put("\\diagup", '╱').
            put("\\ding{110}", '■').
            put("\\square", '□').
            put("\\blacksquare", '▪').
            put("\\fbox{~~}", '▭').
            put("\\Elzvrecto", '▯').
            put("\\ElsevierGlyph{E381}", '▱').
            put("\\ding{115}", '▲').
            put("\\bigtriangleup", '△').
            put("\\blacktriangle", '▴').
            put("\\vartriangle", '▵').
            put("\\blacktriangleright", '▸').
            put("\\triangleright", '▹').
            put("\\ding{116}", '▼').
            put("\\bigtriangledown", '▽').
            put("\\blacktriangledown", '▾').
            put("\\triangledown", '▿').
            put("\\blacktriangleleft", '◂').
            put("\\triangleleft", '◃').
            put("\\ding{117}", '◆').
            put("\\lozenge", '◊').
            put("\\bigcirc", '○').
            put("\\ding{108}", '●').
            put("\\Elzcirfl", '◐').
            put("\\Elzcirfr", '◑').
            put("\\Elzcirfb", '◒').
            put("\\ding{119}", '◗').
            put("\\Elzrvbull", '◘').
            put("\\Elzsqfl", '◧').
            put("\\Elzsqfr", '◨').
            put("\\Elzsqfse", '◪').
            put("\\ding{72}", '★').
            put("\\ding{73}", '☆').
            put("\\ding{37}", '☎').
            put("\\ding{42}", '☛').
            put("\\ding{43}", '☞').
            put("\\rightmoon", '☾').
            put("\\mercury", '☿').
            put("\\venus", '♀').
            put("\\male", '♂').
            put("\\jupiter", '♃').
            put("\\saturn", '♄').
            put("\\uranus", '♅').
            put("\\neptune", '♆').
            put("\\pluto", '♇').
            put("\\aries", '♈').
            put("\\taurus", '♉').
            put("\\gemini", '♊').
            put("\\cancer", '♋').
            put("\\leo", '♌').
            put("\\virgo", '♍').
            put("\\libra", '♎').
            put("\\scorpio", '♏').
            put("\\sagittarius", '♐').
            put("\\capricornus", '♑').
            put("\\aquarius", '♒').
            put("\\pisces", '♓').
            put("\\ding{171}", '♠').
            put("\\ding{168}", '♣').
            put("\\ding{170}", '♥').
            put("\\ding{169}", '♦').
            put("\\quarternote", '♩').
            put("\\eighthnote", '♪').
            put("\\flat", '♭').
            put("\\natural", '♮').
            put("\\sharp", '♯').
            put("\\ding{33}", '✁').
            put("\\ding{34}", '✂').
            put("\\ding{35}", '✃').
            put("\\ding{36}", '✄').
            put("\\ding{38}", '✆').
            put("\\ding{39}", '✇').
            put("\\ding{40}", '✈').
            put("\\ding{41}", '✉').
            put("\\ding{44}", '✌').
            put("\\ding{45}", '✍').
            put("\\ding{46}", '✎').
            put("\\ding{47}", '✏').
            put("\\ding{48}", '✐').
            put("\\ding{49}", '✑').
            put("\\ding{50}", '✒').
            put("\\ding{51}", '✓').
            put("\\ding{52}", '✔').
            put("\\ding{53}", '✕').
            put("\\ding{54}", '✖').
            put("\\ding{55}", '✗').
            put("\\ding{56}", '✘').
            put("\\ding{57}", '✙').
            put("\\ding{58}", '✚').
            put("\\ding{59}", '✛').
            put("\\ding{60}", '✜').
            put("\\ding{61}", '✝').
            put("\\ding{62}", '✞').
            put("\\ding{63}", '✟').
            put("\\ding{64}", '✠').
            put("\\ding{65}", '✡').
            put("\\ding{66}", '✢').
            put("\\ding{67}", '✣').
            put("\\ding{68}", '✤').
            put("\\ding{69}", '✥').
            put("\\ding{70}", '✦').
            put("\\ding{71}", '✧').
            put("\\ding{74}", '✪').
            put("\\ding{75}", '✫').
            put("\\ding{76}", '✬').
            put("\\ding{77}", '✭').
            put("\\ding{78}", '✮').
            put("\\ding{79}", '✯').
            put("\\ding{80}", '✰').
            put("\\ding{81}", '✱').
            put("\\ding{82}", '✲').
            put("\\ding{83}", '✳').
            put("\\ding{84}", '✴').
            put("\\ding{85}", '✵').
            put("\\ding{86}", '✶').
            put("\\ding{87}", '✷').
            put("\\ding{88}", '✸').
            put("\\ding{89}", '✹').
            put("\\ding{90}", '✺').
            put("\\ding{91}", '✻').
            put("\\ding{92}", '✼').
            put("\\ding{93}", '✽').
            put("\\ding{94}", '✾').
            put("\\ding{95}", '✿').
            put("\\ding{96}", '❀').
            put("\\ding{97}", '❁').
            put("\\ding{98}", '❂').
            put("\\ding{99}", '❃').
            put("\\ding{100}", '❄').
            put("\\ding{101}", '❅').
            put("\\ding{102}", '❆').
            put("\\ding{103}", '❇').
            put("\\ding{104}", '❈').
            put("\\ding{105}", '❉').
            put("\\ding{106}", '❊').
            put("\\ding{107}", '❋').
            put("\\ding{109}", '❍').
            put("\\ding{111}", '❏').
            put("\\ding{112}", '❐').
            put("\\ding{113}", '❑').
            put("\\ding{114}", '❒').
            put("\\ding{118}", '❖').
            put("\\ding{120}", '❘').
            put("\\ding{121}", '❙').
            put("\\ding{122}", '❚').
            put("\\ding{123}", '❛').
            put("\\ding{124}", '❜').
            put("\\ding{125}", '❝').
            put("\\ding{126}", '❞').
            put("\\ding{161}", '❡').
            put("\\ding{162}", '❢').
            put("\\ding{163}", '❣').
            put("\\ding{164}", '❤').
            put("\\ding{165}", '❥').
            put("\\ding{166}", '❦').
            put("\\ding{167}", '❧').
            put("\\ding{182}", '❶').
            put("\\ding{183}", '❷').
            put("\\ding{184}", '❸').
            put("\\ding{185}", '❹').
            put("\\ding{186}", '❺').
            put("\\ding{187}", '❻').
            put("\\ding{188}", '❼').
            put("\\ding{189}", '❽').
            put("\\ding{190}", '❾').
            put("\\ding{191}", '❿').
            put("\\ding{192}", '➀').
            put("\\ding{193}", '➁').
            put("\\ding{194}", '➂').
            put("\\ding{195}", '➃').
            put("\\ding{196}", '➄').
            put("\\ding{197}", '➅').
            put("\\ding{198}", '➆').
            put("\\ding{199}", '➇').
            put("\\ding{200}", '➈').
            put("\\ding{201}", '➉').
            put("\\ding{202}", '➊').
            put("\\ding{203}", '➋').
            put("\\ding{204}", '➌').
            put("\\ding{205}", '➍').
            put("\\ding{206}", '➎').
            put("\\ding{207}", '➏').
            put("\\ding{208}", '➐').
            put("\\ding{209}", '➑').
            put("\\ding{210}", '➒').
            put("\\ding{211}", '➓').
            put("\\ding{212}", '➔').
            put("\\ding{216}", '➘').
            put("\\ding{217}", '➙').
            put("\\ding{218}", '➚').
            put("\\ding{219}", '➛').
            put("\\ding{220}", '➜').
            put("\\ding{221}", '➝').
            put("\\ding{222}", '➞').
            put("\\ding{223}", '➟').
            put("\\ding{224}", '➠').
            put("\\ding{225}", '➡').
            put("\\ding{226}", '➢').
            put("\\ding{227}", '➣').
            put("\\ding{228}", '➤').
            put("\\ding{229}", '➥').
            put("\\ding{230}", '➦').
            put("\\ding{231}", '➧').
            put("\\ding{232}", '➨').
            put("\\ding{233}", '➩').
            put("\\ding{234}", '➪').
            put("\\ding{235}", '➫').
            put("\\ding{236}", '➬').
            put("\\ding{237}", '➭').
            put("\\ding{238}", '➮').
            put("\\ding{239}", '➯').
            put("\\ding{241}", '➱').
            put("\\ding{242}", '➲').
            put("\\ding{243}", '➳').
            put("\\ding{244}", '➴').
            put("\\ding{245}", '➵').
            put("\\ding{246}", '➶').
            put("\\ding{247}", '➷').
            put("\\ding{248}", '➸').
            put("\\ding{249}", '➹').
            put("\\ding{250}", '➺').
            put("\\ding{251}", '➻').
            put("\\ding{252}", '➼').
            put("\\ding{253}", '➽').
            put("\\ding{254}", '➾').
            put("\\langle", '⟨').
            put("\\rangle", '⟩').
            put("\\longleftarrow", '⟵').
            put("\\longrightarrow", '⟶').
            put("\\longleftrightarrow", '⟷').
            put("\\Longleftarrow", '⟸').
            put("\\Longrightarrow", '⟹').
            put("\\Longleftrightarrow", '⟺').
            put("\\longmapsto", '⟼').
            put("\\sim\\joinrel\\leadsto", '⟿').
            put("\\ElsevierGlyph{E212}", '⤅').
            put("\\UpArrowBar", '⤒').
            put("\\DownArrowBar", '⤓').
            put("\\ElsevierGlyph{E20C}", '⤣').
            put("\\ElsevierGlyph{E20D}", '⤤').
            put("\\ElsevierGlyph{E20B}", '⤥').
            put("\\ElsevierGlyph{E20A}", '⤦').
            put("\\ElsevierGlyph{E211}", '⤧').
            put("\\ElsevierGlyph{E20E}", '⤨').
            put("\\ElsevierGlyph{E20F}", '⤩').
            put("\\ElsevierGlyph{E210}", '⤪').
            put("\\ElsevierGlyph{E21C}", '⤳').
            put("\\ElsevierGlyph{E21A}", '⤶').
            put("\\ElsevierGlyph{E219}", '⤷').
            put("\\Elolarr", '⥀').
            put("\\Elorarr", '⥁').
            put("\\ElzRlarr", '⥂').
            put("\\ElzrLarr", '⥄').
            put("\\Elzrarrx", '⥇').
            put("\\LeftRightVector", '⥎').
            put("\\RightUpDownVector", '⥏').
            put("\\DownLeftRightVector", '⥐').
            put("\\LeftUpDownVector", '⥑').
            put("\\LeftVectorBar", '⥒').
            put("\\RightVectorBar", '⥓').
            put("\\RightUpVectorBar", '⥔').
            put("\\RightDownVectorBar", '⥕').
            put("\\DownLeftVectorBar", '⥖').
            put("\\DownRightVectorBar", '⥗').
            put("\\LeftUpVectorBar", '⥘').
            put("\\LeftDownVectorBar", '⥙').
            put("\\LeftTeeVector", '⥚').
            put("\\RightTeeVector", '⥛').
            put("\\RightUpTeeVector", '⥜').
            put("\\RightDownTeeVector", '⥝').
            put("\\DownLeftTeeVector", '⥞').
            put("\\DownRightTeeVector", '⥟').
            put("\\LeftUpTeeVector", '⥠').
            put("\\LeftDownTeeVector", '⥡').
            put("\\UpEquilibrium", '⥮').
            put("\\ReverseUpEquilibrium", '⥯').
            put("\\RoundImplies", '⥰').
            put("\\ElsevierGlyph{E214}", '⥼').
            put("\\ElsevierGlyph{E215}", '⥽').
            put("\\Elztfnc", '⦀').
            put("\\ElsevierGlyph{3018}", '⦅').
            put("\\Elroang", '⦆').
            put("\\ElsevierGlyph{E291}", '⦔').
            put("\\Elzddfnc", '⦙').
            put("\\Angle", '⦜').
            put("\\Elzlpargt", '⦠').
            put("\\ElsevierGlyph{E260}", '⦵').
            put("\\ElsevierGlyph{E61B}", '⦶').
            put("\\ElzLap", '⧊').
            put("\\Elzdefas", '⧋').
            put("\\LeftTriangleBar", '⧏').
            put("\\RightTriangleBar", '⧐').
            put("\\ElsevierGlyph{E372}", '⧜').
            put("\\blacklozenge", '⧫').
            put("\\RuleDelayed", '⧴').
            put("\\Elxuplus", '⨄').
            put("\\ElzThr", '⨅').
            put("\\Elxsqcup", '⨆').
            put("\\ElzInf", '⨇').
            put("\\ElzSup", '⨈').
            put("\\ElzCint", '⨍').
            put("\\clockoint", '⨏').
            put("\\ElsevierGlyph{E395}", '⨐').
            put("\\sqrint", '⨖').
            put("\\ElsevierGlyph{E25A}", '⨥').
            put("\\ElsevierGlyph{E25B}", '⨪').
            put("\\ElsevierGlyph{E25C}", '⨭').
            put("\\ElsevierGlyph{E25D}", '⨮').
            put("\\ElzTimes", '⨯').
            put("\\ElsevierGlyph{E25E}", '⨴').
            put("\\ElsevierGlyph{E259}", '⨼').
            put("\\amalg", '⨿').
            put("\\ElzAnd", '⩓').
            put("\\ElzOr", '⩔').
            put("\\ElsevierGlyph{E36E}", '⩕').
            put("\\ElOr", '⩖').
            put("\\perspcorrespond", '⩞').
            put("\\Elzminhat", '⩟').
            put("\\stackrel{*}{=}", '⩮').
            put("\\Equal", '⩵').
            put("\\leqslant", '⩽').
            put("\\geqslant", '⩾').
            put("\\lessapprox", '⪅').
            put("\\gtrapprox", '⪆').
            put("\\lneq", '⪇').
            put("\\gneq", '⪈').
            put("\\lnapprox", '⪉').
            put("\\gnapprox", '⪊').
            put("\\lesseqqgtr", '⪋').
            put("\\gtreqqless", '⪌').
            put("\\eqslantless", '⪕').
            put("\\eqslantgtr", '⪖').
            put("\\Pisymbol{ppi020}{117}", '⪝').
            put("\\Pisymbol{ppi020}{105}", '⪞').
            put("\\NestedLessLess", '⪡').
            put("\\NestedGreaterGreater", '⪢').
            put("\\preceq", '⪯').
            put("\\succeq", '⪰').
            put("\\precneqq", '⪵').
            put("\\succneqq", '⪶').
            put("\\precnapprox", '⪹').
            put("\\succnapprox", '⪺').
            put("\\subseteqq", '⫅').
            put("\\supseteqq", '⫆').
            put("\\subsetneqq", '⫋').
            put("\\supsetneqq", '⫌').
            put("\\ElsevierGlyph{E30D}", '⫫').
            put("\\Elztdcol", '⫶').
            put("\\ElsevierGlyph{300A}", '《').
            put("\\ElsevierGlyph{300B}", '》').
            put("\\ElsevierGlyph{3019}", '〙').
            put("\\openbracketleft", '〚').
            put("\\openbracketright", '〛');
            
            
    
            final static Map<Character,String> escapeW3CReverse = builder.build().inverse();
            */

}
