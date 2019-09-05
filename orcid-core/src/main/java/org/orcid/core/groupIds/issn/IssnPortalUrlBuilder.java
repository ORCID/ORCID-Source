package org.orcid.core.groupIds.issn;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class IssnPortalUrlBuilder {

    @Value("${org.orcid.core.issn.portal.url.json:https://portal.issn.org/resource/ISSN/%s?format=json}")
    private String jsonUrl;
    
    @Value("${org.orcid.core.issn.portal.url:https://portal.issn.org/resource/ISSN/%s}")
    private String normalUrl;
    
    public String buildJsonIssnPortalUrlForIssn(String issn) {
        return String.format(jsonUrl, issn);
    }
    
    public String buildIssnPortalUrlForIssn(String issn) {
        return String.format(normalUrl, issn);
    }
    
}
