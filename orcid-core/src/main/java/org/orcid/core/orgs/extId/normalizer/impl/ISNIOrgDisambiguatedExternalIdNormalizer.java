package org.orcid.core.orgs.extId.normalizer.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.orcid.core.orgs.extId.normalizer.OrgDisambiguatedExternalIdNormalizer;
import org.springframework.stereotype.Component;

@Component
public class ISNIOrgDisambiguatedExternalIdNormalizer implements OrgDisambiguatedExternalIdNormalizer {
    
    private static final Pattern PATTERN = Pattern.compile("\\d{15}[\\dX]{1}");

    @Override
    public String getType() {
        return "ISNI";
    }

    @Override
    public String normalize(String value) {
        // keep original in case we can't normalise it 
        String original = value;
        if (value == null) {
            return value;
        }
        value = stripDelimiters(value);
        value = stripIllegalCharacters(value);
        value = normaliseX(value);
        value = normaliseLength(value);
        String match = findMatch(value);
        return match != null ? match : original;
    }
    
    private String stripIllegalCharacters(String value) {
        return value.replaceAll("[^\\dXx]", "");
    }

    private String normaliseLength(String value) {
        // prefix value with 0s if too short
        while (value.length() < 16) {
            value = "0" + value;
        }
        
        // remove leading 0s if value too long
        while (value.length() > 16 && value.startsWith("0")) {
            value = value.substring(1);
        }
        return value;
    }

    private String normaliseX(String value) {
        return value.replaceAll("x", "X");
    }

    private String stripDelimiters(String value) {
        // strip white space, hyphens
        value = value.replaceAll("\\s", "");
        value = value.replaceAll("-", "");
        return value;
    }
    
    private String findMatch(String value) {
        Matcher matcher = PATTERN.matcher(value);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

}
