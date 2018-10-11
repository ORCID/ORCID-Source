package org.orcid.frontend.web.util;

import org.apache.commons.lang.RandomStringUtils;
import org.orcid.password.constants.OrcidPasswordConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UtilEndpoints {
    private static int RANDOM_STRING_LENGTH = 15;

    /**
     * Generate random string
     */
    @RequestMapping(value = "/generate-random-string.json", method = RequestMethod.GET)
    public @ResponseBody String generateRandomString() {
        return RandomStringUtils.random(RANDOM_STRING_LENGTH, OrcidPasswordConstants.getEntirePasswordCharsRange());
    }
}
