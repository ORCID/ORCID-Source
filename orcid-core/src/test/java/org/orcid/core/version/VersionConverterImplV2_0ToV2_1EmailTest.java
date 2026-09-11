package org.orcid.core.version;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.version.impl.V2VersionObjectFactoryImpl;
import org.orcid.core.version.impl.VersionConverterImplV2_0ToV2_1;
import org.orcid.jaxb.model.common_v2.Source;
import org.orcid.jaxb.model.common_v2.SourceName;
import org.orcid.jaxb.model.common_v2.SourceOrcid;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.record_v2.Email;
import org.orcid.jaxb.model.record_v2.Emails;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * J21-004. The 2.0 <-> 2.1 converter copies bean properties with the JavaBeans Introspector,
 * which recognises an "is" accessor only for primitive boolean. Email declares verified, current
 * and primary as boxed Boolean with isVerified() / isCurrent() / isPrimary(), so the Introspector
 * reports no read method for them and the converter skipped all three. Every /v2.0 and /v2.1
 * email came back with the flags dropped - and verified in particular is the one callers use to
 * decide whether to trust an address.
 *
 * The values are what matters here, not the keys. The serialiser still emits verified/primary
 * either way, so a test that only checks the field is present passes against the bug.
 *
 * V2VersionConverterChainTest exercises private stub classes of the test's own making and could
 * never have caught this; it takes a real model object to reach the boxed accessors.
 */
public class VersionConverterImplV2_0ToV2_1EmailTest {

    @Mock
    private OrcidUrlManager orcidUrlManager;

    private VersionConverterImplV2_0ToV2_1 converter;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        when(orcidUrlManager.getBaseUrl()).thenReturn("https://orcid.org");
        when(orcidUrlManager.getBaseUriHttp()).thenReturn("http://orcid.org");
        converter = new VersionConverterImplV2_0ToV2_1();
        ReflectionTestUtils.setField(converter, "orcidUrlManager", orcidUrlManager);
        ReflectionTestUtils.setField(converter, "v2VersionObjectFactory", new V2VersionObjectFactoryImpl());
    }

    private Emails emailsWithFlags() {
        Email email = new Email();
        email.setEmail("test@orcid.org");
        email.setPutCode(1L);
        email.setVisibility(Visibility.PUBLIC);
        email.setVerified(Boolean.TRUE);
        email.setPrimary(Boolean.TRUE);
        email.setCurrent(Boolean.TRUE);

        SourceOrcid sourceOrcid = new SourceOrcid();
        sourceOrcid.setPath("0000-0000-0000-0001");
        sourceOrcid.setHost("orcid.org");
        Source source = new Source();
        source.setSourceOrcid(sourceOrcid);
        source.setSourceName(new SourceName("Test source"));
        email.setSource(source);

        Emails emails = new Emails();
        emails.getEmails().add(email);
        return emails;
    }

    private Email onlyEmailOf(V2Convertible converted) {
        assertNotNull("the converter returned nothing", converted);
        Emails emails = (Emails) converted.getObjectToConvert();
        assertNotNull("the converted Emails has no email list", emails.getEmails());
        assertEquals(1, emails.getEmails().size());
        return emails.getEmails().get(0);
    }

    @Test
    public void testDowngradeKeepsTheBooleanFlags() {
        Email email = onlyEmailOf(converter.downgrade(new V2Convertible(emailsWithFlags(), "2.1")));

        assertEquals("test@orcid.org", email.getEmail());
        assertEquals("verified was dropped on the way down to 2.0", Boolean.TRUE, email.isVerified());
        assertEquals("primary was dropped on the way down to 2.0", Boolean.TRUE, email.isPrimary());
        assertEquals("current was dropped on the way down to 2.0", Boolean.TRUE, email.isCurrent());
    }

    @Test
    public void testUpgradeKeepsTheBooleanFlags() {
        Email email = onlyEmailOf(converter.upgrade(new V2Convertible(emailsWithFlags(), "2.0")));

        assertEquals("test@orcid.org", email.getEmail());
        assertEquals("verified was dropped on the way up to 2.1", Boolean.TRUE, email.isVerified());
        assertEquals("primary was dropped on the way up to 2.1", Boolean.TRUE, email.isPrimary());
        assertEquals("current was dropped on the way up to 2.1", Boolean.TRUE, email.isCurrent());
    }

    /**
     * False has to survive as False, not collapse to null. A fallback that only ever copied
     * truthy values would pass the two tests above and still lose the answer that matters:
     * an unverified address reported as "no value" reads the same as one nobody asked about.
     */
    @Test
    public void testFalseIsCarriedThroughAsFalseNotNull() {
        Emails emails = emailsWithFlags();
        emails.getEmails().get(0).setVerified(Boolean.FALSE);
        emails.getEmails().get(0).setPrimary(Boolean.FALSE);

        Email email = onlyEmailOf(converter.downgrade(new V2Convertible(emails, "2.1")));

        assertEquals("an unverified address came back as null instead of false", Boolean.FALSE, email.isVerified());
        assertEquals("a non-primary address came back as null instead of false", Boolean.FALSE, email.isPrimary());
    }

    /**
     * The rest of the object still has to convert. The fix changes how every property descriptor
     * resolves its read method, so a mistake there would quietly stop copying ordinary fields.
     */
    @Test
    public void testOrdinaryFieldsStillConvert() {
        Email email = onlyEmailOf(converter.downgrade(new V2Convertible(emailsWithFlags(), "2.1")));

        assertEquals(Long.valueOf(1L), email.getPutCode());
        assertEquals(Visibility.PUBLIC, email.getVisibility());
        assertNotNull("the source was dropped", email.getSource());
        assertEquals("0000-0000-0000-0001", email.getSource().getSourceOrcid().getPath());
        assertEquals("http://orcid.org/0000-0000-0000-0001", email.getSource().getSourceOrcid().getUri());
    }
}
