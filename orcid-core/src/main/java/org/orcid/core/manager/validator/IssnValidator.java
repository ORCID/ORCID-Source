package org.orcid.core.manager.validator;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

@Component
public class IssnValidator {
    
    private static final Pattern pattern = Pattern.compile("(?:^|[^\\d])(\\d{4}\\ {0,1}[-–]\\ {0,1}\\d{3}[\\dXx])(?:$|[^-\\d])");
    
    public boolean issnValid(String issn) {
        return pattern.matcher(issn).find();
    }

}
