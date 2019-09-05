package org.orcid.frontend.web.util;

import javax.annotation.Resource;

import org.apache.commons.lang.RandomStringUtils;
import org.orcid.core.manager.PasswordGenerationManager;
import org.orcid.core.manager.impl.PasswordGenerationManagerImpl;
import org.orcid.password.constants.OrcidPasswordConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UtilEndpoints {
    private static int RANDOM_STRING_LENGTH = 15;
    
    @Resource
    private PasswordGenerationManager passwordGenerationManager;

    /**
     * Generate random string
     */
    @RequestMapping(value = "/generate-random-string.json", method = RequestMethod.GET)
    public @ResponseBody String generateRandomString() {
    	char[] newPassword = passwordGenerationManager.createNewPassword();
        return new String(newPassword);
    }
}
