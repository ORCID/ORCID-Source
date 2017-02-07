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

import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.manager.SalesForceManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.salesforce.dao.SalesForceDao;
import org.orcid.core.salesforce.model.Contact;
import org.orcid.core.salesforce.model.ContactRole;
import org.orcid.core.salesforce.model.ContactRoleType;
import org.orcid.persistence.dao.SalesForceConnectionDao;
import org.orcid.persistence.jpa.entities.SalesForceConnectionEntity;
import org.orcid.test.LambdaMatcher;
import org.orcid.test.TargetProxyHelper;

/**
 * 
 * @author Will Simpson
 *
 */
public class SalesForceManagerImplTest {

    private static final String TEST_ORCID = "4444-4444-4444-4441";

    private SalesForceManager salesForceManager = new SalesForceManagerImpl();

    @Mock
    private SalesForceDao salesForceDao;

    @Mock
    private SourceManager sourceManager;

    @Mock
    private SalesForceConnectionDao salesForceConnectionDao;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(salesForceManager, "salesForceDao", salesForceDao);
        TargetProxyHelper.injectIntoProxy(salesForceManager, "sourceManager", sourceManager);
        TargetProxyHelper.injectIntoProxy(salesForceManager, "salesForceConnectionDao", salesForceConnectionDao);
        setUpUser();
        setUpContact1();
        setUpContact2();
    }

    private void setUpUser() {
        when(sourceManager.retrieveRealUserOrcid()).thenReturn(TEST_ORCID);
        SalesForceConnectionEntity connection = new SalesForceConnectionEntity();
        connection.setSalesForceAccountId("account1Id");
        when(salesForceConnectionDao.findByOrcid(TEST_ORCID)).thenReturn(connection);
    }

    private void setUpContact1() {
        List<ContactRole> contact1Roles = new ArrayList<>();
        when(salesForceDao.retrieveContactRolesByContactIdAndAccountId("contact1Id", "account1Id")).thenReturn(contact1Roles);
    }

    private void setUpContact2() {
        List<ContactRole> contact1Roles = new ArrayList<>();
        contact1Roles.add(createContactRole("contact2Idrole1Id", ContactRoleType.MAIN_CONTACT));
        when(salesForceDao.retrieveContactRolesByContactIdAndAccountId("contact2Id", "account1Id")).thenReturn(contact1Roles);
    }

    private ContactRole createContactRole(String roleId, ContactRoleType roleType) {
        ContactRole contactRole = new ContactRole();
        contactRole.setId(roleId);
        contactRole.setRole(roleType);
        return contactRole;
    }

    @Test
    public void testUpdateContact2() {
        // Switch from main to technical contact
        Contact contact = new Contact();
        contact.setId("contact2Id");
        contact.setAccountId("account1");
        contact.setRole(ContactRoleType.TECHNICAL_CONTACT.value());
        salesForceManager.updateContact(contact);
        verify(salesForceDao, times(1)).createContactRole(argThat(new LambdaMatcher<ContactRole>(r -> {
            return "contact2Id".equals(r.getContactId()) && "account1Id".equals(r.getAccountId()) && ContactRoleType.TECHNICAL_CONTACT.equals(r.getRole());
        })));
        verify(salesForceDao, times(1)).removeContactRole(eq("contact2Idrole1Id"));
    }

}
