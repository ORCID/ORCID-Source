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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableBiMap;

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
                            sb.append("ARGH");
                    }
            }
        }
        return sb.toString();
    }

    final static Map<Character, String> escapeW3C = ImmutableBiMap.<String, Character>builder().
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
            put("\\openbracketright", '〛').
            
            //The following are beyond the normal range of addressable java Characters (off the BMP), so I've excluded them
            
            /*put("\\mathbf{A}", '𝐀').
            put("\\mathbf{B}", '𝐁').
            put("\\mathbf{C}", '𝐂').
            put("\\mathbf{D}", '𝐃').
            put("\\mathbf{E}", '𝐄').
            put("\\mathbf{F}", '𝐅').
            put("\\mathbf{G}", '𝐆').
            put("\\mathbf{H}", '𝐇').
            put("\\mathbf{I}", '𝐈').
            put("\\mathbf{J}", '𝐉').
            put("\\mathbf{K}", '𝐊').
            put("\\mathbf{L}", '𝐋').
            put("\\mathbf{M}", '𝐌').
            put("\\mathbf{N}", '𝐍').
            put("\\mathbf{O}", '𝐎').
            put("\\mathbf{P}", '𝐏').
            put("\\mathbf{Q}", '𝐐').
            put("\\mathbf{R}", '𝐑').
            put("\\mathbf{S}", '𝐒').
            put("\\mathbf{T}", '𝐓').
            put("\\mathbf{U}", '𝐔').
            put("\\mathbf{V}", '𝐕').
            put("\\mathbf{W}", '𝐖').
            put("\\mathbf{X}", '𝐗').
            put("\\mathbf{Y}", '𝐘').
            put("\\mathbf{Z}", '𝐙').
            put("\\mathbf{a}", '𝐚').
            put("\\mathbf{b}", '𝐛').
            put("\\mathbf{c}", '𝐜').
            put("\\mathbf{d}", '𝐝').
            put("\\mathbf{e}", '𝐞').
            put("\\mathbf{f}", '𝐟').
            put("\\mathbf{g}", '𝐠').
            put("\\mathbf{h}", '𝐡').
            put("\\mathbf{i}", '𝐢').
            put("\\mathbf{j}", '𝐣').
            put("\\mathbf{k}", '𝐤').
            put("\\mathbf{l}", '𝐥').
            put("\\mathbf{m}", '𝐦').
            put("\\mathbf{n}", '𝐧').
            put("\\mathbf{o}", '𝐨').
            put("\\mathbf{p}", '𝐩').
            put("\\mathbf{q}", '𝐪').
            put("\\mathbf{r}", '𝐫').
            put("\\mathbf{s}", '𝐬').
            put("\\mathbf{t}", '𝐭').
            put("\\mathbf{u}", '𝐮').
            put("\\mathbf{v}", '𝐯').
            put("\\mathbf{w}", '𝐰').
            put("\\mathbf{x}", '𝐱').
            put("\\mathbf{y}", '𝐲').
            put("\\mathbf{z}", '𝐳').
            put("\\mathmit{A}", '𝐴').
            put("\\mathmit{B}", '𝐵').
            put("\\mathmit{C}", '𝐶').
            put("\\mathmit{D}", '𝐷').
            put("\\mathmit{E}", '𝐸').
            put("\\mathmit{F}", '𝐹').
            put("\\mathmit{G}", '𝐺').
            put("\\mathmit{H}", '𝐻').
            put("\\mathmit{I}", '𝐼').
            put("\\mathmit{J}", '𝐽').
            put("\\mathmit{K}", '𝐾').
            put("\\mathmit{L}", '𝐿').
            put("\\mathmit{M}", '𝑀').
            put("\\mathmit{N}", '𝑁').
            put("\\mathmit{O}", '𝑂').
            put("\\mathmit{P}", '𝑃').
            put("\\mathmit{Q}", '𝑄').
            put("\\mathmit{R}", '𝑅').
            put("\\mathmit{S}", '𝑆').
            put("\\mathmit{T}", '𝑇').
            put("\\mathmit{U}", '𝑈').
            put("\\mathmit{V}", '𝑉').
            put("\\mathmit{W}", '𝑊').
            put("\\mathmit{X}", '𝑋').
            put("\\mathmit{Y}", '𝑌').
            put("\\mathmit{Z}", '𝑍').
            put("\\mathmit{a}", '𝑎').
            put("\\mathmit{b}", '𝑏').
            put("\\mathmit{c}", '𝑐').
            put("\\mathmit{d}", '𝑑').
            put("\\mathmit{e}", '𝑒').
            put("\\mathmit{f}", '𝑓').
            put("\\mathmit{g}", '𝑔').
            put("\\mathmit{i}", '𝑖').
            put("\\mathmit{j}", '𝑗').
            put("\\mathmit{k}", '𝑘').
            put("\\mathmit{l}", '𝑙').
            put("\\mathmit{m}", '𝑚').
            put("\\mathmit{n}", '𝑛').
            put("\\mathmit{o}", '𝑜').
            put("\\mathmit{p}", '𝑝').
            put("\\mathmit{q}", '𝑞').
            put("\\mathmit{r}", '𝑟').
            put("\\mathmit{s}", '𝑠').
            put("\\mathmit{t}", '𝑡').
            put("\\mathmit{u}", '𝑢').
            put("\\mathmit{v}", '𝑣').
            put("\\mathmit{w}", '𝑤').
            put("\\mathmit{x}", '𝑥').
            put("\\mathmit{y}", '𝑦').
            put("\\mathmit{z}", '𝑧').
            put("\\mathbit{A}", '𝑨').
            put("\\mathbit{B}", '𝑩').
            put("\\mathbit{C}", '𝑪').
            put("\\mathbit{D}", '𝑫').
            put("\\mathbit{E}", '𝑬').
            put("\\mathbit{F}", '𝑭').
            put("\\mathbit{G}", '𝑮').
            put("\\mathbit{H}", '𝑯').
            put("\\mathbit{I}", '𝑰').
            put("\\mathbit{J}", '𝑱').
            put("\\mathbit{K}", '𝑲').
            put("\\mathbit{L}", '𝑳').
            put("\\mathbit{M}", '𝑴').
            put("\\mathbit{N}", '𝑵').
            put("\\mathbit{O}", '𝑶').
            put("\\mathbit{P}", '𝑷').
            put("\\mathbit{Q}", '𝑸').
            put("\\mathbit{R}", '𝑹').
            put("\\mathbit{S}", '𝑺').
            put("\\mathbit{T}", '𝑻').
            put("\\mathbit{U}", '𝑼').
            put("\\mathbit{V}", '𝑽').
            put("\\mathbit{W}", '𝑾').
            put("\\mathbit{X}", '𝑿').
            put("\\mathbit{Y}", '𝒀').
            put("\\mathbit{Z}", '𝒁').
            put("\\mathbit{a}", '𝒂').
            put("\\mathbit{b}", '𝒃').
            put("\\mathbit{c}", '𝒄').
            put("\\mathbit{d}", '𝒅').
            put("\\mathbit{e}", '𝒆').
            put("\\mathbit{f}", '𝒇').
            put("\\mathbit{g}", '𝒈').
            put("\\mathbit{h}", '𝒉').
            put("\\mathbit{i}", '𝒊').
            put("\\mathbit{j}", '𝒋').
            put("\\mathbit{k}", '𝒌').
            put("\\mathbit{l}", '𝒍').
            put("\\mathbit{m}", '𝒎').
            put("\\mathbit{n}", '𝒏').
            put("\\mathbit{o}", '𝒐').
            put("\\mathbit{p}", '𝒑').
            put("\\mathbit{q}", '𝒒').
            put("\\mathbit{r}", '𝒓').
            put("\\mathbit{s}", '𝒔').
            put("\\mathbit{t}", '𝒕').
            put("\\mathbit{u}", '𝒖').
            put("\\mathbit{v}", '𝒗').
            put("\\mathbit{w}", '𝒘').
            put("\\mathbit{x}", '𝒙').
            put("\\mathbit{y}", '𝒚').
            put("\\mathbit{z}", '𝒛').
            put("\\mathscr{A}", '𝒜').
            put("\\mathscr{C}", '𝒞').
            put("\\mathscr{D}", '𝒟').
            put("\\mathscr{G}", '𝒢').
            put("\\mathscr{J}", '𝒥').
            put("\\mathscr{K}", '𝒦').
            put("\\mathscr{N}", '𝒩').
            put("\\mathscr{O}", '𝒪').
            put("\\mathscr{P}", '𝒫').
            put("\\mathscr{Q}", '𝒬').
            put("\\mathscr{S}", '𝒮').
            put("\\mathscr{T}", '𝒯').
            put("\\mathscr{U}", '𝒰').
            put("\\mathscr{V}", '𝒱').
            put("\\mathscr{W}", '𝒲').
            put("\\mathscr{X}", '𝒳').
            put("\\mathscr{Y}", '𝒴').
            put("\\mathscr{Z}", '𝒵').
            put("\\mathscr{a}", '𝒶').
            put("\\mathscr{b}", '𝒷').
            put("\\mathscr{c}", '𝒸').
            put("\\mathscr{d}", '𝒹').
            put("\\mathscr{f}", '𝒻').
            put("\\mathscr{h}", '𝒽').
            put("\\mathscr{i}", '𝒾').
            put("\\mathscr{j}", '𝒿').
            put("\\mathscr{k}", '𝓀').
            put("\\mathscr{m}", '𝓂').
            put("\\mathscr{n}", '𝓃').
            put("\\mathscr{p}", '𝓅').
            put("\\mathscr{q}", '𝓆').
            put("\\mathscr{r}", '𝓇').
            put("\\mathscr{s}", '𝓈').
            put("\\mathscr{t}", '𝓉').
            put("\\mathscr{u}", '𝓊').
            put("\\mathscr{v}", '𝓋').
            put("\\mathscr{w}", '𝓌').
            put("\\mathscr{x}", '𝓍').
            put("\\mathscr{y}", '𝓎').
            put("\\mathscr{z}", '𝓏').
            put("\\mathbcal{A}", '𝓐').
            put("\\mathbcal{B}", '𝓑').
            put("\\mathbcal{C}", '𝓒').
            put("\\mathbcal{D}", '𝓓').
            put("\\mathbcal{E}", '𝓔').
            put("\\mathbcal{F}", '𝓕').
            put("\\mathbcal{G}", '𝓖').
            put("\\mathbcal{H}", '𝓗').
            put("\\mathbcal{I}", '𝓘').
            put("\\mathbcal{J}", '𝓙').
            put("\\mathbcal{K}", '𝓚').
            put("\\mathbcal{L}", '𝓛').
            put("\\mathbcal{M}", '𝓜').
            put("\\mathbcal{N}", '𝓝').
            put("\\mathbcal{O}", '𝓞').
            put("\\mathbcal{P}", '𝓟').
            put("\\mathbcal{Q}", '𝓠').
            put("\\mathbcal{R}", '𝓡').
            put("\\mathbcal{S}", '𝓢').
            put("\\mathbcal{T}", '𝓣').
            put("\\mathbcal{U}", '𝓤').
            put("\\mathbcal{V}", '𝓥').
            put("\\mathbcal{W}", '𝓦').
            put("\\mathbcal{X}", '𝓧').
            put("\\mathbcal{Y}", '𝓨').
            put("\\mathbcal{Z}", '𝓩').
            put("\\mathbcal{a}", '𝓪').
            put("\\mathbcal{b}", '𝓫').
            put("\\mathbcal{c}", '𝓬').
            put("\\mathbcal{d}", '𝓭').
            put("\\mathbcal{e}", '𝓮').
            put("\\mathbcal{f}", '𝓯').
            put("\\mathbcal{g}", '𝓰').
            put("\\mathbcal{h}", '𝓱').
            put("\\mathbcal{i}", '𝓲').
            put("\\mathbcal{j}", '𝓳').
            put("\\mathbcal{k}", '𝓴').
            put("\\mathbcal{l}", '𝓵').
            put("\\mathbcal{m}", '𝓶').
            put("\\mathbcal{n}", '𝓷').
            put("\\mathbcal{o}", '𝓸').
            put("\\mathbcal{p}", '𝓹').
            put("\\mathbcal{q}", '𝓺').
            put("\\mathbcal{r}", '𝓻').
            put("\\mathbcal{s}", '𝓼').
            put("\\mathbcal{t}", '𝓽').
            put("\\mathbcal{u}", '𝓾').
            put("\\mathbcal{v}", '𝓿').
            put("\\mathbcal{w}", '𝔀').
            put("\\mathbcal{x}", '𝔁').
            put("\\mathbcal{y}", '𝔂').
            put("\\mathbcal{z}", '𝔃').
            put("\\mathfrak{A}", '𝔄').
            put("\\mathfrak{B}", '𝔅').
            put("\\mathfrak{D}", '𝔇').
            put("\\mathfrak{E}", '𝔈').
            put("\\mathfrak{F}", '𝔉').
            put("\\mathfrak{G}", '𝔊').
            put("\\mathfrak{J}", '𝔍').
            put("\\mathfrak{K}", '𝔎').
            put("\\mathfrak{L}", '𝔏').
            put("\\mathfrak{M}", '𝔐').
            put("\\mathfrak{N}", '𝔑').
            put("\\mathfrak{O}", '𝔒').
            put("\\mathfrak{P}", '𝔓').
            put("\\mathfrak{Q}", '𝔔').
            put("\\mathfrak{S}", '𝔖').
            put("\\mathfrak{T}", '𝔗').
            put("\\mathfrak{U}", '𝔘').
            put("\\mathfrak{V}", '𝔙').
            put("\\mathfrak{W}", '𝔚').
            put("\\mathfrak{X}", '𝔛').
            put("\\mathfrak{Y}", '𝔜').
            put("\\mathfrak{a}", '𝔞').
            put("\\mathfrak{b}", '𝔟').
            put("\\mathfrak{c}", '𝔠').
            put("\\mathfrak{d}", '𝔡').
            put("\\mathfrak{e}", '𝔢').
            put("\\mathfrak{f}", '𝔣').
            put("\\mathfrak{g}", '𝔤').
            put("\\mathfrak{h}", '𝔥').
            put("\\mathfrak{i}", '𝔦').
            put("\\mathfrak{j}", '𝔧').
            put("\\mathfrak{k}", '𝔨').
            put("\\mathfrak{l}", '𝔩').
            put("\\mathfrak{m}", '𝔪').
            put("\\mathfrak{n}", '𝔫').
            put("\\mathfrak{o}", '𝔬').
            put("\\mathfrak{p}", '𝔭').
            put("\\mathfrak{q}", '𝔮').
            put("\\mathfrak{r}", '𝔯').
            put("\\mathfrak{s}", '𝔰').
            put("\\mathfrak{t}", '𝔱').
            put("\\mathfrak{u}", '𝔲').
            put("\\mathfrak{v}", '𝔳').
            put("\\mathfrak{w}", '𝔴').
            put("\\mathfrak{x}", '𝔵').
            put("\\mathfrak{y}", '𝔶').
            put("\\mathfrak{z}", '𝔷').
            put("\\mathbb{A}", '𝔸').
            put("\\mathbb{B}", '𝔹').
            put("\\mathbb{D}", '𝔻').
            put("\\mathbb{E}", '𝔼').
            put("\\mathbb{F}", '𝔽').
            put("\\mathbb{G}", '𝔾').
            put("\\mathbb{I}", '𝕀').
            put("\\mathbb{J}", '𝕁').
            put("\\mathbb{K}", '𝕂').
            put("\\mathbb{L}", '𝕃').
            put("\\mathbb{M}", '𝕄').
            put("\\mathbb{O}", '𝕆').
            put("\\mathbb{S}", '𝕊').
            put("\\mathbb{T}", '𝕋').
            put("\\mathbb{U}", '𝕌').
            put("\\mathbb{V}", '𝕍').
            put("\\mathbb{W}", '𝕎').
            put("\\mathbb{X}", '𝕏').
            put("\\mathbb{Y}", '𝕐').
            put("\\mathbb{a}", '𝕒').
            put("\\mathbb{b}", '𝕓').
            put("\\mathbb{c}", '𝕔').
            put("\\mathbb{d}", '𝕕').
            put("\\mathbb{e}", '𝕖').
            put("\\mathbb{f}", '𝕗').
            put("\\mathbb{g}", '𝕘').
            put("\\mathbb{h}", '𝕙').
            put("\\mathbb{i}", '𝕚').
            put("\\mathbb{j}", '𝕛').
            put("\\mathbb{k}", '𝕜').
            put("\\mathbb{l}", '𝕝').
            put("\\mathbb{m}", '𝕞').
            put("\\mathbb{n}", '𝕟').
            put("\\mathbb{o}", '𝕠').
            put("\\mathbb{p}", '𝕡').
            put("\\mathbb{q}", '𝕢').
            put("\\mathbb{r}", '𝕣').
            put("\\mathbb{s}", '𝕤').
            put("\\mathbb{t}", '𝕥').
            put("\\mathbb{u}", '𝕦').
            put("\\mathbb{v}", '𝕧').
            put("\\mathbb{w}", '𝕨').
            put("\\mathbb{x}", '𝕩').
            put("\\mathbb{y}", '𝕪').
            put("\\mathbb{z}", '𝕫').
            put("\\mathbfrak{A}", '𝕬').
            put("\\mathbfrak{B}", '𝕭').
            put("\\mathbfrak{C}", '𝕮').
            put("\\mathbfrak{D}", '𝕯').
            put("\\mathbfrak{E}", '𝕰').
            put("\\mathbfrak{F}", '𝕱').
            put("\\mathbfrak{G}", '𝕲').
            put("\\mathbfrak{H}", '𝕳').
            put("\\mathbfrak{I}", '𝕴').
            put("\\mathbfrak{J}", '𝕵').
            put("\\mathbfrak{K}", '𝕶').
            put("\\mathbfrak{L}", '𝕷').
            put("\\mathbfrak{M}", '𝕸').
            put("\\mathbfrak{N}", '𝕹').
            put("\\mathbfrak{O}", '𝕺').
            put("\\mathbfrak{P}", '𝕻').
            put("\\mathbfrak{Q}", '𝕼').
            put("\\mathbfrak{R}", '𝕽').
            put("\\mathbfrak{S}", '𝕾').
            put("\\mathbfrak{T}", '𝕿').
            put("\\mathbfrak{U}", '𝖀').
            put("\\mathbfrak{V}", '𝖁').
            put("\\mathbfrak{W}", '𝖂').
            put("\\mathbfrak{X}", '𝖃').
            put("\\mathbfrak{Y}", '𝖄').
            put("\\mathbfrak{Z}", '𝖅').
            put("\\mathbfrak{a}", '𝖆').
            put("\\mathbfrak{b}", '𝖇').
            put("\\mathbfrak{c}", '𝖈').
            put("\\mathbfrak{d}", '𝖉').
            put("\\mathbfrak{e}", '𝖊').
            put("\\mathbfrak{f}", '𝖋').
            put("\\mathbfrak{g}", '𝖌').
            put("\\mathbfrak{h}", '𝖍').
            put("\\mathbfrak{i}", '𝖎').
            put("\\mathbfrak{j}", '𝖏').
            put("\\mathbfrak{k}", '𝖐').
            put("\\mathbfrak{l}", '𝖑').
            put("\\mathbfrak{m}", '𝖒').
            put("\\mathbfrak{n}", '𝖓').
            put("\\mathbfrak{o}", '𝖔').
            put("\\mathbfrak{p}", '𝖕').
            put("\\mathbfrak{q}", '𝖖').
            put("\\mathbfrak{r}", '𝖗').
            put("\\mathbfrak{s}", '𝖘').
            put("\\mathbfrak{t}", '𝖙').
            put("\\mathbfrak{u}", '𝖚').
            put("\\mathbfrak{v}", '𝖛').
            put("\\mathbfrak{w}", '𝖜').
            put("\\mathbfrak{x}", '𝖝').
            put("\\mathbfrak{y}", '𝖞').
            put("\\mathbfrak{z}", '𝖟').
            put("\\mathsf{A}", '𝖠').
            put("\\mathsf{B}", '𝖡').
            put("\\mathsf{C}", '𝖢').
            put("\\mathsf{D}", '𝖣').
            put("\\mathsf{E}", '𝖤').
            put("\\mathsf{F}", '𝖥').
            put("\\mathsf{G}", '𝖦').
            put("\\mathsf{H}", '𝖧').
            put("\\mathsf{I}", '𝖨').
            put("\\mathsf{J}", '𝖩').
            put("\\mathsf{K}", '𝖪').
            put("\\mathsf{L}", '𝖫').
            put("\\mathsf{M}", '𝖬').
            put("\\mathsf{N}", '𝖭').
            put("\\mathsf{O}", '𝖮').
            put("\\mathsf{P}", '𝖯').
            put("\\mathsf{Q}", '𝖰').
            put("\\mathsf{R}", '𝖱').
            put("\\mathsf{S}", '𝖲').
            put("\\mathsf{T}", '𝖳').
            put("\\mathsf{U}", '𝖴').
            put("\\mathsf{V}", '𝖵').
            put("\\mathsf{W}", '𝖶').
            put("\\mathsf{X}", '𝖷').
            put("\\mathsf{Y}", '𝖸').
            put("\\mathsf{Z}", '𝖹').
            put("\\mathsf{a}", '𝖺').
            put("\\mathsf{b}", '𝖻').
            put("\\mathsf{c}", '𝖼').
            put("\\mathsf{d}", '𝖽').
            put("\\mathsf{e}", '𝖾').
            put("\\mathsf{f}", '𝖿').
            put("\\mathsf{g}", '𝗀').
            put("\\mathsf{h}", '𝗁').
            put("\\mathsf{i}", '𝗂').
            put("\\mathsf{j}", '𝗃').
            put("\\mathsf{k}", '𝗄').
            put("\\mathsf{l}", '𝗅').
            put("\\mathsf{m}", '𝗆').
            put("\\mathsf{n}", '𝗇').
            put("\\mathsf{o}", '𝗈').
            put("\\mathsf{p}", '𝗉').
            put("\\mathsf{q}", '𝗊').
            put("\\mathsf{r}", '𝗋').
            put("\\mathsf{s}", '𝗌').
            put("\\mathsf{t}", '𝗍').
            put("\\mathsf{u}", '𝗎').
            put("\\mathsf{v}", '𝗏').
            put("\\mathsf{w}", '𝗐').
            put("\\mathsf{x}", '𝗑').
            put("\\mathsf{y}", '𝗒').
            put("\\mathsf{z}", '𝗓').
            put("\\mathsfbf{A}", '𝗔').
            put("\\mathsfbf{B}", '𝗕').
            put("\\mathsfbf{C}", '𝗖').
            put("\\mathsfbf{D}", '𝗗').
            put("\\mathsfbf{E}", '𝗘').
            put("\\mathsfbf{F}", '𝗙').
            put("\\mathsfbf{G}", '𝗚').
            put("\\mathsfbf{H}", '𝗛').
            put("\\mathsfbf{I}", '𝗜').
            put("\\mathsfbf{J}", '𝗝').
            put("\\mathsfbf{K}", '𝗞').
            put("\\mathsfbf{L}", '𝗟').
            put("\\mathsfbf{M}", '𝗠').
            put("\\mathsfbf{N}", '𝗡').
            put("\\mathsfbf{O}", '𝗢').
            put("\\mathsfbf{P}", '𝗣').
            put("\\mathsfbf{Q}", '𝗤').
            put("\\mathsfbf{R}", '𝗥').
            put("\\mathsfbf{S}", '𝗦').
            put("\\mathsfbf{T}", '𝗧').
            put("\\mathsfbf{U}", '𝗨').
            put("\\mathsfbf{V}", '𝗩').
            put("\\mathsfbf{W}", '𝗪').
            put("\\mathsfbf{X}", '𝗫').
            put("\\mathsfbf{Y}", '𝗬').
            put("\\mathsfbf{Z}", '𝗭').
            put("\\mathsfbf{a}", '𝗮').
            put("\\mathsfbf{b}", '𝗯').
            put("\\mathsfbf{c}", '𝗰').
            put("\\mathsfbf{d}", '𝗱').
            put("\\mathsfbf{e}", '𝗲').
            put("\\mathsfbf{f}", '𝗳').
            put("\\mathsfbf{g}", '𝗴').
            put("\\mathsfbf{h}", '𝗵').
            put("\\mathsfbf{i}", '𝗶').
            put("\\mathsfbf{j}", '𝗷').
            put("\\mathsfbf{k}", '𝗸').
            put("\\mathsfbf{l}", '𝗹').
            put("\\mathsfbf{m}", '𝗺').
            put("\\mathsfbf{n}", '𝗻').
            put("\\mathsfbf{o}", '𝗼').
            put("\\mathsfbf{p}", '𝗽').
            put("\\mathsfbf{q}", '𝗾').
            put("\\mathsfbf{r}", '𝗿').
            put("\\mathsfbf{s}", '𝘀').
            put("\\mathsfbf{t}", '𝘁').
            put("\\mathsfbf{u}", '𝘂').
            put("\\mathsfbf{v}", '𝘃').
            put("\\mathsfbf{w}", '𝘄').
            put("\\mathsfbf{x}", '𝘅').
            put("\\mathsfbf{y}", '𝘆').
            put("\\mathsfbf{z}", '𝘇').
            put("\\mathsfsl{A}", '𝘈').
            put("\\mathsfsl{B}", '𝘉').
            put("\\mathsfsl{C}", '𝘊').
            put("\\mathsfsl{D}", '𝘋').
            put("\\mathsfsl{E}", '𝘌').
            put("\\mathsfsl{F}", '𝘍').
            put("\\mathsfsl{G}", '𝘎').
            put("\\mathsfsl{H}", '𝘏').
            put("\\mathsfsl{I}", '𝘐').
            put("\\mathsfsl{J}", '𝘑').
            put("\\mathsfsl{K}", '𝘒').
            put("\\mathsfsl{L}", '𝘓').
            put("\\mathsfsl{M}", '𝘔').
            put("\\mathsfsl{N}", '𝘕').
            put("\\mathsfsl{O}", '𝘖').
            put("\\mathsfsl{P}", '𝘗').
            put("\\mathsfsl{Q}", '𝘘').
            put("\\mathsfsl{R}", '𝘙').
            put("\\mathsfsl{S}", '𝘚').
            put("\\mathsfsl{T}", '𝘛').
            put("\\mathsfsl{U}", '𝘜').
            put("\\mathsfsl{V}", '𝘝').
            put("\\mathsfsl{W}", '𝘞').
            put("\\mathsfsl{X}", '𝘟').
            put("\\mathsfsl{Y}", '𝘠').
            put("\\mathsfsl{Z}", '𝘡').
            put("\\mathsfsl{a}", '𝘢').
            put("\\mathsfsl{b}", '𝘣').
            put("\\mathsfsl{c}", '𝘤').
            put("\\mathsfsl{d}", '𝘥').
            put("\\mathsfsl{e}", '𝘦').
            put("\\mathsfsl{f}", '𝘧').
            put("\\mathsfsl{g}", '𝘨').
            put("\\mathsfsl{h}", '𝘩').
            put("\\mathsfsl{i}", '𝘪').
            put("\\mathsfsl{j}", '𝘫').
            put("\\mathsfsl{k}", '𝘬').
            put("\\mathsfsl{l}", '𝘭').
            put("\\mathsfsl{m}", '𝘮').
            put("\\mathsfsl{n}", '𝘯').
            put("\\mathsfsl{o}", '𝘰').
            put("\\mathsfsl{p}", '𝘱').
            put("\\mathsfsl{q}", '𝘲').
            put("\\mathsfsl{r}", '𝘳').
            put("\\mathsfsl{s}", '𝘴').
            put("\\mathsfsl{t}", '𝘵').
            put("\\mathsfsl{u}", '𝘶').
            put("\\mathsfsl{v}", '𝘷').
            put("\\mathsfsl{w}", '𝘸').
            put("\\mathsfsl{x}", '𝘹').
            put("\\mathsfsl{y}", '𝘺').
            put("\\mathsfsl{z}", '𝘻').
            put("\\mathsfbfsl{A}", '𝘼').
            put("\\mathsfbfsl{B}", '𝘽').
            put("\\mathsfbfsl{C}", '𝘾').
            put("\\mathsfbfsl{D}", '𝘿').
            put("\\mathsfbfsl{E}", '𝙀').
            put("\\mathsfbfsl{F}", '𝙁').
            put("\\mathsfbfsl{G}", '𝙂').
            put("\\mathsfbfsl{H}", '𝙃').
            put("\\mathsfbfsl{I}", '𝙄').
            put("\\mathsfbfsl{J}", '𝙅').
            put("\\mathsfbfsl{K}", '𝙆').
            put("\\mathsfbfsl{L}", '𝙇').
            put("\\mathsfbfsl{M}", '𝙈').
            put("\\mathsfbfsl{N}", '𝙉').
            put("\\mathsfbfsl{O}", '𝙊').
            put("\\mathsfbfsl{P}", '𝙋').
            put("\\mathsfbfsl{Q}", '𝙌').
            put("\\mathsfbfsl{R}", '𝙍').
            put("\\mathsfbfsl{S}", '𝙎').
            put("\\mathsfbfsl{T}", '𝙏').
            put("\\mathsfbfsl{U}", '𝙐').
            put("\\mathsfbfsl{V}", '𝙑').
            put("\\mathsfbfsl{W}", '𝙒').
            put("\\mathsfbfsl{X}", '𝙓').
            put("\\mathsfbfsl{Y}", '𝙔').
            put("\\mathsfbfsl{Z}", '𝙕').
            put("\\mathsfbfsl{a}", '𝙖').
            put("\\mathsfbfsl{b}", '𝙗').
            put("\\mathsfbfsl{c}", '𝙘').
            put("\\mathsfbfsl{d}", '𝙙').
            put("\\mathsfbfsl{e}", '𝙚').
            put("\\mathsfbfsl{f}", '𝙛').
            put("\\mathsfbfsl{g}", '𝙜').
            put("\\mathsfbfsl{h}", '𝙝').
            put("\\mathsfbfsl{i}", '𝙞').
            put("\\mathsfbfsl{j}", '𝙟').
            put("\\mathsfbfsl{k}", '𝙠').
            put("\\mathsfbfsl{l}", '𝙡').
            put("\\mathsfbfsl{m}", '𝙢').
            put("\\mathsfbfsl{n}", '𝙣').
            put("\\mathsfbfsl{o}", '𝙤').
            put("\\mathsfbfsl{p}", '𝙥').
            put("\\mathsfbfsl{q}", '𝙦').
            put("\\mathsfbfsl{r}", '𝙧').
            put("\\mathsfbfsl{s}", '𝙨').
            put("\\mathsfbfsl{t}", '𝙩').
            put("\\mathsfbfsl{u}", '𝙪').
            put("\\mathsfbfsl{v}", '𝙫').
            put("\\mathsfbfsl{w}", '𝙬').
            put("\\mathsfbfsl{x}", '𝙭').
            put("\\mathsfbfsl{y}", '𝙮').
            put("\\mathsfbfsl{z}", '𝙯').
            put("\\mathtt{A}", '𝙰').
            put("\\mathtt{B}", '𝙱').
            put("\\mathtt{C}", '𝙲').
            put("\\mathtt{D}", '𝙳').
            put("\\mathtt{E}", '𝙴').
            put("\\mathtt{F}", '𝙵').
            put("\\mathtt{G}", '𝙶').
            put("\\mathtt{H}", '𝙷').
            put("\\mathtt{I}", '𝙸').
            put("\\mathtt{J}", '𝙹').
            put("\\mathtt{K}", '𝙺').
            put("\\mathtt{L}", '𝙻').
            put("\\mathtt{M}", '𝙼').
            put("\\mathtt{N}", '𝙽').
            put("\\mathtt{O}", '𝙾').
            put("\\mathtt{P}", '𝙿').
            put("\\mathtt{Q}", '𝚀').
            put("\\mathtt{R}", '𝚁').
            put("\\mathtt{S}", '𝚂').
            put("\\mathtt{T}", '𝚃').
            put("\\mathtt{U}", '𝚄').
            put("\\mathtt{V}", '𝚅').
            put("\\mathtt{W}", '𝚆').
            put("\\mathtt{X}", '𝚇').
            put("\\mathtt{Y}", '𝚈').
            put("\\mathtt{Z}", '𝚉').
            put("\\mathtt{a}", '𝚊').
            put("\\mathtt{b}", '𝚋').
            put("\\mathtt{c}", '𝚌').
            put("\\mathtt{d}", '𝚍').
            put("\\mathtt{e}", '𝚎').
            put("\\mathtt{f}", '𝚏').
            put("\\mathtt{g}", '𝚐').
            put("\\mathtt{h}", '𝚑').
            put("\\mathtt{i}", '𝚒').
            put("\\mathtt{j}", '𝚓').
            put("\\mathtt{k}", '𝚔').
            put("\\mathtt{l}", '𝚕').
            put("\\mathtt{m}", '𝚖').
            put("\\mathtt{n}", '𝚗').
            put("\\mathtt{o}", '𝚘').
            put("\\mathtt{p}", '𝚙').
            put("\\mathtt{q}", '𝚚').
            put("\\mathtt{r}", '𝚛').
            put("\\mathtt{s}", '𝚜').
            put("\\mathtt{t}", '𝚝').
            put("\\mathtt{u}", '𝚞').
            put("\\mathtt{v}", '𝚟').
            put("\\mathtt{w}", '𝚠').
            put("\\mathtt{x}", '𝚡').
            put("\\mathtt{y}", '𝚢').
            put("\\mathtt{z}", '𝚣').
            put("\\mathbf{\\Alpha}", '𝚨').
            put("\\mathbf{\\Beta}", '𝚩').
            put("\\mathbf{\\Gamma}", '𝚪').
            put("\\mathbf{\\Delta}", '𝚫').
            put("\\mathbf{\\Epsilon}", '𝚬').
            put("\\mathbf{\\Zeta}", '𝚭').
            put("\\mathbf{\\Eta}", '𝚮').
            put("\\mathbf{\\Theta}", '𝚯').
            put("\\mathbf{\\Iota}", '𝚰').
            put("\\mathbf{\\Kappa}", '𝚱').
            put("\\mathbf{\\Lambda}", '𝚲').
            put("\\mathbf{\\Xi}", '𝚵').
            put("\\mathbf{\\Pi}", '𝚷').
            put("\\mathbf{\\Rho}", '𝚸').
            put("\\mathbf{\\vartheta}", '𝚹').
            put("\\mathbf{\\Sigma}", '𝚺').
            put("\\mathbf{\\Tau}", '𝚻').
            put("\\mathbf{\\Upsilon}", '𝚼').
            put("\\mathbf{\\Phi}", '𝚽').
            put("\\mathbf{\\Chi}", '𝚾').
            put("\\mathbf{\\Psi}", '𝚿').
            put("\\mathbf{\\Omega}", '𝛀').
            put("\\mathbf{\\nabla}", '𝛁').
            put("\\mathbf{\\alpha}", '𝛂').
            put("\\mathbf{\\beta}", '𝛃').
            put("\\mathbf{\\gamma}", '𝛄').
            put("\\mathbf{\\delta}", '𝛅').
            put("\\mathbf{\\epsilon}", '𝛆').
            put("\\mathbf{\\zeta}", '𝛇').
            put("\\mathbf{\\eta}", '𝛈').
            put("\\mathbf{\\theta}", '𝛉').
            put("\\mathbf{\\iota}", '𝛊').
            put("\\mathbf{\\kappa}", '𝛋').
            put("\\mathbf{\\lambda}", '𝛌').
            put("\\mathbf{\\mu}", '𝛍').
            put("\\mathbf{\\nu}", '𝛎').
            put("\\mathbf{\\xi}", '𝛏').
            put("\\mathbf{\\pi}", '𝛑').
            put("\\mathbf{\\rho}", '𝛒').
            put("\\mathbf{\\varsigma}", '𝛓').
            put("\\mathbf{\\sigma}", '𝛔').
            put("\\mathbf{\\tau}", '𝛕').
            put("\\mathbf{\\upsilon}", '𝛖').
            put("\\mathbf{\\phi}", '𝛗').
            put("\\mathbf{\\chi}", '𝛘').
            put("\\mathbf{\\psi}", '𝛙').
            put("\\mathbf{\\omega}", '𝛚').
            put("\\mathbf{\\varepsilon}", '𝛜').
            put("\\mathbf{\\varkappa}", '𝛞').
            put("\\mathbf{\\varrho}", '𝛠').
            put("\\mathbf{\\varpi}", '𝛡').
            put("\\mathmit{\\Alpha}", '𝛢').
            put("\\mathmit{\\Beta}", '𝛣').
            put("\\mathmit{\\Gamma}", '𝛤').
            put("\\mathmit{\\Delta}", '𝛥').
            put("\\mathmit{\\Epsilon}", '𝛦').
            put("\\mathmit{\\Zeta}", '𝛧').
            put("\\mathmit{\\Eta}", '𝛨').
            put("\\mathmit{\\Theta}", '𝛩').
            put("\\mathmit{\\Iota}", '𝛪').
            put("\\mathmit{\\Kappa}", '𝛫').
            put("\\mathmit{\\Lambda}", '𝛬').
            put("\\mathmit{\\Xi}", '𝛯').
            put("\\mathmit{\\Pi}", '𝛱').
            put("\\mathmit{\\Rho}", '𝛲').
            put("\\mathmit{\\vartheta}", '𝛳').
            put("\\mathmit{\\Sigma}", '𝛴').
            put("\\mathmit{\\Tau}", '𝛵').
            put("\\mathmit{\\Upsilon}", '𝛶').
            put("\\mathmit{\\Phi}", '𝛷').
            put("\\mathmit{\\Chi}", '𝛸').
            put("\\mathmit{\\Psi}", '𝛹').
            put("\\mathmit{\\Omega}", '𝛺').
            put("\\mathmit{\\nabla}", '𝛻').
            put("\\mathmit{\\alpha}", '𝛼').
            put("\\mathmit{\\beta}", '𝛽').
            put("\\mathmit{\\gamma}", '𝛾').
            put("\\mathmit{\\delta}", '𝛿').
            put("\\mathmit{\\epsilon}", '𝜀').
            put("\\mathmit{\\zeta}", '𝜁').
            put("\\mathmit{\\eta}", '𝜂').
            put("\\mathmit{\\theta}", '𝜃').
            put("\\mathmit{\\iota}", '𝜄').
            put("\\mathmit{\\kappa}", '𝜅').
            put("\\mathmit{\\lambda}", '𝜆').
            put("\\mathmit{\\mu}", '𝜇').
            put("\\mathmit{\\nu}", '𝜈').
            put("\\mathmit{\\xi}", '𝜉').
            put("\\mathmit{\\pi}", '𝜋').
            put("\\mathmit{\\rho}", '𝜌').
            put("\\mathmit{\\varsigma}", '𝜍').
            put("\\mathmit{\\sigma}", '𝜎').
            put("\\mathmit{\\tau}", '𝜏').
            put("\\mathmit{\\upsilon}", '𝜐').
            put("\\mathmit{\\phi}", '𝜑').
            put("\\mathmit{\\chi}", '𝜒').
            put("\\mathmit{\\psi}", '𝜓').
            put("\\mathmit{\\omega}", '𝜔').
            put("\\mathmit{\\varkappa}", '𝜘').
            put("\\mathmit{\\varrho}", '𝜚').
            put("\\mathmit{\\varpi}", '𝜛').
            put("\\mathbit{\\Alpha}", '𝜜').
            put("\\mathbit{\\Beta}", '𝜝').
            put("\\mathbit{\\Gamma}", '𝜞').
            put("\\mathbit{\\Delta}", '𝜟').
            put("\\mathbit{\\Epsilon}", '𝜠').
            put("\\mathbit{\\Zeta}", '𝜡').
            put("\\mathbit{\\Eta}", '𝜢').
            put("\\mathbit{\\Theta}", '𝜣').
            put("\\mathbit{\\Iota}", '𝜤').
            put("\\mathbit{\\Kappa}", '𝜥').
            put("\\mathbit{\\Lambda}", '𝜦').
            put("\\mathbit{\\Xi}", '𝜩').
            put("\\mathbit{\\Pi}", '𝜫').
            put("\\mathbit{\\Rho}", '𝜬').
            put("\\mathbit{\\Sigma}", '𝜮').
            put("\\mathbit{\\Tau}", '𝜯').
            put("\\mathbit{\\Upsilon}", '𝜰').
            put("\\mathbit{\\Phi}", '𝜱').
            put("\\mathbit{\\Chi}", '𝜲').
            put("\\mathbit{\\Psi}", '𝜳').
            put("\\mathbit{\\Omega}", '𝜴').
            put("\\mathbit{\\nabla}", '𝜵').
            put("\\mathbit{\\alpha}", '𝜶').
            put("\\mathbit{\\beta}", '𝜷').
            put("\\mathbit{\\gamma}", '𝜸').
            put("\\mathbit{\\delta}", '𝜹').
            put("\\mathbit{\\epsilon}", '𝜺').
            put("\\mathbit{\\zeta}", '𝜻').
            put("\\mathbit{\\eta}", '𝜼').
            put("\\mathbit{\\theta}", '𝜽').
            put("\\mathbit{\\iota}", '𝜾').
            put("\\mathbit{\\kappa}", '𝜿').
            put("\\mathbit{\\lambda}", '𝝀').
            put("\\mathbit{\\mu}", '𝝁').
            put("\\mathbit{\\nu}", '𝝂').
            put("\\mathbit{\\xi}", '𝝃').
            put("\\mathbit{\\pi}", '𝝅').
            put("\\mathbit{\\rho}", '𝝆').
            put("\\mathbit{\\varsigma}", '𝝇').
            put("\\mathbit{\\sigma}", '𝝈').
            put("\\mathbit{\\tau}", '𝝉').
            put("\\mathbit{\\upsilon}", '𝝊').
            put("\\mathbit{\\phi}", '𝝋').
            put("\\mathbit{\\chi}", '𝝌').
            put("\\mathbit{\\psi}", '𝝍').
            put("\\mathbit{\\omega}", '𝝎').
            put("\\mathbit{\\vartheta}", '𝝑').
            put("\\mathbit{\\varkappa}", '𝝒').
            put("\\mathbit{\\varrho}", '𝝔').
            put("\\mathbit{\\varpi}", '𝝕').
            put("\\mathsfbf{\\Alpha}", '𝝖').
            put("\\mathsfbf{\\Beta}", '𝝗').
            put("\\mathsfbf{\\Gamma}", '𝝘').
            put("\\mathsfbf{\\Delta}", '𝝙').
            put("\\mathsfbf{\\Epsilon}", '𝝚').
            put("\\mathsfbf{\\Zeta}", '𝝛').
            put("\\mathsfbf{\\Eta}", '𝝜').
            put("\\mathsfbf{\\Theta}", '𝝝').
            put("\\mathsfbf{\\Iota}", '𝝞').
            put("\\mathsfbf{\\Kappa}", '𝝟').
            put("\\mathsfbf{\\Lambda}", '𝝠').
            put("\\mathsfbf{\\Xi}", '𝝣').
            put("\\mathsfbf{\\Pi}", '𝝥').
            put("\\mathsfbf{\\Rho}", '𝝦').
            put("\\mathsfbf{\\vartheta}", '𝝧').
            put("\\mathsfbf{\\Sigma}", '𝝨').
            put("\\mathsfbf{\\Tau}", '𝝩').
            put("\\mathsfbf{\\Upsilon}", '𝝪').
            put("\\mathsfbf{\\Phi}", '𝝫').
            put("\\mathsfbf{\\Chi}", '𝝬').
            put("\\mathsfbf{\\Psi}", '𝝭').
            put("\\mathsfbf{\\Omega}", '𝝮').
            put("\\mathsfbf{\\nabla}", '𝝯').
            put("\\mathsfbf{\\alpha}", '𝝰').
            put("\\mathsfbf{\\beta}", '𝝱').
            put("\\mathsfbf{\\gamma}", '𝝲').
            put("\\mathsfbf{\\delta}", '𝝳').
            put("\\mathsfbf{\\epsilon}", '𝝴').
            put("\\mathsfbf{\\zeta}", '𝝵').
            put("\\mathsfbf{\\eta}", '𝝶').
            put("\\mathsfbf{\\theta}", '𝝷').
            put("\\mathsfbf{\\iota}", '𝝸').
            put("\\mathsfbf{\\kappa}", '𝝹').
            put("\\mathsfbf{\\lambda}", '𝝺').
            put("\\mathsfbf{\\mu}", '𝝻').
            put("\\mathsfbf{\\nu}", '𝝼').
            put("\\mathsfbf{\\xi}", '𝝽').
            put("\\mathsfbf{\\pi}", '𝝿').
            put("\\mathsfbf{\\rho}", '𝞀').
            put("\\mathsfbf{\\varsigma}", '𝞁').
            put("\\mathsfbf{\\sigma}", '𝞂').
            put("\\mathsfbf{\\tau}", '𝞃').
            put("\\mathsfbf{\\upsilon}", '𝞄').
            put("\\mathsfbf{\\phi}", '𝞅').
            put("\\mathsfbf{\\chi}", '𝞆').
            put("\\mathsfbf{\\psi}", '𝞇').
            put("\\mathsfbf{\\omega}", '𝞈').
            put("\\mathsfbf{\\varepsilon}", '𝞊').
            put("\\mathsfbf{\\varkappa}", '𝞌').
            put("\\mathsfbf{\\varrho}", '𝞎').
            put("\\mathsfbf{\\varpi}", '𝞏').
            put("\\mathsfbfsl{\\Alpha}", '𝞐').
            put("\\mathsfbfsl{\\Beta}", '𝞑').
            put("\\mathsfbfsl{\\Gamma}", '𝞒').
            put("\\mathsfbfsl{\\Delta}", '𝞓').
            put("\\mathsfbfsl{\\Epsilon}", '𝞔').
            put("\\mathsfbfsl{\\Zeta}", '𝞕').
            put("\\mathsfbfsl{\\Eta}", '𝞖').
            put("\\mathsfbfsl{\\vartheta}", '𝞗').
            put("\\mathsfbfsl{\\Iota}", '𝞘').
            put("\\mathsfbfsl{\\Kappa}", '𝞙').
            put("\\mathsfbfsl{\\Lambda}", '𝞚').
            put("\\mathsfbfsl{\\Xi}", '𝞝').
            put("\\mathsfbfsl{\\Pi}", '𝞟').
            put("\\mathsfbfsl{\\Rho}", '𝞠').
            put("\\mathsfbfsl{\\Sigma}", '𝞢').
            put("\\mathsfbfsl{\\Tau}", '𝞣').
            put("\\mathsfbfsl{\\Upsilon}", '𝞤').
            put("\\mathsfbfsl{\\Phi}", '𝞥').
            put("\\mathsfbfsl{\\Chi}", '𝞦').
            put("\\mathsfbfsl{\\Psi}", '𝞧').
            put("\\mathsfbfsl{\\Omega}", '𝞨').
            put("\\mathsfbfsl{\\nabla}", '𝞩').
            put("\\mathsfbfsl{\\alpha}", '𝞪').
            put("\\mathsfbfsl{\\beta}", '𝞫').
            put("\\mathsfbfsl{\\gamma}", '𝞬').
            put("\\mathsfbfsl{\\delta}", '𝞭').
            put("\\mathsfbfsl{\\epsilon}", '𝞮').
            put("\\mathsfbfsl{\\zeta}", '𝞯').
            put("\\mathsfbfsl{\\eta}", '𝞰').
            put("\\mathsfbfsl{\\iota}", '𝞲').
            put("\\mathsfbfsl{\\kappa}", '𝞳').
            put("\\mathsfbfsl{\\lambda}", '𝞴').
            put("\\mathsfbfsl{\\mu}", '𝞵').
            put("\\mathsfbfsl{\\nu}", '𝞶').
            put("\\mathsfbfsl{\\xi}", '𝞷').
            put("\\mathsfbfsl{\\pi}", '𝞹').
            put("\\mathsfbfsl{\\rho}", '𝞺').
            put("\\mathsfbfsl{\\varsigma}", '𝞻').
            put("\\mathsfbfsl{\\sigma}", '𝞼').
            put("\\mathsfbfsl{\\tau}", '𝞽').
            put("\\mathsfbfsl{\\upsilon}", '𝞾').
            put("\\mathsfbfsl{\\phi}", '𝞿').
            put("\\mathsfbfsl{\\chi}", '𝟀').
            put("\\mathsfbfsl{\\psi}", '𝟁').
            put("\\mathsfbfsl{\\omega}", '𝟂').
            put("\\mathsfbfsl{\\varkappa}", '𝟆').
            put("\\mathsfbfsl{\\varrho}", '𝟈').
            put("\\mathsfbfsl{\\varpi}", '𝟉').
            put("\\mathbf{0}", '𝟎').
            put("\\mathbf{1}", '𝟏').
            put("\\mathbf{2}", '𝟐').
            put("\\mathbf{3}", '𝟑').
            put("\\mathbf{4}", '𝟒').
            put("\\mathbf{5}", '𝟓').
            put("\\mathbf{6}", '𝟔').
            put("\\mathbf{7}", '𝟕').
            put("\\mathbf{8}", '𝟖').
            put("\\mathbf{9}", '𝟗').
            put("\\mathbb{0}", '𝟘').
            put("\\mathbb{1}", '𝟙').
            put("\\mathbb{2}", '𝟚').
            put("\\mathbb{3}", '𝟛').
            put("\\mathbb{4}", '𝟜').
            put("\\mathbb{5}", '𝟝').
            put("\\mathbb{6}", '𝟞').
            put("\\mathbb{7}", '𝟟').
            put("\\mathbb{8}", '𝟠').
            put("\\mathbb{9}", '𝟡').
            put("\\mathsf{0}", '𝟢').
            put("\\mathsf{1}", '𝟣').
            put("\\mathsf{2}", '𝟤').
            put("\\mathsf{3}", '𝟥').
            put("\\mathsf{4}", '𝟦').
            put("\\mathsf{5}", '𝟧').
            put("\\mathsf{6}", '𝟨').
            put("\\mathsf{7}", '𝟩').
            put("\\mathsf{8}", '𝟪').
            put("\\mathsf{9}", '𝟫').
            put("\\mathsfbf{0}", '𝟬').
            put("\\mathsfbf{1}", '𝟭').
            put("\\mathsfbf{2}", '𝟮').
            put("\\mathsfbf{3}", '𝟯').
            put("\\mathsfbf{4}", '𝟰').
            put("\\mathsfbf{5}", '𝟱').
            put("\\mathsfbf{6}", '𝟲').
            put("\\mathsfbf{7}", '𝟳').
            put("\\mathsfbf{8}", '𝟴').
            put("\\mathsfbf{9}", '𝟵').
            put("\\mathtt{0}", '𝟶').
            put("\\mathtt{1}", '𝟷').
            put("\\mathtt{2}", '𝟸').
            put("\\mathtt{3}", '𝟹').
            put("\\mathtt{4}", '𝟺').
            put("\\mathtt{5}", '𝟻').
            put("\\mathtt{6}", '𝟼').
            put("\\mathtt{7}", '𝟽').
            put("\\mathtt{8}", '𝟾').
            put("\\mathtt{9}", '𝟿")*/
            build().inverse();
}
