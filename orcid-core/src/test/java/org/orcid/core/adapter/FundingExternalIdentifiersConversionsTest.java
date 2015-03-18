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
import org.orcid.jaxb.model.message.FundingExternalIdentifierType;
import org.orcid.pojo.FundingExternalIdentifier;
import org.orcid.pojo.FundingExternalIdentifiers;

public class FundingExternalIdentifiersConversionsTest {

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

        FundingExternalIdentifiers fei = FundingExternalIdentifiers.fromMessagePojo(recordFei);
        assertNotNull(fei);
        assertEquals(3, fei.getFundingExternalIdentifier().size());

        boolean found1 = false, found2 = false, found3 = false;
        for (FundingExternalIdentifier f : fei.getFundingExternalIdentifier()) {
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
    public void recordToCoreObjectTest() {
        org.orcid.jaxb.model.record.FundingExternalIdentifiers recordFei = new org.orcid.jaxb.model.record.FundingExternalIdentifiers();
        org.orcid.jaxb.model.record.FundingExternalIdentifier f1 = new org.orcid.jaxb.model.record.FundingExternalIdentifier();
        f1.setType(org.orcid.jaxb.model.record.FundingExternalIdentifierType.GRANT_NUMBER);
        f1.setUrl(new org.orcid.jaxb.model.common.Url("www.f1.com"));
        f1.setValue("f1");
        recordFei.getExternalIdentifier().add(f1);

        org.orcid.jaxb.model.record.FundingExternalIdentifier f2 = new org.orcid.jaxb.model.record.FundingExternalIdentifier();
        f2.setType(org.orcid.jaxb.model.record.FundingExternalIdentifierType.GRANT_NUMBER);
        f2.setUrl(new org.orcid.jaxb.model.common.Url("www.f2.com"));
        f2.setValue("f2");
        recordFei.getExternalIdentifier().add(f2);

        org.orcid.jaxb.model.record.FundingExternalIdentifier f3 = new org.orcid.jaxb.model.record.FundingExternalIdentifier();
        f3.setType(org.orcid.jaxb.model.record.FundingExternalIdentifierType.GRANT_NUMBER);
        f3.setUrl(new org.orcid.jaxb.model.common.Url("www.f3.com"));
        f3.setValue("f3");
        recordFei.getExternalIdentifier().add(f3);

        FundingExternalIdentifiers fei = FundingExternalIdentifiers.fromRecordPojo(recordFei);
        assertNotNull(fei);
        assertEquals(3, fei.getFundingExternalIdentifier().size());

        boolean found1 = false, found2 = false, found3 = false;
        for (FundingExternalIdentifier f : fei.getFundingExternalIdentifier()) {
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
        org.orcid.jaxb.model.record.FundingExternalIdentifiers messageObject = fei.toRecordPojo();
        assertNotNull(messageObject);
        assertEquals(3, messageObject.getExternalIdentifier().size());

        boolean found1 = false, found2 = false, found3 = false;
        for (org.orcid.jaxb.model.record.FundingExternalIdentifier f : messageObject.getExternalIdentifier()) {
            if (f.getValue().equals("f1")) {
                found1 = true;
                assertEquals("www.f1.com", f.getUrl().getValue());
                assertEquals(FundingExternalIdentifierType.GRANT_NUMBER.value(), f.getType().value());
            } else if (f.getValue().equals("f2")) {
                found2 = true;
                assertEquals("www.f2.com", f.getUrl().getValue());
                assertEquals(FundingExternalIdentifierType.GRANT_NUMBER.value(), f.getType().value());
            } else if (f.getValue().equals("f3")) {
                found3 = true;
                assertEquals("www.f3.com", f.getUrl().getValue());
                assertEquals(FundingExternalIdentifierType.GRANT_NUMBER.value(), f.getType().value());
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
        FundingExternalIdentifier f1 = new FundingExternalIdentifier();
        f1.setType(FundingExternalIdentifierType.GRANT_NUMBER);
        f1.setUrl(new org.orcid.jaxb.model.message.Url("www.f1.com"));
        f1.setValue("f1");
        fei.getFundingExternalIdentifier().add(f1);

        FundingExternalIdentifier f2 = new FundingExternalIdentifier();
        f2.setType(FundingExternalIdentifierType.GRANT_NUMBER);
        f2.setUrl(new org.orcid.jaxb.model.message.Url("www.f2.com"));
        f2.setValue("f2");
        fei.getFundingExternalIdentifier().add(f2);

        FundingExternalIdentifier f3 = new FundingExternalIdentifier();
        f3.setType(FundingExternalIdentifierType.GRANT_NUMBER);
        f3.setUrl(new org.orcid.jaxb.model.message.Url("www.f3.com"));
        f3.setValue("f3");
        fei.getFundingExternalIdentifier().add(f3);
        return fei;
    }

}
