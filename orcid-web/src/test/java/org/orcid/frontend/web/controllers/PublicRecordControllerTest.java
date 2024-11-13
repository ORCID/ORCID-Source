package org.orcid.frontend.web.controllers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.manager.v3.read_only.*;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.SourceClientId;
import org.orcid.jaxb.model.v3.release.common.SourceName;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.*;
import org.orcid.pojo.PublicRecord;
import org.orcid.test.TargetProxyHelper;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class PublicRecordControllerTest {

    private static final String USER_ORCID = "0000-0000-0000-0001";
    private static final String USER_CREDIT_NAME = "Credit Name";

    @Resource
    PublicRecordController publicRecordController;

    @Mock
    private HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    @Mock
    private PersonalDetailsManagerReadOnly mockPersonalDetailsManagerReadOnly;

    @Mock
    private ProfileKeywordManagerReadOnly mockKeywordManagerReadOnly;

    @Mock
    private AddressManagerReadOnly mockAddressManagerReadOnly;

    @Mock
    private ResearcherUrlManagerReadOnly mockResearcherUrlManagerReadOnly;

    @Mock
    private EmailManagerReadOnly mockEmailManagerReadOnly;

    @Mock
    private ProfileEntityManager mockProfileEntityManager;

    @Mock
    private ExternalIdentifierManagerReadOnly mockExternalIdentifierManagerReadOnly;

    @Mock
    private ProfileEmailDomainManagerReadOnly mockProfileEmailDomainManagerReadOnly;

    @Mock
    private LocaleManager mockLocaleManager;

    @Mock
    private HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        publicRecordController = new PublicRecordController();

        Mockito.when(request.getRequestURI()).thenReturn("/");

        TargetProxyHelper.injectIntoProxy(publicRecordController, "profileEmailDomainManagerReadOnly", mockProfileEmailDomainManagerReadOnly);
        TargetProxyHelper.injectIntoProxy(publicRecordController, "personalDetailsManagerReadOnly", mockPersonalDetailsManagerReadOnly);
        TargetProxyHelper.injectIntoProxy(publicRecordController, "addressManagerReadOnly", mockAddressManagerReadOnly);
        TargetProxyHelper.injectIntoProxy(publicRecordController, "keywordManagerReadOnly", mockKeywordManagerReadOnly);
        TargetProxyHelper.injectIntoProxy(publicRecordController, "researcherUrlManagerReadOnly", mockResearcherUrlManagerReadOnly);
        TargetProxyHelper.injectIntoProxy(publicRecordController, "externalIdentifierManagerReadOnly", mockExternalIdentifierManagerReadOnly);
        TargetProxyHelper.injectIntoProxy(publicRecordController, "emailManagerReadOnly", mockEmailManagerReadOnly);
        TargetProxyHelper.injectIntoProxy(publicRecordController, "profileEntityManager", mockProfileEntityManager);
        TargetProxyHelper.injectIntoProxy(publicRecordController, "localeManager", mockLocaleManager);

        when(mockPersonalDetailsManagerReadOnly.getPublicPersonalDetails(eq(USER_ORCID))).thenReturn(null);
        when(mockAddressManagerReadOnly.getPublicAddresses(eq(USER_ORCID))).thenReturn(new Addresses());
        when(mockKeywordManagerReadOnly.getPublicKeywords(eq(USER_ORCID))).thenReturn(new Keywords());
        when(mockResearcherUrlManagerReadOnly.getPublicResearcherUrls(eq(USER_ORCID))).thenReturn(new ResearcherUrls());
        when(mockProfileEmailDomainManagerReadOnly.getPublicEmailDomains(eq(USER_ORCID))).thenReturn(Collections.emptyList());
        when(mockEmailManagerReadOnly.getPublicEmails(Mockito.anyString())).thenAnswer(new Answer<Emails>() {

            @Override
            public Emails answer(InvocationOnMock invocation) throws Throwable {
                Emails emails = new Emails();
                Email email1 = new Email();
                email1.setEmail(invocation.getArgument(0) + "_1@test.orcid.org");
                email1.setSource(new Source());
                email1.setVisibility(Visibility.PUBLIC);
                email1.setVerified(true);
                emails.getEmails().add(email1);

                Email email2 = new Email();
                email2.setEmail(invocation.getArgument(0) + "_2@test.orcid.org");
                email2.setSource(new Source());
                email2.getSource().setSourceName(new SourceName(USER_CREDIT_NAME));
                email2.setVerified(true);
                email2.setVisibility(Visibility.PUBLIC);
                emails.getEmails().add(email2);

                Email email3 = new Email();
                email3.setEmail(invocation.getArgument(0) + "_3@test.orcid.org");
                email3.setSource(new Source());
                email3.getSource().setSourceClientId(new SourceClientId(USER_ORCID));
                email3.setVisibility(Visibility.PUBLIC);
                email3.setVerified(true);
                emails.getEmails().add(email3);
                return emails;
            }

        });
    }

    @Test
    public void testEmptyEmailSource() {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        MockHttpSession mockSession = new MockHttpSession();
        mockRequest.setSession(mockSession);
        PublicRecord publicRecord = publicRecordController.getRecord(USER_ORCID);

        assertEquals(3, publicRecord.getEmails().getEmails().size());

        org.orcid.pojo.ajaxForm.Email email1 =  publicRecord.getEmails().getEmails().get(0);
        assertEquals(email1.getValue(), USER_ORCID + "_1@test.orcid.org");
        assertEquals(email1.getSource(), USER_ORCID);
        assertNull(email1.getSourceName());

        org.orcid.pojo.ajaxForm.Email email2 =  publicRecord.getEmails().getEmails().get(1);
        assertEquals(email2.getValue(), USER_ORCID + "_2@test.orcid.org");
        assertNull(email2.getSource());
        assertEquals(email2.getSourceName(), USER_CREDIT_NAME);
    }

    @Test
    public void testEmailSourceWithSourceName() {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        MockHttpSession mockSession = new MockHttpSession();
        mockRequest.setSession(mockSession);
        PublicRecord publicRecord = publicRecordController.getRecord(USER_ORCID);

        assertEquals(3, publicRecord.getEmails().getEmails().size());

        org.orcid.pojo.ajaxForm.Email email2 = publicRecord.getEmails().getEmails().get(1);
        assertEquals(email2.getValue(), USER_ORCID + "_2@test.orcid.org");
        assertNull(email2.getSource());
        assertEquals(email2.getSourceName(), USER_CREDIT_NAME);
    }

    @Test
    public void testEmailSourceWithSourceId() {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        MockHttpSession mockSession = new MockHttpSession();
        mockRequest.setSession(mockSession);
        PublicRecord publicRecord = publicRecordController.getRecord(USER_ORCID);

        assertEquals(3, publicRecord.getEmails().getEmails().size());

        org.orcid.pojo.ajaxForm.Email email3 = publicRecord.getEmails().getEmails().get(2);
        assertEquals(email3.getValue(), USER_ORCID + "_3@test.orcid.org");
        assertNull(email3.getSourceName());
        assertEquals(email3.getSource(), USER_ORCID);
    }

}
