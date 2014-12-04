/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
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
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.TemplateManager;
import org.springframework.beans.factory.InitializingBean;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class TemplateManagerImpl implements TemplateManager, InitializingBean {

    private Configuration freeMarkerConfiguration;

    @Resource
    private LocaleManager localeManager;

    @Override
    public String processTemplate(String templateName, Map<String, Object> params) {
        return processTemplate(templateName, params, localeManager.getLocale());
    }

    @Override
    public String processTemplate(String templateName, Map<String, Object> params, Locale locale) {
        try {
            Template template = freeMarkerConfiguration.getTemplate(templateName, locale);
            StringWriter result = new StringWriter();
            template.process(params, result);
            return result.toString();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        } catch (TemplateException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void afterPropertiesSet() throws IOException {
        freeMarkerConfiguration = new Configuration();
        freeMarkerConfiguration.setClassForTemplateLoading(this.getClass(), "/org/orcid/core/template");
    }

}
