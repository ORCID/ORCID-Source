package org.orcid.frontend.web.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.orcid.core.constants.EmailConstants;
import org.orcid.core.manager.v3.ClientDetailsManager;
import org.orcid.core.manager.CustomEmailManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.jaxb.model.clientgroup.MemberType;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Angel Montenegro
 */

@Controller
@RequestMapping(value = { "/group/custom-emails" })
public class CustomEmailController extends BaseController {

    private static final String DEFAULT_CLAIM_SENDER = "claim@notify.orcid.org";
    private static final int SUBJECT_MAX_LENGTH = 255;
        
    @Resource
    CustomEmailManager customEmailManager;
    
    @Resource(name = "clientDetailsManagerV3")
    ClientDetailsManager clientDetailsManager;
    
    @Resource(name = "profileEntityManagerV3")
    ProfileEntityManager profileEntityManager;
    
    @RequestMapping        
    public ModelAndView manageDeveloperTools(@RequestParam("clientId") String clientId) {
        ModelAndView mav = new ModelAndView("custom_emails");
        boolean haveErrors = false;        
        String groupId = getEffectiveUserOrcid();        
        MemberType groupType = profileEntityManager.getGroupType(groupId);        
        if(!(MemberType.PREMIUM_INSTITUTION.equals(groupType) || MemberType.BASIC_INSTITUTION.equals(groupType))) {
            haveErrors = true;
            mav.addObject("invalid_request", getMessage("manage.developer_tools.group.custom_emails.invalid_group_type"));
        } else if(!clientDetailsManager.exists(clientId)) {
            haveErrors = true;
            mav.addObject("invalid_request", getMessage("manage.developer_tools.group.custom_emails.invalid_client_id"));
        } else if(!clientDetailsManager.belongsTo(clientId, groupId)) {
            haveErrors = true;
            mav.addObject("invalid_request", getMessage("manage.developer_tools.group.custom_emails.not_your_client"));
        }
        
        if(!haveErrors) {
            mav.addObject("client_id", clientId);
        }
                
        return mav;
    }
    
    @RequestMapping(value = "/get-empty.json", method = RequestMethod.GET)
    public @ResponseBody
    CustomEmailForm getEmptyCustomEmailForm(@RequestParam("clientId") String clientId) {
        String groupId = getEffectiveUserOrcid();        
        if(PojoUtil.isEmpty(clientId) || !clientDetailsManager.exists(clientId)) {
            throw new IllegalArgumentException(getMessage("manage.developer_tools.group.custom_emails.invalid_client_id"));
        } else if(!clientDetailsManager.belongsTo(clientId, groupId)) {
            throw new IllegalArgumentException(getMessage("manage.developer_tools.group.custom_emails.not_your_client"));
        }
        
        CustomEmailForm result = new CustomEmailForm();
        result.setSubject(Text.valueOf(""));
        result.setContent(Text.valueOf(""));
        result.setSender(Text.valueOf(""));
        result.setHtml(true);
        result.setEmailType(Text.valueOf(EmailType.CLAIM.name()));
        result.setClientId(clientId);
        return result;
    }        
    
    @RequestMapping(value = "/get.json", method = RequestMethod.GET)
    public @ResponseBody
    List<CustomEmailForm> getCustomEmails(@RequestParam("clientId") String clientId) throws IllegalArgumentException {        
        List<CustomEmailForm> result = new ArrayList<CustomEmailForm>();
        boolean haveErrors = false;
        String groupId = getEffectiveUserOrcid();        
        MemberType groupType = profileEntityManager.getGroupType(groupId);        
        if(!(MemberType.PREMIUM_INSTITUTION.equals(groupType) || MemberType.BASIC_INSTITUTION.equals(groupType))) {
            haveErrors = true;           
        } else if(!clientDetailsManager.exists(clientId)) {
            haveErrors = true;
        } else if(!clientDetailsManager.belongsTo(clientId, groupId)) {
            haveErrors = true;
        }                        
        
        if(!haveErrors) {
            List<CustomEmailEntity> customEmails = customEmailManager.getCustomEmails(clientId);
            for(CustomEmailEntity entity : customEmails) {
                CustomEmailForm form = CustomEmailForm.valueOf(entity);
                result.add(form);
            }
        }                
        return result;
    }
                            
    @RequestMapping(value = "/create.json", method = RequestMethod.POST)
    public @ResponseBody
    CustomEmailForm createCustomEmailForm(@RequestBody CustomEmailForm customEmailForm) {
        String groupId = getEffectiveUserOrcid();
        String clientId = customEmailForm.getClientId();
        
        if(clientDetailsManager.belongsTo(clientId, groupId)) {
            customEmailForm.setErrors(new ArrayList<String>());
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
                
                customEmailManager.createCustomEmail(clientId, emailType, sender, subject, content, isHtml);
            }                        
            
        }                 
        return customEmailForm;
    }
    
    @RequestMapping(value = "/update.json", method = RequestMethod.POST)
    public @ResponseBody
    CustomEmailForm updateCustomEmailForm(@RequestBody CustomEmailForm customEmailForm) {
        String groupId = getEffectiveUserOrcid();
        String clientId = customEmailForm.getClientId();                
        
        if(clientDetailsManager.belongsTo(clientId, groupId)) {
            customEmailForm.setErrors(new ArrayList<String>());
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
                
                customEmailManager.updateCustomEmail(clientId, emailType, sender, subject, content, isHtml);
            }
        }
        return customEmailForm;
    }
    
    @RequestMapping(value = "/delete.json", method = RequestMethod.POST)
    public @ResponseBody
    boolean deleteCustomEmailForm(@RequestBody CustomEmailForm customEmailForm) {
        String groupId = getEffectiveUserOrcid();
        String clientId = customEmailForm.getClientId();
        
        EmailType type = null;
        if(!PojoUtil.isEmpty(customEmailForm.getEmailType())) {
            type = EmailType.valueOf(customEmailForm.getEmailType().getValue());
        }
        if(type != null && clientDetailsManager.belongsTo(clientId, groupId))
            return customEmailManager.deleteCustomEmail(clientId, type);
        return false;
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
            else if(OrcidStringUtils.hasHtml(customEmailForm.getSubject().getValue())){
                customEmailForm.getSubject().getErrors().add(getMessage("custom_email.subject.html"));
            }
        }
                
        return customEmailForm;
    }
    
    @RequestMapping(value = "/validate-content.json", method = RequestMethod.POST)
    public @ResponseBody
    CustomEmailForm validateContent(@RequestBody CustomEmailForm customEmailForm) {
        customEmailForm.getContent().setErrors(new ArrayList<String>());
        if(PojoUtil.isEmpty(customEmailForm.getContent())){
            customEmailForm.getContent().getErrors().add(getMessage("custom_email.content.not_blank"));
        } else {
            String content = customEmailForm.getContent().getValue();
            if(!content.contains(EmailConstants.WILDCARD_VERIFICATION_URL)) {
                customEmailForm.getContent().getErrors().add(getMessage("custom_email.content.verification_url_required"));
            } else if(!customEmailForm.isHtml()){
                if(OrcidStringUtils.hasHtml(content)) {            
                    customEmailForm.getContent().getErrors().add(getMessage("custom_email.content.html"));
                }
            }           
        }
        return customEmailForm;
    }
}
