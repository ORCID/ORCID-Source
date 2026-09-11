package org.orcid.api.common.jaxb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import jakarta.xml.bind.MarshalException;
import jakarta.xml.bind.ValidationEvent;
import jakarta.xml.bind.helpers.ValidationEventImpl;

import org.junit.Before;
import org.junit.Test;
import org.orcid.api.common.jaxb.OrcidValidationJaxbContextResolver.OrcidValidationHandler;
import org.orcid.core.exception.OrcidBadRequestException;
import org.orcid.jaxb.model.common.adapters.IllegalEnumValueException;
import org.orcid.jaxb.model.common.WorkType;
import org.xml.sax.SAXException;

/**
 * J21-009. An invalid enum in an XML body used to come back as 9051, naming the value the caller
 * got wrong and the enum it belongs to. Since the jakarta migration it comes back as the generic
 * 9012, which names neither.
 *
 * The cause is a check that can never be true. The handler tested whether the linked exception was
 * a com.sun.xml.bind.api.AccessorException, but under jakarta the runtime throws
 * org.glassfish.jaxb.runtime.api.AccessorException - an unrelated class with the same simple name.
 * Nothing failed loudly, because the root pom still manages jaxb-runtime 2.3.8, which still carries
 * the old class, so the import kept resolving and the comparison just kept returning false.
 *
 * These tests deliberately do not name either AccessorException. The fix walks the cause chain for
 * IllegalEnumValueException instead of recognising provider classes, and a test that pinned the
 * provider class would re-introduce exactly the coupling the fix removes.
 */
public class OrcidValidationHandlerTest {

    private OrcidValidationHandler handler;

    @Before
    public void before() {
        handler = new OrcidValidationJaxbContextResolver().new OrcidValidationHandler();
    }

    private ValidationEvent event(int severity, Throwable linkedException) {
        return new ValidationEventImpl(severity, "cvc-enumeration-valid: value not facet-valid", null, linkedException);
    }

    /**
     * The shape the runtime actually produces: something provider-specific wrapping the
     * IllegalEnumValueException as its cause.
     */
    @Test
    public void testInvalidEnumIsThrownOnSoTheCallerGetsTheDetail() {
        IllegalEnumValueException illegal = new IllegalEnumValueException(WorkType.class, "journal-articel");
        Throwable fromTheProvider = new RuntimeException("accessor failure", illegal);

        try {
            handler.handleEvent(event(ValidationEvent.ERROR, fromTheProvider));
            fail("the handler swallowed an invalid enum; the caller gets the generic error, not 9051");
        } catch (IllegalEnumValueException thrown) {
            assertSame("a different exception instance came back", illegal, thrown);
            assertEquals("journal-articel", thrown.getInvalidValue());
            assertEquals(WorkType.class, thrown.getEnumClass());
        } catch (OrcidBadRequestException generic) {
            fail("an invalid enum was reported as a generic bad request. That is the 9012 the "
                    + "caller sees today: it names neither the value they got wrong nor the enum "
                    + "it belongs to. The handler did not recognise the invalid enum in the chain.");
        }
    }

    /**
     * The linked-exception hop is the one a plain getCause() walk misses. JAXBException keeps its
     * real cause in getLinkedException() and leaves getCause() null, so a chain that passes through
     * a MarshalException - which is exactly what unmarshalling a bad enum produces - dead-ends
     * unless the walk knows to follow it.
     */
    @Test
    public void testTheWalkCrossesTheLinkedExceptionHop() {
        IllegalEnumValueException illegal = new IllegalEnumValueException(WorkType.class, "journal-articel");
        MarshalException marshal = new MarshalException(new RuntimeException("accessor failure", illegal));
        assertTrue("this test is only meaningful while JAXBException hides its cause",
                marshal.getCause() == null || marshal.getCause() == marshal.getLinkedException());
        SAXException outer = new SAXException(marshal);

        try {
            handler.handleEvent(event(ValidationEvent.ERROR, outer));
            fail("the walk stopped before reaching the invalid enum three layers down");
        } catch (IllegalEnumValueException thrown) {
            assertSame(illegal, thrown);
        } catch (OrcidBadRequestException generic) {
            fail("the walk stopped at the linked-exception hop and fell through to the generic "
                    + "bad request, which is the 9012 the caller sees today");
        }
    }

    /**
     * The negative case, and the reason it is here: a fix that returned "found it" too eagerly
     * would turn every schema violation into an enum error and this suite would still be green
     * without it.
     */
    @Test
    public void testAnOrdinaryValidationErrorIsStillABadRequest() {
        try {
            handler.handleEvent(event(ValidationEvent.ERROR, new SAXException("element is not allowed here")));
            fail("an ordinary schema violation was accepted instead of refused");
        } catch (OrcidBadRequestException expected) {
            assertTrue(expected.getMessage().contains("cvc-enumeration-valid"));
        } catch (IllegalEnumValueException wrong) {
            fail("an ordinary schema violation was reported as an invalid enum value");
        }
    }

    @Test
    public void testAFatalErrorWithNoLinkedExceptionIsStillABadRequest() {
        try {
            handler.handleEvent(event(ValidationEvent.FATAL_ERROR, null));
            fail("a fatal validation error was accepted");
        } catch (OrcidBadRequestException expected) {
            // as before the migration
        }
    }

    @Test
    public void testAWarningIsAcceptedAndNotThrown() {
        assertTrue("a warning must not abort the unmarshal",
                handler.handleEvent(event(ValidationEvent.WARNING, null)));
    }
}
