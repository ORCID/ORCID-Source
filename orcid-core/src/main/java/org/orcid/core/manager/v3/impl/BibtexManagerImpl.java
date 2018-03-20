package org.orcid.core.manager.v3.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.manager.v3.ActivitiesSummaryManager;
import org.orcid.core.manager.v3.BibtexManager;
import org.orcid.core.manager.DOIManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.manager.v3.WorkManager;
import org.orcid.jaxb.model.v3.dev1.common.Contributor;
import org.orcid.jaxb.model.v3.dev1.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.dev1.record.summary.WorkGroup;
import org.orcid.jaxb.model.v3.dev1.record.summary.WorkSummary;
import org.orcid.jaxb.model.record_rc1.WorkExternalIdentifierType;
import org.orcid.jaxb.model.v3.dev1.record.CitationType;
import org.orcid.jaxb.model.v3.dev1.record.ExternalID;
import org.orcid.jaxb.model.v3.dev1.record.Work;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.RecordNameEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;

public class BibtexManagerImpl implements BibtexManager{

    private static final Logger LOGGER = LoggerFactory.getLogger(BibtexManagerImpl.class);
    
    @Resource(name = "activitiesSummaryManagerV3")
    private ActivitiesSummaryManager activitiesManager;
    
    @Resource(name = "workManagerV3")
    private WorkManager workManager;
    
    @Resource(name = "profileEntityManagerV3")
    private ProfileEntityManager profileEntityManager;
    
    @Resource 
    private DOIManager doiManager;
    
    private static volatile ImmutableMap<Character,String> escapeW3C = null;
    private static Object initLock = new Object();
    
    public BibtexManagerImpl(){
        if (escapeW3C == null){
            synchronized(initLock){
                if (escapeW3C == null){
                    CsvMapper mapper = new CsvMapper();
                    mapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);
                    try {
                        MappingIterator<String[]> it = mapper.reader(String[].class).readValues(getClass().getResourceAsStream("/org/orcid/core/manager/impl/escape_bibtex.txt"));
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
        }
    }
    
    @Override
    public String generateBibtexReferenceList(String orcid) {
        ActivitiesSummary summary = activitiesManager.getActivitiesSummary(orcid);
        List<String> citations = new ArrayList<String>();
        if (summary.getWorks()!=null){
            for (WorkGroup group : summary.getWorks().getWorkGroup()){
                WorkSummary workSummary = group.getWorkSummary().get(0);
                Work work = workManager.getWork(orcid, workSummary.getPutCode());
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
       //for (int i=0 ; i < text.length();i++){
           //char ch = text.charAt(i);
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
                            sb.append(ch); //here we just spit out the utf-8.  This is what GS does...
                    }
            }
        }
        return sb.toString();
    }
    
}
