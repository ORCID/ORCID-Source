package org.orcid.core.manager;

import java.util.Locale;
import java.util.Map;

public interface TemplateManager {

    /**
     * Retrieves the template from the templates package, and creates string
     * with template params filled in.
     * 
     * @param templateName
     *            The name of the template file in the templates package (do not
     *            include package path).
     * @param params
     *            The params to be filled in.
     * @return A string with the template text filled in with the param values.
     */
    String processTemplate(String templateName, Map<String, Object> params);
    
    String processTemplate(String templateName, Map<String, Object> params, Locale locale);

}
