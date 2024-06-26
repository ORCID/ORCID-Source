package org.orcid.frontend.web.controllers;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.codec.binary.Base64;
import org.orcid.core.common.manager.EmailFrequencyManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.v3.EmailManager;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.jaxb.model.v3.release.record.Email;
import org.orcid.persistence.constants.SendEmailFrequency;
import org.orcid.pojo.UnsubscribeData;
import org.orcid.utils.alerting.SlackManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller("unsubscribeController")
@RequestMapping(value = { "/unsubscribe" })
public class UnsubscribeController extends BaseController {

    @Resource
    private EmailFrequencyManager emailFrequencyManager;
    
    @Resource
    private EncryptionManager encryptionManager;
    
    @Resource(name = "emailManagerReadOnlyV3")
    private EmailManagerReadOnly emailManagerReadOnly;
    
    @Resource(name = "emailManagerV3")
    private EmailManager emailManager;
    
    @Resource
    private SlackManager slackManager;
    
    @RequestMapping(value="/{encryptedId}", method = RequestMethod.GET)
    public ModelAndView unsubscribeView(@PathVariable("encryptedId") String encryptedId) throws UnsupportedEncodingException {
        ModelAndView result = new ModelAndView("unsubscribe");
        result.addObject("noIndex", true);
        
        String decryptedId = encryptionManager.decryptForExternalUse(new String(Base64.decodeBase64(encryptedId), "UTF-8"));
        String orcid = emailFrequencyManager.findOrcidId(decryptedId);
        
        // Disable quarterly notifications
        emailFrequencyManager.updateSendQuarterlyTips(orcid, false);
        
        // Verify primary email address if it is not verified already        
        if(!emailManagerReadOnly.isPrimaryEmailVerified(orcid)) {
            try {
                emailManager.verifyPrimaryEmail(orcid);
            } catch(javax.persistence.NoResultException nre) {
                slackManager.sendSystemAlert(String.format("User with orcid %s have no primary email, so, we are setting the newest verified email, or, the newest email in case non is verified as the primary one", orcid));
            } catch(javax.persistence.NonUniqueResultException nure) {
                slackManager.sendSystemAlert(String.format("User with orcid %s have more than one primary email, so, we are setting the latest modified primary as the primary one", orcid));
            } 
        }
        return result;
    }
    
    @RequestMapping(value = "/unsubscribeData.json", method = RequestMethod.GET)
    public @ResponseBody UnsubscribeData getUnsubscribeData(@RequestParam(value = "id") String encryptedId) throws UnsupportedEncodingException {
        String decryptedId = encryptionManager.decryptForExternalUse(new String(Base64.decodeBase64(encryptedId), "UTF-8"));
        String orcid = emailFrequencyManager.findOrcidId(decryptedId);
        Email email = emailManagerReadOnly.findPrimaryEmail(orcid);

        UnsubscribeData unsubscribeData = new UnsubscribeData();
        unsubscribeData.setEmailAddress(email.getEmail());
        unsubscribeData.setEmailFrequencyOptions(emailManagerReadOnly.getEmailFrequencyOptions());
        return unsubscribeData;
    }
    
    @RequestMapping(value = "/preferences.json", method = RequestMethod.GET)
    public @ResponseBody Map<String, String> getPreferences(@RequestParam(value = "id") String encryptedId) throws UnsupportedEncodingException {
        String decryptedId = encryptionManager.decryptForExternalUse(new String(Base64.decodeBase64(encryptedId), "UTF-8"));
        return emailFrequencyManager.getEmailFrequencyById(decryptedId);
    }
    
    @RequestMapping(value = "/preferences.json", method = RequestMethod.POST)
    public @ResponseBody Map<String, String> updatePreferences(@RequestParam(value = "id") String encryptedId, @RequestBody Map<String, String> params) throws UnsupportedEncodingException {
        String decryptedId = encryptionManager.decryptForExternalUse(new String(Base64.decodeBase64(encryptedId), "UTF-8"));
        
        SendEmailFrequency administrativeChangeNotifications = SendEmailFrequency.fromValue(params.get(EmailFrequencyManager.ADMINISTRATIVE_CHANGE_NOTIFICATIONS));
        SendEmailFrequency changeNotifications = SendEmailFrequency.fromValue(params.get(EmailFrequencyManager.CHANGE_NOTIFICATIONS));
        SendEmailFrequency memberUpdateRequests = SendEmailFrequency.fromValue(params.get(EmailFrequencyManager.MEMBER_UPDATE_REQUESTS));
        Boolean quarterlyTips = Boolean.valueOf(params.get(EmailFrequencyManager.QUARTERLY_TIPS));
        
        emailFrequencyManager.updateById(decryptedId, changeNotifications, administrativeChangeNotifications, memberUpdateRequests, quarterlyTips);
        
        Map<String, String> result = new HashMap<String, String>();
        result.put("redirect_uri", orcidUrlManager.getBaseUrl() + "/signin");
        
        return result;
    }
}
