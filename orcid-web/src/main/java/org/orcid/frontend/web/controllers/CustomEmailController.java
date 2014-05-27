/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
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
import java.util.List;

import javax.annotation.Resource;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;

import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.core.manager.CustomEmailManager;
import org.orcid.core.manager.LoadOptions;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.persistence.jpa.entities.CustomEmailEntity;
import org.orcid.persistence.jpa.entities.EmailType;
import org.orcid.pojo.ajaxForm.CustomEmailForm;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.utils.OrcidStringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Angel Montenegro
 */

@Controller
@RequestMapping(value = { "/custom-emails" })
public class CustomEmailController extends BaseController {

    private static final String DEFAULT_CLAIM_SENDER = "claim@notify.orcid.org";
    private static final int SUBJECT_MAX_LENGTH = 255;
    
    @Resource
    CustomEmailManager customEmailManager;
    @Resource
    ClientDetailsManager clientDetailsManager;
    
    @RequestMapping
    public ModelAndView manageDeveloperTools() {
        ModelAndView mav = new ModelAndView("custom_emails");
        OrcidProfile profile = orcidProfileManager.retrieveOrcidProfile(getCurrentUserOrcid(), LoadOptions.BIO_AND_INTERNAL_ONLY);
        mav.addObject("profile", profile);        
        return mav;
    }
    
    @RequestMapping(value = "/get-empty.json", method = RequestMethod.GET)
    public @ResponseBody
    CustomEmailForm getEmptyCustomEmailForm(HttpServletRequest request) {
        CustomEmailForm result = new CustomEmailForm();
        result.setSubject(Text.valueOf(""));
        result.setContent(Text.valueOf(""));
        result.setSender(Text.valueOf(""));
        result.setHtml(true);
        result.setEmailType(Text.valueOf(EmailType.CLAIM.name()));
        return result;
    }
    
    @RequestMapping(value = "/create.json", method = RequestMethod.POST)
    public @ResponseBody
    CustomEmailForm createCustomEmailForm(HttpServletRequest request, @RequestBody CustomEmailForm customEmailForm) {
        String currentOrcid = getEffectiveUserOrcid();
        customEmailForm.setErrors(new ArrayList<String>());
        if(clientDetailsManager.exists(currentOrcid)) {
            //Validate
            validateEmailType(customEmailForm);
            validateSender(customEmailForm);
            validateSubject(customEmailForm);
            validateContent(customEmailForm);                       
            
            copyErrors(customEmailForm.getEmailType(), customEmailForm);
            copyErrors(customEmailForm.getSender(), customEmailForm);
            copyErrors(customEmailForm.getSubject(), customEmailForm);
            copyErrors(customEmailForm.getContent(), customEmailForm);
            
            //If valid
            if(customEmailForm.getErrors().isEmpty()) {
                EmailType emailType = EmailType.valueOf(customEmailForm.getEmailType().getValue());
                boolean isHtml = customEmailForm.isHtml();
                String sender = "";
                if(PojoUtil.isEmpty(customEmailForm.getSender())) {
                    sender = DEFAULT_CLAIM_SENDER;
                } else {
                    sender = customEmailForm.getSender().getValue();
                }
                
                String subject = "";
                if(PojoUtil.isEmpty(customEmailForm.getSubject())) {
                    subject = getMessage("email.subject.api_record_creation");
                } else {
                    subject = customEmailForm.getSubject().getValue();
                }
                
                String content = customEmailForm.getContent().getValue();
                
                customEmailManager.createCustomEmail(currentOrcid, emailType, sender, subject, content, isHtml);
            }                        
            
        }                 
        return customEmailForm;
    }
    
    @RequestMapping(value = "/update.json", method = RequestMethod.POST)
    public @ResponseBody
    CustomEmailForm updateCustomEmailForm(HttpServletRequest request, @RequestBody CustomEmailForm customEmailForm) {
        String currentOrcid = getEffectiveUserOrcid();
        if(clientDetailsManager.exists(currentOrcid)) {
            //Validate
            validateEmailType(customEmailForm);
            validateSender(customEmailForm);
            validateSubject(customEmailForm);
            validateContent(customEmailForm);
                        
            copyErrors(customEmailForm.getEmailType(), customEmailForm);
            copyErrors(customEmailForm.getSender(), customEmailForm);
            copyErrors(customEmailForm.getSubject(), customEmailForm);
            copyErrors(customEmailForm.getContent(), customEmailForm);
            
            //If valid
            if(customEmailForm.getErrors().isEmpty()) {
                EmailType emailType = EmailType.valueOf(customEmailForm.getEmailType().getValue());
                boolean isHtml = customEmailForm.isHtml();
                String sender = "";
                if(PojoUtil.isEmpty(customEmailForm.getSender())) {
                    sender = DEFAULT_CLAIM_SENDER;
                } else {
                    sender = OrcidStringUtils.stripHtml(customEmailForm.getSender().getValue());
                }
                
                String subject = "";
                if(PojoUtil.isEmpty(customEmailForm.getSubject())) {
                    subject = getMessage("email.subject.api_record_creation");
                } else {
                    subject = OrcidStringUtils.stripHtml(customEmailForm.getSubject().getValue());
                }
                
                String content = customEmailForm.getContent().getValue();
                
                customEmailManager.updateCustomEmail(currentOrcid, emailType, sender, subject, content, isHtml);
            }
        }
        return customEmailForm;
    }
    
    @RequestMapping(value = "/delete.json", method = RequestMethod.POST)
    public @ResponseBody
    boolean deleteCustomEmailForm(HttpServletRequest request, @RequestBody CustomEmailForm customEmailForm) {
        String currentOrcid = getEffectiveUserOrcid();
        EmailType type = null;
        if(!PojoUtil.isEmpty(customEmailForm.getEmailType())) {
            type = EmailType.valueOf(customEmailForm.getEmailType().getValue());
        }
        if(currentOrcid != null && type != null)
            return customEmailManager.deleteCustomEmail(currentOrcid, type);
        return false;
    }
    
    @RequestMapping(value = "/get.json", method = RequestMethod.GET)
    public @ResponseBody
    List<CustomEmailForm> getCustomEmails(HttpServletRequest request){
        String currentOrcid = getEffectiveUserOrcid();
        List<CustomEmailForm> result = new ArrayList<CustomEmailForm>();
        if(clientDetailsManager.exists(currentOrcid)) {
            List<CustomEmailEntity> customEmails = customEmailManager.getCustomEmails(currentOrcid);
            for(CustomEmailEntity entity : customEmails) {
                CustomEmailForm form = CustomEmailForm.valueOf(entity);
                result.add(form);
            }
        }
        return result;
    }
    
    /******
     * Validators 
     * ****/
        
    @RequestMapping(value = "/validate-email-type.json", method = RequestMethod.POST)
    public @ResponseBody
    CustomEmailForm validateEmailType(@RequestBody CustomEmailForm customEmailForm){
        customEmailForm.getEmailType().setErrors(new ArrayList<String>());
        if(PojoUtil.isEmpty(customEmailForm.getEmailType()))
                customEmailForm.getEmailType().getErrors().add(getMessage("custom_email.email_type.not_blank"));
        else {
            try {
                EmailType.valueOf(customEmailForm.getEmailType().getValue());
            } catch(IllegalArgumentException ie) {
                customEmailForm.getEmailType().getErrors().add(getMessage("custom_email.email_type.invalid"));
            }
        } 
        return customEmailForm;
    }
    
    @RequestMapping(value = "/validate-sender.json", method = RequestMethod.POST)
    public @ResponseBody
    CustomEmailForm validateSender(@RequestBody CustomEmailForm customEmailForm) {
        customEmailForm.getSender().setErrors(new ArrayList<String>());
        if(!PojoUtil.isEmpty(customEmailForm.getSender())) {
            try {                
                String sender = customEmailForm.getSender().getValue();
                InternetAddress addr = new InternetAddress(sender);
                addr.validate();
            } catch (AddressException ex) {
                customEmailForm.getSender().getErrors().add(getMessage("custom_email.sender.invalid"));
            }
        }        
        return customEmailForm;
    }
    
    @RequestMapping(value = "/validate-subject.json", method = RequestMethod.POST)
    public @ResponseBody
    CustomEmailForm validateSubject(@RequestBody CustomEmailForm customEmailForm) {
        customEmailForm.getSubject().setErrors(new ArrayList<String>());
        if(!PojoUtil.isEmpty(customEmailForm.getSubject())){
            if(customEmailForm.getSubject().getValue().length() > SUBJECT_MAX_LENGTH)
                customEmailForm.getSubject().getErrors().add(getMessage("custom_email.subject.too_long"));
        }
                
        return customEmailForm;
    }
    
    @RequestMapping(value = "/validate-content.json", method = RequestMethod.POST)
    public @ResponseBody
    CustomEmailForm validateContent(@RequestBody CustomEmailForm customEmailForm) {
        customEmailForm.getContent().setErrors(new ArrayList<String>());
        if(PojoUtil.isEmpty(customEmailForm.getContent())){
            customEmailForm.getContent().getErrors().add(getMessage("custom_email.content.not_blank"));
        } 
        return customEmailForm;
    }
}
