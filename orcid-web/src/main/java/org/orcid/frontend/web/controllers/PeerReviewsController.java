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
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.locale.LocaleManager;
import org.orcid.frontend.web.util.LanguagesMap;
import org.orcid.jaxb.model.record.PeerReviewType;
import org.orcid.jaxb.model.record.Role;
import org.orcid.pojo.ajaxForm.Date;
import org.orcid.pojo.ajaxForm.PeerReviewForm;
import org.orcid.pojo.ajaxForm.PeerReviewSubjectForm;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.pojo.ajaxForm.TranslatedTitle;
import org.orcid.pojo.ajaxForm.WorkExternalIdentifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Angel Montenegro
 */
@Controller("peerReviewsController")
@RequestMapping(value = { "/peer-reviews" })
public class PeerReviewsController extends BaseWorkspaceController {
    @Resource
    private LocaleManager localeManager;

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
        Text emptyText = Text.valueOf(StringUtils.EMPTY);
        
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
                       
        form.setCity(emptyText);
        form.setRegion(emptyText);
        form.setCountry(emptyText);
        form.setCountryForDisplay(StringUtils.EMPTY);
        form.setDisambiguationSource(emptyText);
        form.setDisambiguatedOrganizationId(emptyText);
        
        form.setPutCode(emptyText);
        form.setRole(Text.valueOf(Role.REVIEWER.value()));
        form.setType(Text.valueOf(PeerReviewType.REVIEW.value()));
        form.setUrl(emptyText);
        form.setExternalIdentifiers(Collections.<WorkExternalIdentifier> emptyList());
        
        PeerReviewSubjectForm subjectForm = new PeerReviewSubjectForm();
        subjectForm.setErrors(Collections.<String> emptyList());
        subjectForm.setJournalTitle(emptyText);
        subjectForm.setPutCode(emptyText);
        subjectForm.setSubtitle(emptyText);
        subjectForm.setTitle(emptyText);
        subjectForm.setTranslatedTitle(emptyTranslatedTitle);
        subjectForm.setUrl(emptyText);
        subjectForm.setWorkExternalIdentifiers(Collections.<WorkExternalIdentifier> emptyList());
        subjectForm.setWorkType(emptyText);
        
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
        
        
        return peerReview;
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
    
    @RequestMapping(value = "/peer-review/orgNameValidate.json", method = RequestMethod.POST)
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
    
    @RequestMapping(value = "/peer-review/cityValidate.json", method = RequestMethod.POST)
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

    @RequestMapping(value = "/peer-review/regionValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    PeerReviewForm validateRegion(@RequestBody PeerReviewForm peerReview) {
        peerReview.getRegion().setErrors(new ArrayList<String>());
        if (peerReview.getRegion().getValue() != null && peerReview.getRegion().getValue().trim().length() > 1000) {
            setError(peerReview.getRegion(), "common.length_less_1000");
        }
        return peerReview;
    }

    @RequestMapping(value = "/peer-review/countryValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    PeerReviewForm validateCountry(@RequestBody PeerReviewForm peerReview) {
        peerReview.getCountry().setErrors(new ArrayList<String>());
        if (peerReview.getCountry().getValue() == null || peerReview.getCountry().getValue().trim().length() == 0) {
            setError(peerReview.getCountry(), "common.country.not_empty");
        }
        return peerReview;
    }
    
    @RequestMapping(value = "/peer-review/urlValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    PeerReviewForm validateUrl(@RequestBody PeerReviewForm peerReview) {        
        validateUrl(peerReview.getUrl());
        return peerReview;
    }
    
    @RequestMapping(value = "/peer-review/completionDateValidate.json", method = RequestMethod.POST)
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
    
    @RequestMapping(value = "/peer-review/subject/titleValidate.json", method = RequestMethod.POST)
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
    
}



















