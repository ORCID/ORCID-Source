/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.manager;

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

}
