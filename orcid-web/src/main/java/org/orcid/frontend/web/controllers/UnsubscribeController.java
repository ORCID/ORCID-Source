package org.orcid.frontend.web.controllers;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.codec.binary.Base64;
import org.orcid.core.common.manager.EmailFrequencyManager;
import org.orcid.core.manager.EncryptionManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller("unsubscribeController")
@RequestMapping(value = { "/unsubscribe" })
public class UnsubscribeController extends BaseController {

    @Resource
    private EmailFrequencyManager emailFrequencyManager;
    
    @Resource
    private EncryptionManager encryptionManager;
    
    @RequestMapping(value="/{encryptedId}", method = RequestMethod.GET)
    public ModelAndView unsubscribeView(@PathVariable("encryptedId") String encryptedId) {
        ModelAndView result = new ModelAndView("unsubscribe");
        result.addObject("noIndex", true);
        return result;
    }
    
    @RequestMapping(value = "/preferences.json", method = RequestMethod.GET)
    public Map<String, String> getPreferences(@RequestParam(value = "encryptedId") String encryptedId) throws UnsupportedEncodingException {
        String decryptedId = encryptionManager.decryptForExternalUse(new String(Base64.decodeBase64(encryptedId), "UTF-8"));
        return emailFrequencyManager.getEmailFrequencyById(decryptedId);
    }
}
