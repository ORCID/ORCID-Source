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
package org.orcid.frontend.web.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.core.security.OrcidWebRole;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.message.OrcidType;
import org.orcid.persistence.jpa.entities.GivenPermissionToEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileSummaryEntity;
import org.orcid.persistence.jpa.entities.RecordNameEntity;
import org.orcid.pojo.DelegateForm;
import org.orcid.test.TargetProxyHelper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author Declan Newman (declan) Date: 23/02/2012
 */
public class ManageProfileControllerTest {

    private ManageProfileController controller;

    private static final String USER_ORCID = "0000-0000-0000-0001";
    
    @Mock
    ProfileEntityCacheManager profileEntityCacheManager;

    @Before
    public void initMocks() throws Exception {
        controller = new ManageProfileController();
        MockitoAnnotations.initMocks(this);                   
        SecurityContextHolder.getContext().setAuthentication(getAuthentication(USER_ORCID));        
        TargetProxyHelper.injectIntoProxy(controller, "profileEntityCacheManager", profileEntityCacheManager);
        
        when(profileEntityCacheManager.retrieve(Mockito.eq(USER_ORCID))).then(new Answer<ProfileEntity>(){

            @Override
            public ProfileEntity answer(InvocationOnMock invocation) throws Throwable {
                ProfileEntity entity = new ProfileEntity();
                entity.setId(invocation.getArgument(0));
                Set<GivenPermissionToEntity> givenPermissionTo = new HashSet<GivenPermissionToEntity>();
                
                IntStream.range(0, 2).forEachOrdered(i -> {
                    GivenPermissionToEntity e1 = new GivenPermissionToEntity();
                    e1.setId(Long.valueOf(i));
                    Date now = new Date();
                    e1.setApprovalDate(now);
                    e1.setDateCreated(now);
                    e1.setGiver(invocation.getArgument(0));
                    ProfileSummaryEntity ps = new ProfileSummaryEntity();
                    RecordNameEntity recordName = new RecordNameEntity();
                    recordName.setVisibility(Visibility.PUBLIC);
                    if(i == 0) {
                        ps.setId("0000-0000-0000-0002");                        
                        recordName.setCreditName("Credit Name");                        
                    } else {
                        ps.setId("0000-0000-0000-0003");
                        recordName.setFamilyName("Family Name");
                        recordName.setGivenNames("Given Names");
                    }
                    ps.setRecordNameEntity(recordName);
                    e1.setReceiver(ps);
                    givenPermissionTo.add(e1);                    
                });
                entity.setGivenPermissionTo(givenPermissionTo);
                return entity;
            }            
        });
    }
    
    @Test
    public void testGetDelegates() {       
        List<DelegateForm> list = controller.getDelegates();
        assertNotNull(list);
        assertEquals(2, list.size());
        boolean found1 = false, found2 = false;
        for(DelegateForm form : list) {
            assertNotNull(form);
            assertNotNull(form.getApprovalDate());
            assertEquals(USER_ORCID, form.getGiverOrcid().getValue());
            assertNotNull(form.getReceiverOrcid());
            if(form.getReceiverOrcid().getValue().equals("0000-0000-0000-0002")) {
                assertEquals("Credit Name", form.getReceiverName().getValue());
                found1 = true;
            } else {
                assertEquals("0000-0000-0000-0003", form.getReceiverOrcid().getValue());
                assertEquals("Given Names Family Name", form.getReceiverName().getValue());
                found2 = true;
            }
        }
        
        assertTrue(found1);
        assertTrue(found2);
    }
    
    protected Authentication getAuthentication(String orcid) {
        OrcidProfileUserDetails details = new OrcidProfileUserDetails(USER_ORCID, "user_1@test.orcid.org", null);
        details.setOrcidType(OrcidType.USER);        
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(details, orcid, Arrays.asList(OrcidWebRole.ROLE_USER));
        return auth;
    }
}