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
package org.orcid.core.adapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.orcid.core.adapter.impl.jsonidentifiers.FundingExternalIdentifier;
import org.orcid.core.adapter.impl.jsonidentifiers.FundingExternalIdentifiers;
import org.orcid.jaxb.model.message.FundingExternalIdentifierType;
import org.orcid.jaxb.model.record_v2.ExternalID;
import org.orcid.jaxb.model.record_v2.ExternalIDs;

public class FundingExternalIdentifiersConversionsTest {

    //TODO: write same test for WorkExternalIdentifiers
    //TODO: refactor out separate pojos for the different identifiers (requires DB work?)
    @Test
    public void messageToCoreObjectTest() {
        org.orcid.jaxb.model.message.FundingExternalIdentifiers recordFei = new org.orcid.jaxb.model.message.FundingExternalIdentifiers();
        org.orcid.jaxb.model.message.FundingExternalIdentifier f1 = new org.orcid.jaxb.model.message.FundingExternalIdentifier();
        f1.setType(org.orcid.jaxb.model.message.FundingExternalIdentifierType.GRANT_NUMBER);
        f1.setUrl(new org.orcid.jaxb.model.message.Url("www.f1.com"));
        f1.setValue("f1");
        recordFei.getFundingExternalIdentifier().add(f1);

        org.orcid.jaxb.model.message.FundingExternalIdentifier f2 = new org.orcid.jaxb.model.message.FundingExternalIdentifier();
        f2.setType(org.orcid.jaxb.model.message.FundingExternalIdentifierType.GRANT_NUMBER);
        f2.setUrl(new org.orcid.jaxb.model.message.Url("www.f2.com"));
        f2.setValue("f2");
        recordFei.getFundingExternalIdentifier().add(f2);

        org.orcid.jaxb.model.message.FundingExternalIdentifier f3 = new org.orcid.jaxb.model.message.FundingExternalIdentifier();
        f3.setType(org.orcid.jaxb.model.message.FundingExternalIdentifierType.GRANT_NUMBER);
        f3.setUrl(new org.orcid.jaxb.model.message.Url("www.f3.com"));
        f3.setValue("f3");
        recordFei.getFundingExternalIdentifier().add(f3);

        FundingExternalIdentifiers fei = new FundingExternalIdentifiers(recordFei);
        assertNotNull(fei);
        assertEquals(3, fei.getFundingExternalIdentifier().size());

        boolean found1 = false, found2 = false, found3 = false;
        for (FundingExternalIdentifier f : fei.getFundingExternalIdentifier()) {
            if (f.getValue().equals("f1")) {
                found1 = true;
                assertEquals("www.f1.com", f.getUrl().getValue());
                assertEquals(FundingExternalIdentifierType.GRANT_NUMBER.value().toUpperCase(), f.getType());
            } else if (f.getValue().equals("f2")) {
                found2 = true;
                assertEquals("www.f2.com", f.getUrl().getValue());
                assertEquals(FundingExternalIdentifierType.GRANT_NUMBER.value().toUpperCase(), f.getType());
            } else if (f.getValue().equals("f3")) {
                found3 = true;
                assertEquals("www.f3.com", f.getUrl().getValue());
                assertEquals(FundingExternalIdentifierType.GRANT_NUMBER.value().toUpperCase(), f.getType());
            } else {
                fail();
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
    }

    @Test
    public void recordToCoreObjectTest() {
        ExternalIDs recordFei = new ExternalIDs();
        ExternalID f1 = new ExternalID();
        f1.setType(org.orcid.jaxb.model.message.FundingExternalIdentifierType.GRANT_NUMBER.value());
        f1.setUrl(new org.orcid.jaxb.model.common_v2.Url("www.f1.com"));
        f1.setValue("f1");
        recordFei.getExternalIdentifier().add(f1);

        ExternalID f2 = new ExternalID();
        f2.setType(org.orcid.jaxb.model.message.FundingExternalIdentifierType.GRANT_NUMBER.value());
        f2.setUrl(new org.orcid.jaxb.model.common_v2.Url("www.f2.com"));
        f2.setValue("f2");
        recordFei.getExternalIdentifier().add(f2);

        ExternalID f3 = new ExternalID();
        f3.setType(org.orcid.jaxb.model.message.FundingExternalIdentifierType.GRANT_NUMBER.value());
        f3.setUrl(new org.orcid.jaxb.model.common_v2.Url("www.f3.com"));
        f3.setValue("f3");
        recordFei.getExternalIdentifier().add(f3);

        FundingExternalIdentifiers fei = new FundingExternalIdentifiers(recordFei);
        assertNotNull(fei);
        assertEquals(3, fei.getFundingExternalIdentifier().size());

        boolean found1 = false, found2 = false, found3 = false;
        for (FundingExternalIdentifier f : fei.getFundingExternalIdentifier()) {
            if (f.getValue().equals("f1")) {
                found1 = true;
                assertEquals("www.f1.com", f.getUrl().getValue());
                assertEquals(FundingExternalIdentifierType.GRANT_NUMBER.value().toUpperCase(), f.getType());
            } else if (f.getValue().equals("f2")) {
                found2 = true;
                assertEquals("www.f2.com", f.getUrl().getValue());
                assertEquals(FundingExternalIdentifierType.GRANT_NUMBER.value().toUpperCase(), f.getType());
            } else if (f.getValue().equals("f3")) {
                found3 = true;
                assertEquals("www.f3.com", f.getUrl().getValue());
                assertEquals(FundingExternalIdentifierType.GRANT_NUMBER.value().toUpperCase(), f.getType());
            } else {
                fail();
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
    }

    @Test
    public void coreObjectToMessageTest() {
        FundingExternalIdentifiers fei = getFundingExternalIdentifiers();
        org.orcid.jaxb.model.message.FundingExternalIdentifiers messageObject = fei.toMessagePojo();
        assertNotNull(messageObject);
        assertEquals(3, messageObject.getFundingExternalIdentifier().size());

        boolean found1 = false, found2 = false, found3 = false;
        for (org.orcid.jaxb.model.message.FundingExternalIdentifier f : messageObject.getFundingExternalIdentifier()) {
            if (f.getValue().equals("f1")) {
                found1 = true;
                assertEquals("www.f1.com", f.getUrl().getValue());
                assertEquals(FundingExternalIdentifierType.GRANT_NUMBER, f.getType());
            } else if (f.getValue().equals("f2")) {
                found2 = true;
                assertEquals("www.f2.com", f.getUrl().getValue());
                assertEquals(FundingExternalIdentifierType.GRANT_NUMBER, f.getType());
            } else if (f.getValue().equals("f3")) {
                found3 = true;
                assertEquals("www.f3.com", f.getUrl().getValue());
                assertEquals(FundingExternalIdentifierType.GRANT_NUMBER, f.getType());
            } else {
                fail();
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
    }

    @Test
    public void coreObjectToRecordTest() {
        FundingExternalIdentifiers fei = getFundingExternalIdentifiers();
        ExternalIDs messageObject = fei.toRecordPojo();
        assertNotNull(messageObject);
        assertEquals(3, messageObject.getExternalIdentifier().size());

        boolean found1 = false, found2 = false, found3 = false;
        for (ExternalID f : messageObject.getExternalIdentifier()) {
            if (f.getValue().equals("f1")) {
                found1 = true;
                assertEquals("www.f1.com", f.getUrl().getValue());
                assertEquals(org.orcid.jaxb.model.message.FundingExternalIdentifierType.GRANT_NUMBER.value(), f.getType());
            } else if (f.getValue().equals("f2")) {
                found2 = true;
                assertEquals("www.f2.com", f.getUrl().getValue());
                assertEquals(org.orcid.jaxb.model.message.FundingExternalIdentifierType.GRANT_NUMBER.value(), f.getType());
            } else if (f.getValue().equals("f3")) {
                found3 = true;
                assertEquals("www.f3.com", f.getUrl().getValue());
                assertEquals(org.orcid.jaxb.model.message.FundingExternalIdentifierType.GRANT_NUMBER.value(), f.getType());
            } else {
                fail();
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
    }

    private FundingExternalIdentifiers getFundingExternalIdentifiers() {
        FundingExternalIdentifiers fei = new FundingExternalIdentifiers();
        FundingExternalIdentifier f1 = new FundingExternalIdentifier(FundingExternalIdentifierType.GRANT_NUMBER, "www.f1.com","f1");
        fei.getFundingExternalIdentifier().add(f1);

        FundingExternalIdentifier f2 = new FundingExternalIdentifier(FundingExternalIdentifierType.GRANT_NUMBER, "www.f2.com","f2");
        fei.getFundingExternalIdentifier().add(f2);

        FundingExternalIdentifier f3 = new FundingExternalIdentifier(FundingExternalIdentifierType.GRANT_NUMBER, "www.f3.com","f3");
        fei.getFundingExternalIdentifier().add(f3);
        return fei;
    }

}
