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
package org.orcid.core;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.orcid.core.manager.ValidationBehaviour;
import org.orcid.core.manager.ValidationManager;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * 
 * @author Will Simpson
 * 
 */
public class TestXmlValidity extends BaseTest {

    @Autowired
    private ValidationManager validationManager;
    private Unmarshaller unmarshaller;

    private static final Logger LOG = LoggerFactory.getLogger(TestXmlValidity.class);

    @Before
    public void before() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(OrcidMessage.class);
        unmarshaller = context.createUnmarshaller();
        validationManager.setValidationBehaviour(ValidationBehaviour.THROW_RUNTIME_EXCEPTION);
    }

    @Test
    public void testAllOrcidMessages() throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath*:**/*message*.xml");
        for (Resource resource : resources) {
            LOG.info("Found resource: {}", resource);
            InputStream is = null;
            try {
                is = resource.getInputStream();
                validationManager.validateMessage((OrcidMessage) unmarshaller.unmarshal(is));
            } catch (IOException e) {
                Assert.fail("Unable to read resource: " + resource + "\n" + e);
            } catch (JAXBException e) {
                Assert.fail("ORCID message is not well formed: " + resource + "\n" + e);
            } catch (RuntimeException e) {
                Assert.fail("Validation failed: " + resource + "\n" + e.getCause());
            } finally {
                IOUtils.closeQuietly(is);
            }
        }
    }

}
