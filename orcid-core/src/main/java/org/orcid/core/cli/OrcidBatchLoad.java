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
package org.orcid.core.cli;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.Keyword;
import org.orcid.jaxb.model.message.Keywords;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Util class to load a given number of profiles into the DB, for diagnostic
 * purposes. Overrides any unique constraint fields with random data. This will
 * actually enter random records into the DB so **USE WITH CAUTION**
 * 
 * @author jamesb
 * 
 */
public class OrcidBatchLoad {

    private static final String ORCID_INTERNAL_FULL_XML = "/orcid-db-storage-message.xml";
    private static final Logger logger = Logger.getLogger(OrcidBatchLoad.class);

    private JAXBContext context;
    private Unmarshaller unmarshaller;
    private InputStream is;

    public OrcidBatchLoad() {
        try {
            context = JAXBContext.newInstance(OrcidMessage.class);
            unmarshaller = context.createUnmarshaller();
            is = OrcidBatchLoad.class.getResourceAsStream(ORCID_INTERNAL_FULL_XML);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {

        int numOrcidsRequired = args.length == 1 ? Integer.parseInt(args[0]) : 2;
        OrcidBatchLoad populateSampleProfileData = new OrcidBatchLoad();
        ApplicationContext context = new ClassPathXmlApplicationContext(new String[] { "orcid-core-context.xml" });
        OrcidProfileManager orcidProfileManager = (OrcidProfileManager) context.getBean("orcidProfileManager");

        Date startTime = new Date(System.currentTimeMillis());
        logger.info(MessageFormat.format("Starting creation of profiles at {0}", startTime));

        Date currTime = new Date(System.currentTimeMillis());
        logger.info(MessageFormat.format("Took {0} millis to unmarshall {1} orcids", new Object[] { currTime.getTime() - startTime.getTime(), numOrcidsRequired }));
        for (int i = 0; i < numOrcidsRequired; i++) {
            OrcidProfile orcidProfile = populateSampleProfileData.populateDefaultOrcidMessage();
            orcidProfileManager.createOrcidProfile(orcidProfile);
        }

        Date dbPersistTime = new Date(System.currentTimeMillis());
        logger.info(MessageFormat.format("Took {0} millis to persist {1} orcids", new Object[] { dbPersistTime.getTime() - currTime.getTime(), numOrcidsRequired }));
    }

    public OrcidProfile populateDefaultOrcidMessage() throws JAXBException {

        OrcidProfile profile = createTemplateOrcidProfile();
        assignPersistenceFields(profile);
        return profile;
    }

    private OrcidProfile createTemplateOrcidProfile() {

        try {
            logger.info(MessageFormat.format("Going to map stream at {0}", new Date()));
            return ((OrcidMessage) unmarshaller.unmarshal(is)).getOrcidProfile();
        }

        catch (Exception e) {
            IOUtils.closeQuietly(is);
            throw new RuntimeException(e);

        } finally {

        }

    }

    private void assignPersistenceFields(OrcidProfile profile) {
        // set the transient fields that the encrypters need
        profile.setPassword("password");
        // profile.setOrcidIdentifier();
        profile.setSecurityQuestionAnswer("securityQuestionAnswer");
        profile.setVerificationCode("1111");

        // randomise any fields that are uniquely constrained so that we can
        // re-use the test data
        profile.getOrcidBio().getContactDetails().addOrReplacePrimaryEmail(new Email(RandomStringUtils.randomAlphabetic(150)));

        Keywords keywords = new Keywords();
        keywords.getKeyword().add(new Keyword(RandomStringUtils.randomAlphabetic(255)));
        profile.getOrcidBio().setKeywords(keywords);

        profile.retrieveOrcidWorks().getOrcidWork();

    }

}
