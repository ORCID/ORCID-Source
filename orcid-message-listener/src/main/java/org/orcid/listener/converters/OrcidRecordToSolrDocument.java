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
package org.orcid.listener.converters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.orcid.jaxb.model.record.summary_rc4.FundingGroup;
import org.orcid.jaxb.model.record.summary_rc4.FundingSummary;
import org.orcid.jaxb.model.record.summary_rc4.WorkGroup;
import org.orcid.jaxb.model.record.summary_rc4.WorkSummary;
import org.orcid.jaxb.model.record_rc1.WorkExternalIdentifierType;
import org.orcid.jaxb.model.record_rc4.ExternalID;
import org.orcid.jaxb.model.record_rc4.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_rc4.Record;
import org.orcid.jaxb.model.record_rc4.Relationship;
import org.orcid.utils.NullUtils;
import org.orcid.utils.solr.entities.OrcidSolrDocument;
import org.orcid.utils.solr.entities.SolrConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrcidRecordToSolrDocument {
    
    private final boolean indexProfile;
  
    public OrcidRecordToSolrDocument(boolean indexProfile){
        this.indexProfile=indexProfile;
    }
    
    
    Logger LOG = LoggerFactory.getLogger(OrcidRecordToSolrDocument.class);

    public OrcidSolrDocument convert(Record record, String v12profileXML) {
        OrcidSolrDocument profileIndexDocument = new OrcidSolrDocument();
        profileIndexDocument.setOrcid(record.getOrcidIdentifier().getPath());
        
        
        if(record.getHistory() != null) {
            if (record.getHistory().getLastModifiedDate() != null){
                profileIndexDocument.setProfileLastModifiedDate(record.getHistory().getLastModifiedDate().getValue().toGregorianCalendar().getTime());            
            }
            if (record.getHistory().getSubmissionDate() != null){
                profileIndexDocument.setProfileSubmissionDate(record.getHistory().getSubmissionDate().getValue().toGregorianCalendar().getTime());
            }
        }
        
        if (record.getDeprecated() != null) {
            profileIndexDocument.setPrimaryRecord(record.getDeprecated().getPrimaryRecord() != null ? record.getDeprecated().getPrimaryRecord().getOrcidIdentifier().getPath() : null);
        }

        if (record.getPerson() != null) {
            if (record.getPerson().getName() !=null){
                profileIndexDocument.setFamilyName(record.getPerson().getName().getFamilyName() != null ? record.getPerson().getName().getFamilyName().getContent() : null);
                profileIndexDocument.setGivenNames(record.getPerson().getName().getGivenNames() != null ? record.getPerson().getName().getGivenNames().getContent() : null);
                profileIndexDocument.setCreditName(record.getPerson().getName().getCreditName() != null ? record.getPerson().getName().getCreditName().getContent() : null);                
                
            }
            if (record.getPerson().getOtherNames() != null){
                if (record.getPerson().getOtherNames().getOtherNames() != null && !record.getPerson().getOtherNames().getOtherNames().isEmpty()){
                    List<String> names = new ArrayList<String>();
                    for (org.orcid.jaxb.model.record_rc4.OtherName on : record.getPerson().getOtherNames().getOtherNames()){
                        names.add(on.getContent());
                    }
                    profileIndexDocument.setOtherNames(names);
                }
            }

            if (record.getPerson().getEmails() != null && record.getPerson().getEmails().getEmails() != null){
                for (org.orcid.jaxb.model.record_rc4.Email e : record.getPerson().getEmails().getEmails()){
                    profileIndexDocument.addEmailAddress(e.getEmail());
                }
            }
            
            //weird, the type is not indexed...!
            if (record.getPerson().getExternalIdentifiers() != null && record.getPerson().getExternalIdentifiers().getExternalIdentifiers() != null){
                List<String> extIdOrcids = new ArrayList<String>();
                List<String> extIdRefs = new ArrayList<String>();
                List<String> extIdOrcidsAndRefs = new ArrayList<String>();
                for (PersonExternalIdentifier externalIdentifier : record.getPerson().getExternalIdentifiers().getExternalIdentifiers()){
                        String sourcePath = null;
                        if (externalIdentifier.getSource() != null && externalIdentifier.getSource().retrieveSourcePath() != null) {
                                sourcePath = externalIdentifier.getSource().retrieveSourcePath();
                                extIdOrcids.add(sourcePath);
                        }
                        if (externalIdentifier.getValue() != null) {
                            extIdRefs.add(externalIdentifier.getValue());//weird, the type is not indexed...!
                        }
                        if (NullUtils.noneNull(sourcePath, externalIdentifier.getValue())) {
                            extIdOrcidsAndRefs.add(sourcePath + "=" + externalIdentifier.getValue());
                        }
                }
                if (!extIdOrcids.isEmpty()) {
                    profileIndexDocument.setExternalIdSources(extIdOrcids);
                }
                if (!extIdRefs.isEmpty()) {
                    profileIndexDocument.setExternalIdReferences(extIdRefs);
                }
                if (!extIdOrcidsAndRefs.isEmpty()) {
                    profileIndexDocument.setExternalIdSourcesAndReferences(extIdOrcidsAndRefs);
                }
            }

            //weird, we only index keywords if activities exist...!
            if (record.getActivitiesSummary() != null){
                if (record.getPerson().getKeywords() != null && record.getPerson().getKeywords().getKeywords() != null){
                    List<String> keywordValues = new ArrayList<String>();
                    for (org.orcid.jaxb.model.record_rc4.Keyword keyword : record.getPerson().getKeywords().getKeywords()) {
                        keywordValues.add(keyword.getContent());
                    }
                    profileIndexDocument.setKeywords(keywordValues);
                }
            }
            
            if (record.getActivitiesSummary() != null && record.getActivitiesSummary().getWorks() != null && record.getActivitiesSummary().getWorks().getWorkGroup() != null){
                
                //work ids
                Map<String, List<String>> allExternalIdentifiers = new HashMap<String, List<String>>();
                Map<String, List<String>> partOf = new HashMap<String, List<String>>();
                Map<String, List<String>> self = new HashMap<String, List<String>>();  
                Set<String> workTitles = new HashSet<String>();
                for (WorkGroup wg : record.getActivitiesSummary().getWorks().getWorkGroup()){
                    if (wg.getWorkSummary()!=null){
                        for (WorkSummary w : wg.getWorkSummary()){ // have to use summaries here as group does not include part-of
                            if (w.getExternalIdentifiers() != null && w.getExternalIdentifiers().getExternalIdentifier() != null){
                                for (ExternalID id : w.getExternalIdentifiers().getExternalIdentifier()){
                                    //old way
                                    if (!allExternalIdentifiers.containsKey(id.getType())){
                                        allExternalIdentifiers.put(id.getType(), new ArrayList<String>());
                                    }
                                    if (!allExternalIdentifiers.get(id.getType()).contains(id.getValue())){
                                        allExternalIdentifiers.get(id.getType()).add(id.getValue());
                                    }
                                    //new way
                                    if (Relationship.SELF.equals(id.getRelationship())){
                                        if (!self.containsKey(id.getType()+SolrConstants.DYNAMIC_SELF)){
                                            self.put(id.getType()+SolrConstants.DYNAMIC_SELF, new ArrayList<String>());
                                        } 
                                        if (!self.get(id.getType()+SolrConstants.DYNAMIC_SELF).contains(id.getValue())){
                                            self.get(id.getType()+SolrConstants.DYNAMIC_SELF).add(id.getValue());
                                        }
                                    }
                                    if (Relationship.PART_OF.equals(id.getRelationship())){
                                        if (!partOf.containsKey(id.getType()+SolrConstants.DYNAMIC_PART_OF)){
                                            partOf.put(id.getType()+SolrConstants.DYNAMIC_PART_OF, new ArrayList<String>());
                                        }                                 
                                        if (!partOf.get(id.getType()+SolrConstants.DYNAMIC_PART_OF).contains(id.getValue())){
                                            partOf.get(id.getType()+SolrConstants.DYNAMIC_PART_OF).add(id.getValue());
                                        }
                                    }
                                }
                            }
                            if (w.getTitle() != null){
                                if (w.getTitle().getTitle() !=null && StringUtils.isNotEmpty(w.getTitle().getTitle().getContent())){
                                    workTitles.add(w.getTitle().getTitle().getContent());
                                }
                                if (w.getTitle().getSubtitle() !=null && StringUtils.isNotEmpty(w.getTitle().getSubtitle().getContent())){
                                    workTitles.add(w.getTitle().getSubtitle().getContent());
                                }
                                if (w.getTitle().getTranslatedTitle() !=null && StringUtils.isNotEmpty(w.getTitle().getTranslatedTitle().getContent())){
                                    workTitles.add(w.getTitle().getTranslatedTitle().getContent());
                                }
                            }
                        }
                    }
                }
                profileIndexDocument.setSelfIds(self);
                profileIndexDocument.setPartOfIds(partOf);
                //now add them to the doc, the old way
                addExternalIdentifiersToIndexDocument(profileIndexDocument, allExternalIdentifiers);                
                profileIndexDocument.setWorkTitles(new ArrayList<String>(workTitles));
            }

            if (record.getActivitiesSummary() != null && record.getActivitiesSummary().getFundings() != null && record.getActivitiesSummary().getFundings().getFundingGroup() != null){
                Set<String> fundingTitle = new HashSet<String>();
                for (FundingGroup group : record.getActivitiesSummary().getFundings().getFundingGroup()){
                    if (group.getFundingSummary() !=null){
                        for (FundingSummary f : group.getFundingSummary()){
                            if (f.getTitle() != null){
                                if (f.getTitle().getTitle() != null && StringUtils.isNotEmpty(f.getTitle().getTitle().getContent())){
                                    fundingTitle.add(f.getTitle().getTitle().getContent());
                                }
                                if (f.getTitle().getTranslatedTitle() != null && StringUtils.isNotEmpty(f.getTitle().getTranslatedTitle().getContent())){
                                    fundingTitle.add(f.getTitle().getTranslatedTitle().getContent());
                                }
                            }
                        }                        
                    }
                }
                profileIndexDocument.setFundingTitles(new ArrayList<String>(fundingTitle));
            }
            
            //now do affiliations
        }

        if (indexProfile)
            profileIndexDocument.setPublicProfileMessage(v12profileXML);
        
        LOG.debug(profileIndexDocument.toString());
        return profileIndexDocument;
    }
    
    /**
     * Fill all the different external identifiers in the profile index
     * document.
     * 
     * @param profileIndexDocument
     *            The document that will be indexed by solr
     * @param externalIdentifiers
     *            The list of external identifiers
     */
    private void addExternalIdentifiersToIndexDocument(OrcidSolrDocument profileIndexDocument, Map<String, List<String>> externalIdentifiers) {

        Iterator<Entry<String, List<String>>> it = externalIdentifiers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, List<String>> entry = (Map.Entry<String, List<String>>) it.next();
            if (entry.getKey() != null && entry.getValue() != null && !entry.getValue().isEmpty()) {
                
                switch (WorkExternalIdentifierType.fromValue(entry.getKey())) {
                case AGR:
                    profileIndexDocument.setAgr(entry.getValue());
                    break;
                case ARXIV:
                    profileIndexDocument.setArxiv(entry.getValue());
                    break;
                case ASIN:
                    profileIndexDocument.setAsin(entry.getValue());
                    break;
                case ASIN_TLD:
                    profileIndexDocument.setAsintld(entry.getValue());
                    break;
                case BIBCODE:
                    profileIndexDocument.setBibcode(entry.getValue());
                    break;
                case CBA:
                    profileIndexDocument.setCba(entry.getValue());
                    break;
                case CIT:
                    profileIndexDocument.setCit(entry.getValue());
                    break;
                case CTX:
                    profileIndexDocument.setCtx(entry.getValue());
                    break;
                case DOI:
                    profileIndexDocument.setDigitalObjectIds(entry.getValue());
                    break;
                case EID:
                    profileIndexDocument.setEid(entry.getValue());
                    break;
                case ETHOS:
                    profileIndexDocument.setEthos(entry.getValue());
                    break;
                case HANDLE:
                    profileIndexDocument.setHandle(entry.getValue());
                    break;
                case HIR:
                    profileIndexDocument.setHir(entry.getValue());
                    break;
                case ISBN:
                    profileIndexDocument.setIsbn(entry.getValue());
                    break;
                case ISSN:
                    profileIndexDocument.setIssn(entry.getValue());
                    break;
                case JFM:
                    profileIndexDocument.setJfm(entry.getValue());
                    break;
                case JSTOR:
                    profileIndexDocument.setJstor(entry.getValue());
                    break;
                case LCCN:
                    profileIndexDocument.setLccn(entry.getValue());
                    break;
                case MR:
                    profileIndexDocument.setMr(entry.getValue());
                    break;
                case OCLC:
                    profileIndexDocument.setOclc(entry.getValue());
                    break;
                case OL:
                    profileIndexDocument.setOl(entry.getValue());
                    break;
                case OSTI:
                    profileIndexDocument.setOsti(entry.getValue());
                    break;
                case OTHER_ID:
                    profileIndexDocument.setOtherIdentifierType(entry.getValue());
                    break;
                case PAT:
                    profileIndexDocument.setPat(entry.getValue());
                    break;
                case PMC:
                    profileIndexDocument.setPmc(entry.getValue());
                    break;
                case PMID:
                    profileIndexDocument.setPmid(entry.getValue());
                    break;
                case RFC:
                    profileIndexDocument.setRfc(entry.getValue());
                    break;
                case SOURCE_WORK_ID:
                    profileIndexDocument.setSourceWorkId(entry.getValue());
                    break;
                case SSRN:
                    profileIndexDocument.setSsrn(entry.getValue());
                    break;
                case URI:
                    profileIndexDocument.setUri(entry.getValue());
                    break;
                case URN:
                    profileIndexDocument.setUrn(entry.getValue());
                    break;
                case WOSUID:
                    profileIndexDocument.setWosuid(entry.getValue());
                case ZBL:
                    profileIndexDocument.setZbl(entry.getValue());
                    break;
                }
            }
        }
    }
}
