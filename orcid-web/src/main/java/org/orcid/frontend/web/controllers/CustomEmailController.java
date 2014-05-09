package org.orcid.frontend.web.controllers;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.core.manager.CustomEmailManager;
import org.orcid.core.manager.LoadOptions;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.persistence.jpa.entities.EmailType;
import org.orcid.pojo.ajaxForm.CustomEmailForm;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Text;
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
        result.setEmailType(Text.valueOf(EmailType.CLAIM.name()));
        return result;
    }
    
    @RequestMapping(value = "/create.json", method = RequestMethod.POST)
    public @ResponseBody
    CustomEmailForm createCustomEmailForm(HttpServletRequest request, @RequestBody CustomEmailForm customEmailForm) {
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
                
                customEmailManager.createCustomEmail(currentOrcid, emailType, sender, subject, content);
            }                        
            
        }                 
        return customEmailForm;
    }
    
    @RequestMapping(value = "/validate-email-type.json", method = RequestMethod.POST)
    public @ResponseBody
    CustomEmailForm validateEmailType(@RequestBody CustomEmailForm customEmailForm){
        return customEmailForm;
    }
    
    @RequestMapping(value = "/validate-sender.json", method = RequestMethod.POST)
    public @ResponseBody
    CustomEmailForm validateSender(@RequestBody CustomEmailForm customEmailForm) {
        return customEmailForm;
    }
    
    @RequestMapping(value = "/validate-subject.json", method = RequestMethod.POST)
    public @ResponseBody
    CustomEmailForm validateSubject(@RequestBody CustomEmailForm customEmailForm) {
        return customEmailForm;
    }
    
    @RequestMapping(value = "/validate-content.json", method = RequestMethod.POST)
    public @ResponseBody
    CustomEmailForm validateContent(@RequestBody CustomEmailForm customEmailForm) {
        return customEmailForm;
    }
}
