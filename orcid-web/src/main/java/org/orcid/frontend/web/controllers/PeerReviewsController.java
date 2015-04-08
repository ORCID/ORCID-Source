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
package org.orcid.frontend.web.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.PeerReviewManager;
import org.orcid.frontend.web.util.LanguagesMap;
import org.orcid.jaxb.model.record.PeerReview;
import org.orcid.jaxb.model.record.PeerReviewType;
import org.orcid.jaxb.model.record.Role;
import org.orcid.persistence.dao.OrgDisambiguatedDao;
import org.orcid.persistence.dao.OrgDisambiguatedSolrDao;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;
import org.orcid.persistence.solr.entities.OrgDisambiguatedSolrDocument;
import org.orcid.pojo.ajaxForm.Date;
import org.orcid.pojo.ajaxForm.PeerReviewForm;
import org.orcid.pojo.ajaxForm.PeerReviewSubjectForm;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.pojo.ajaxForm.TranslatedTitle;
import org.orcid.pojo.ajaxForm.WorkExternalIdentifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Angel Montenegro
 */
@Controller("peerReviewsController")
@RequestMapping(value = { "/peer-reviews" })
public class PeerReviewsController extends BaseWorkspaceController {
    @Resource
    private LocaleManager localeManager;

    @Resource
    private PeerReviewManager peerReviewManager;
    
    @Resource
    private OrgDisambiguatedSolrDao orgDisambiguatedSolrDao;

    @Resource
    private OrgDisambiguatedDao orgDisambiguatedDao;
    
    @Resource(name = "languagesMap")
    private LanguagesMap lm;

    public void setLocaleManager(LocaleManager localeManager) {
        this.localeManager = localeManager;
    }
    
    /**
     * Returns a blank peer review form
     * */
    @RequestMapping(value = "/peer-review.json", method = RequestMethod.GET)
    public @ResponseBody
    PeerReviewForm getPeerReview() {
        Text emptyTextRequired = Text.valueOf(StringUtils.EMPTY);
        Text emptyTextNotRequired = Text.valueOf(StringUtils.EMPTY);
        emptyTextNotRequired.setRequired(false);
        
        Date emptyDate = new Date();
        emptyDate.setDay(StringUtils.EMPTY);
        emptyDate.setMonth(StringUtils.EMPTY);
        emptyDate.setYear(StringUtils.EMPTY);
        
        TranslatedTitle emptyTranslatedTitle = new TranslatedTitle();
        emptyTranslatedTitle.setErrors(Collections.<String> emptyList());
        emptyTranslatedTitle.setContent(StringUtils.EMPTY);
        emptyTranslatedTitle.setLanguageCode(StringUtils.EMPTY);
        emptyTranslatedTitle.setLanguageName(StringUtils.EMPTY);
        
        PeerReviewForm form = new PeerReviewForm();
                
        form.setErrors(Collections.<String> emptyList());
        
        form.setCompletionDate(emptyDate);
        form.setCreatedDate(emptyDate);
                       
        form.setCity(emptyTextRequired);
        form.setRegion(emptyTextNotRequired);
        form.setCountry(emptyTextRequired);
        form.setCountryForDisplay(StringUtils.EMPTY);
        form.setDisambiguationSource(emptyTextNotRequired);        
        form.setOrgName(emptyTextRequired);
        
        form.setPutCode(emptyTextNotRequired);
        form.setRole(Text.valueOf(Role.REVIEWER.value()));
        form.setType(Text.valueOf(PeerReviewType.REVIEW.value()));
        form.setUrl(emptyTextNotRequired);
        form.setExternalIdentifiers(Collections.<WorkExternalIdentifier> emptyList());
        
        PeerReviewSubjectForm subjectForm = new PeerReviewSubjectForm();
        subjectForm.setErrors(Collections.<String> emptyList());
        subjectForm.setJournalTitle(emptyTextNotRequired);
        subjectForm.setPutCode(emptyTextNotRequired);
        subjectForm.setSubtitle(emptyTextNotRequired);
        subjectForm.setTitle(emptyTextRequired);
        subjectForm.setTranslatedTitle(emptyTranslatedTitle);
        subjectForm.setUrl(emptyTextNotRequired);
        subjectForm.setWorkExternalIdentifiers(Collections.<WorkExternalIdentifier> emptyList());
        subjectForm.setWorkType(emptyTextRequired);
        
        form.setSubjectForm(subjectForm);                
        return form;
    }
    
    /**
     * Persist a funding object on database
     * */
    @RequestMapping(value = "/peer-review.json", method = RequestMethod.POST)
    public @ResponseBody
    PeerReviewForm postPeerReview(@RequestBody PeerReviewForm peerReview) throws Exception {
        // Reset errors
        peerReview.setErrors(new ArrayList<String>());
        // Remove empty external identifiers
        removeEmptyExternalIds(peerReview);
        
        validateOrgName(peerReview);
        validateCity(peerReview);
        validateRegion(peerReview);
        validateCountry(peerReview);
        validateUrl(peerReview);
        validateCompletionDate(peerReview);
        copyErrors(peerReview.getOrgName(), peerReview);
        copyErrors(peerReview.getCity(), peerReview);
        copyErrors(peerReview.getRegion(), peerReview);
        copyErrors(peerReview.getCountry(), peerReview);
        copyErrors(peerReview.getUrl(), peerReview);
        copyErrors(peerReview.getCompletionDate(), peerReview);
        
        //If there are no errors, persist to DB
        if(peerReview.getErrors().isEmpty()) {            
            if(PojoUtil.isEmpty(peerReview.getPutCode())) {
                addPeerReview(peerReview);
            } else {
                editPeerReview(peerReview);
            }
        }
        
        return peerReview;
    }
    
    private PeerReviewForm addPeerReview(PeerReviewForm peerReviewForm) {
        String userOrcid = getEffectiveUserOrcid();        
        PeerReview peerReview = peerReviewForm.toPeerReview();        
        peerReview = peerReviewManager.createPeerReview(userOrcid, peerReview);
        peerReviewForm = PeerReviewForm.fromPeerReview(peerReview);
        return peerReviewForm; 
    }
    
    private void editPeerReview(PeerReviewForm peerReview) {
        
    }
    
    private void removeEmptyExternalIds(PeerReviewForm peerReview) {
        List<WorkExternalIdentifier> extIds = peerReview.getExternalIdentifiers();
        List<WorkExternalIdentifier> updatedExtIds = new ArrayList<WorkExternalIdentifier>();
        if (extIds != null) {
            // For all external identifiers
            for (WorkExternalIdentifier extId : extIds) {
                // Keep only the ones that contains a value or url
                if (!PojoUtil.isEmpty(extId.getWorkExternalIdentifierId())) {
                    updatedExtIds.add(extId);
                }
            }
        }
        peerReview.setExternalIdentifiers(updatedExtIds);
    }
    
    @RequestMapping(value = "/orgNameValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    PeerReviewForm validateOrgName(@RequestBody PeerReviewForm peerReview) {
        peerReview.getOrgName().setErrors(new ArrayList<String>());
        if (PojoUtil.isEmpty(peerReview.getOrgName())) {
            setError(peerReview.getOrgName(), "org.name.not_blank");
        } else {
            if (peerReview.getOrgName().getValue().trim().length() > 1000) {
                setError(peerReview.getOrgName(), "common.length_less_1000");
            }
        }
        return peerReview;
    }
    
    @RequestMapping(value = "/cityValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    PeerReviewForm validateCity(@RequestBody PeerReviewForm peerReview) {
        peerReview.getCity().setErrors(new ArrayList<String>());
        if (peerReview.getCity().getValue() == null || peerReview.getCity().getValue().trim().length() == 0) {
            setError(peerReview.getCity(), "org.name.not_blank");
        } else {
            if (peerReview.getCity().getValue().trim().length() > 1000) {
                setError(peerReview.getCity(), "common.length_less_1000");
            }
        }
        return peerReview;
    }

    @RequestMapping(value = "/regionValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    PeerReviewForm validateRegion(@RequestBody PeerReviewForm peerReview) {
        peerReview.getRegion().setErrors(new ArrayList<String>());
        if (peerReview.getRegion().getValue() != null && peerReview.getRegion().getValue().trim().length() > 1000) {
            setError(peerReview.getRegion(), "common.length_less_1000");
        }
        return peerReview;
    }

    @RequestMapping(value = "/countryValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    PeerReviewForm validateCountry(@RequestBody PeerReviewForm peerReview) {
        peerReview.getCountry().setErrors(new ArrayList<String>());
        if (peerReview.getCountry().getValue() == null || peerReview.getCountry().getValue().trim().length() == 0) {
            setError(peerReview.getCountry(), "common.country.not_empty");
        }
        return peerReview;
    }
    
    @RequestMapping(value = "/urlValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    PeerReviewForm validateUrl(@RequestBody PeerReviewForm peerReview) {        
        validateUrl(peerReview.getUrl());
        return peerReview;
    }
    
    @RequestMapping(value = "/completionDateValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    PeerReviewForm validateCompletionDate(@RequestBody PeerReviewForm peerReview) {
        if(peerReview.getCompletionDate() != null){
            peerReview.getCompletionDate().setErrors(new ArrayList<String>());
            
            if(!PojoUtil.isEmpty(peerReview.getCompletionDate().getMonth()) && PojoUtil.isEmpty(peerReview.getCompletionDate().getYear())){
                setError(peerReview.getCompletionDate(), "common.dates.invalid");
            }                
        }
                    
        return peerReview;
    }
    
    @RequestMapping(value = "/subject/titleValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    PeerReviewForm validateSubjectTitle(@RequestBody PeerReviewForm peerReview) {
        if(peerReview.getSubjectForm() != null) {
            peerReview.getSubjectForm().getTitle().setErrors(new ArrayList<String>());
            if(PojoUtil.isEmpty(peerReview.getSubjectForm().getTitle())) {
                setError(peerReview.getSubjectForm().getTitle(), "common.title.not_blank");
            } else if(peerReview.getSubjectForm().getTitle().getValue().length() > 100) {
                setError(peerReview.getSubjectForm().getTitle(), "common.length_less_1000");
            }
        }
        
        return peerReview;
    }
    
    @RequestMapping(value = "/roleValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    PeerReviewForm validateRole(@RequestBody PeerReviewForm peerReview) {
        peerReview.getRole().setErrors(new ArrayList<String>());
        if(PojoUtil.isEmpty(peerReview.getRole())) {            
            setError(peerReview.getRole(), "peer_review.role.not_blank");            
        }
        
        return peerReview;
    }
    
    @RequestMapping(value = "/typeValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    PeerReviewForm validateType(@RequestBody PeerReviewForm peerReview) {
        peerReview.getType().setErrors(new ArrayList<String>());
        if(PojoUtil.isEmpty(peerReview.getType())) {            
            setError(peerReview.getType(), "peer_review.type.not_blank");            
        }
        
        return peerReview;
    }
    
    
    
    
    
    /**
     * Typeahead
     * */

    /**
     * Search DB for disambiguated affiliations to suggest to user
     */
    @RequestMapping(value = "/disambiguated/name/{query}", method = RequestMethod.GET)
    public @ResponseBody
    List<Map<String, String>> searchDisambiguated(@PathVariable("query") String query, @RequestParam(value = "limit") int limit) {
        List<Map<String, String>> datums = new ArrayList<>();
        for (OrgDisambiguatedSolrDocument orgDisambiguatedDocument : orgDisambiguatedSolrDao.getOrgs(query, 0, limit)) {
            Map<String, String> datum = createDatumFromOrgDisambiguated(orgDisambiguatedDocument);
            datums.add(datum);
        }
        return datums;
    }

    private Map<String, String> createDatumFromOrgDisambiguated(OrgDisambiguatedSolrDocument orgDisambiguatedDocument) {
        Map<String, String> datum = new HashMap<>();
        datum.put("value", orgDisambiguatedDocument.getOrgDisambiguatedName());
        datum.put("city", orgDisambiguatedDocument.getOrgDisambiguatedCity());
        datum.put("region", orgDisambiguatedDocument.getOrgDisambiguatedRegion());
        datum.put("country", orgDisambiguatedDocument.getOrgDisambiguatedCountry());
        datum.put("orgType", orgDisambiguatedDocument.getOrgDisambiguatedType());
        datum.put("disambiguatedOrganizationIdentifier", Long.toString(orgDisambiguatedDocument.getOrgDisambiguatedId()));
        return datum;
    }

    /**
     * fetch disambiguated by id
     */
    @RequestMapping(value = "/disambiguated/id/{id}", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, String> getDisambiguated(@PathVariable("id") Long id) {
        OrgDisambiguatedEntity orgDisambiguatedEntity = orgDisambiguatedDao.find(id);
        Map<String, String> datum = new HashMap<>();
        datum.put("value", orgDisambiguatedEntity.getName());
        datum.put("city", orgDisambiguatedEntity.getCity());
        datum.put("region", orgDisambiguatedEntity.getRegion());
        if (orgDisambiguatedEntity.getCountry() != null)
            datum.put("country", orgDisambiguatedEntity.getCountry().value());
        datum.put("sourceId", orgDisambiguatedEntity.getSourceId());
        datum.put("sourceType", orgDisambiguatedEntity.getSourceType());
        return datum;
    }
}



















