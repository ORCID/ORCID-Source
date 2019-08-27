package org.orcid.core.groupIds.issn;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IssnGroupIdPatternMatcher {

    // deliberately loose to recognise anything that claims to be an issn record
    private static Pattern issnGroupTypePattern = Pattern.compile("^issn:(.*)$");

    public static boolean isIssnGroupType(String groupId) {
        Matcher matcher = issnGroupTypePattern.matcher(groupId);
        return matcher.find();
    }

    public static String getIssnFromIssnGroupId(String groupId) {
        Matcher matcher = issnGroupTypePattern.matcher(groupId);
        if (!matcher.find()) {
            throw new NotAnIssnGroupIdException();
        }
        return matcher.group(1);
    }
    
}
