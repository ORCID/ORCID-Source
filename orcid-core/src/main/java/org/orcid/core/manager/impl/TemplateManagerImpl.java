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
package org.orcid.core.manager.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import org.orcid.core.manager.TemplateManager;
import org.springframework.beans.factory.InitializingBean;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class TemplateManagerImpl implements TemplateManager, InitializingBean {

    private Configuration freeMarkerConfiguration;

    @Override
    public String processTemplate(String templateName, Map<String, Object> params) {
        try {
            Template template = freeMarkerConfiguration.getTemplate(templateName);
            StringWriter result = new StringWriter();
            template.process(params, result);
            return result.toString();
        } catch (IOException e) {
            // XXX
            throw new IllegalArgumentException(e);
        } catch (TemplateException e) {
            // XXX
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void afterPropertiesSet() throws IOException {
        freeMarkerConfiguration = new Configuration();
        freeMarkerConfiguration.setClassForTemplateLoading(this.getClass(), "/org/orcid/core/template");
    }

}
