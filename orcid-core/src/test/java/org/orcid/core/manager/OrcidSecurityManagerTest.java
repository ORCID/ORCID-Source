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
package org.orcid.core.manager;

import static org.junit.Assert.fail;

import java.security.AccessControlException;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.orcid.core.BaseTest;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.exception.OrcidVisibilityException;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.common_rc4.CreditName;
import org.orcid.jaxb.model.common_rc4.Source;
import org.orcid.jaxb.model.common_rc4.SourceClientId;
import org.orcid.jaxb.model.common_rc4.Visibility;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record_rc4.Biography;
import org.orcid.jaxb.model.record_rc4.FamilyName;
import org.orcid.jaxb.model.record_rc4.GivenNames;
import org.orcid.jaxb.model.record_rc4.Name;
import org.orcid.jaxb.model.record_rc4.OtherName;
import org.orcid.jaxb.model.record_rc4.Work;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;

/**
 * 
 * @author Will Simpson
 *
 */
public class OrcidSecurityManagerTest extends BaseTest {

    @Resource
    private OrcidSecurityManager orcidSecurityManager;
    
    private final String ORCID_1 = "0000-0000-0000-0001";
    private final String ORCID_2 = "0000-0000-0000-0002";
    
    private final String CLIENT_1 = "APP-0000000000000001";
    private final String CLIENT_2 = "APP-0000000000000002";
    
    @Before
    public void before() {
    	
    }
    
    @After
    public void after() {
        SecurityContextTestUtils.setUpSecurityContextForAnonymous();
    }
    
    // Name element tests
    @Test(expected = OrcidUnauthorizedException.class)
    public void testName_ThrowException_When_TokenIsForOtherUser() {
    	SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_UPDATE);
    	Name name = createName(Visibility.PUBLIC);
    	orcidSecurityManager.checkAndFilter(ORCID_2, name, ScopePathType.ORCID_BIO_READ_LIMITED);
    	fail();
    }
    
    @Test
    public void testName_CanRead_When_DontHaveReadScope_IsPublic() {
    	SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_UPDATE);
    	Name name = createName(Visibility.PUBLIC);
    	orcidSecurityManager.checkAndFilter(ORCID_1, name, ScopePathType.ORCID_BIO_READ_LIMITED);
    }
    
    @Test(expected = AccessControlException.class)
    public void testName_CantRead_When_DontHaveReadScope_IsLimited() {
    	SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_UPDATE);
    	Name name = createName(Visibility.LIMITED);
    	orcidSecurityManager.checkAndFilter(ORCID_1, name, ScopePathType.ORCID_BIO_READ_LIMITED);
    }
    
    @Test(expected = AccessControlException.class)
    public void testName_CantRead_When_DontHaveReadScope_IsPrivate() {
    	SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_UPDATE);
    	Name name = createName(Visibility.PRIVATE);
    	orcidSecurityManager.checkAndFilter(ORCID_1, name, ScopePathType.ORCID_BIO_READ_LIMITED);
    }
    
    @Test
    public void testName_CanRead_When_HaveReadScope_IsPublic() {
    	SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_READ_LIMITED);
    	Name name = createName(Visibility.PUBLIC);
    	orcidSecurityManager.checkAndFilter(ORCID_1, name, ScopePathType.ORCID_BIO_READ_LIMITED);
    }
    
    @Test
    public void testName_CanRead_When_HaveReadScope_IsLimited() {
    	SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_READ_LIMITED);
    	Name name = createName(Visibility.LIMITED);
    	orcidSecurityManager.checkAndFilter(ORCID_1, name, ScopePathType.ORCID_BIO_READ_LIMITED);
    }
    
    @Test(expected = OrcidVisibilityException.class)
    public void testName_CantRead_When_HaveReadScope_IsPrivate() {
    	SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_READ_LIMITED);
    	Name name = createName(Visibility.PRIVATE);
    	orcidSecurityManager.checkAndFilter(ORCID_1, name, ScopePathType.ORCID_BIO_READ_LIMITED);
    }
    
    
    
    
    
    
    
    
    
    
    
    
    //Test having having scope
    //Test without having required scope
    //Test all different types of visibilityes
    
    
    private Name createName() {
        Name name = new Name();
        name.setCreditName(new CreditName("Credit Name"));
        name.setFamilyName(new FamilyName("Family Name"));
        name.setGivenNames(new GivenNames("Given Names"));
        name.setVisibility(Visibility.PUBLIC);
        return name;
    }

    private Biography createBiography() {
        return new Biography("Biography", Visibility.PUBLIC);
    }

    private OtherName createOtherName(Visibility v, String sourceId) {
        OtherName otherName = new OtherName();
        otherName.setContent("other-name");
        otherName.setVisibility(v);
        Source source = new Source();        
        source.setSourceClientId(new SourceClientId(sourceId));
        otherName.setSource(source);
        return otherName;
    }

    private Work createWork(Visibility v, String sourceId) {
        Work work = new Work();
        work.setVisibility(v);
        Source source = new Source();        
        source.setSourceClientId(new SourceClientId(sourceId));
        work.setSource(source);
        return work;
    }
    
    private Name createName(Visibility v) {
    	Name name = new Name();
    	name.setVisibility(v);
    	name.setCreditName(new CreditName("Credit Name"));
    	return name;
    }
    
    private ProfileEntity createProfileEntity() {
        ProfileEntity entity = new ProfileEntity();
        entity.setClaimed(true);
        entity.setDeactivationDate(null);
        entity.setDeprecatedDate(null);
        entity.setId("0000-0000-0000-0000");
        entity.setRecordLocked(false);
        entity.setSource(new SourceEntity(new ClientDetailsEntity("APP-0000000000000000")));
        entity.setSubmissionDate(null);
        return entity;
    }
}
