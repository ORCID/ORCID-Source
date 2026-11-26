package org.orcid.api.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.core.exception.OrcidBadRequestException;
import org.orcid.core.manager.v3.OrcidSearchManager;
import org.orcid.core.togglz.Features;
import org.orcid.jaxb.model.v3.release.search.expanded.ExpandedResult;
import org.orcid.jaxb.model.v3.release.search.expanded.ExpandedSearch;
import org.orcid.utils.solr.entities.SolrConstants;
import org.springframework.test.util.ReflectionTestUtils;
import org.togglz.junit.TogglzRule;

public class BasicSearchServiceDelegatorTest {

    private static final String QF = "given-and-family-names^50.0 family-name^10.0";
    private static final String PF = "given-and-family-names^50.0";
    private static final String BQ = "current-institution-affiliation-name:[* TO *]^100.0";
    private static final String MM = "3<-1 6<80%";
    private static final int DEFAULT_ROWS = 50;
    private static final int MAX_ROWS = 200;
    private static final int MAX_TERM_LENGTH = 100;
    private static final int MAX_START = 10000;

    @Rule
    public TogglzRule togglzRule = TogglzRule.allDisabled(Features.class);

    @Mock
    private OrcidSearchManager orcidSearchManager;

    private BasicSearchServiceDelegator delegator;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        delegator = new BasicSearchServiceDelegator();
        ReflectionTestUtils.setField(delegator, "orcidSearchManager", orcidSearchManager);
        ReflectionTestUtils.setField(delegator, "basicSearchQf", QF);
        ReflectionTestUtils.setField(delegator, "basicSearchPf", PF);
        ReflectionTestUtils.setField(delegator, "basicSearchBq", BQ);
        ReflectionTestUtils.setField(delegator, "basicSearchMm", MM);
        ReflectionTestUtils.setField(delegator, "basicSearchRows", DEFAULT_ROWS);
        ReflectionTestUtils.setField(delegator, "basicSearchRowsMax", MAX_ROWS);
        ReflectionTestUtils.setField(delegator, "basicSearchMaxTermLength", MAX_TERM_LENGTH);
        when(orcidSearchManager.expandedSearch(any())).thenAnswer(invocation -> new ExpandedSearch());
    }

    // ---------------------------------------------------------------------
    // Happy path
    // ---------------------------------------------------------------------

    @Test
    public void searchByQuery_happyPath_buildsExpectedSolrParams() throws Exception {
        ExpandedSearch expected = new ExpandedSearch();
        expected.setNumFound(1L);
        ExpandedResult result = new ExpandedResult();
        result.setOrcidId("0000-0002-1825-0097");
        result.setGivenNames("John");
        result.setFamilyNames("Smith");
        expected.getResults().add(result);
        when(orcidSearchManager.expandedSearch(any())).thenReturn(expected);

        ExpandedSearch actual = delegator.searchByQuery("John Smith", "10", "25");

        assertSame(expected, actual);
        assertEquals(Long.valueOf(1L), actual.getNumFound());
        assertEquals(1, actual.getResults().size());
        assertEquals("0000-0002-1825-0097", actual.getResults().get(0).getOrcidId());

        Map<String, List<String>> params = captureParams();
        assertEquals(9, params.size());
        assertEquals(List.of("John Smith"), params.get("q"));
        assertEquals(List.of("edismax"), params.get("defType"));
        assertEquals(List.of(QF), params.get("qf"));
        assertEquals(List.of(PF), params.get("pf"));
        assertEquals(List.of(BQ), params.get("bq"));
        assertEquals(List.of(MM), params.get("mm"));
        assertEquals(List.of("10"), params.get("start"));
        assertEquals(List.of("25"), params.get("rows"));
        assertEquals(SolrConstants.BASIC_SEARCH_ALLOWED_FIELDS, params.get("fl"));
    }

    @Test
    public void searchByQuery_removesEmailsFromResults() throws Exception {
        ExpandedSearch search = new ExpandedSearch();
        ExpandedResult withEmails = new ExpandedResult();
        withEmails.setEmails(new String[] { "john@example.com" });
        ExpandedResult withoutEmails = new ExpandedResult();
        ExpandedResult withEmptyEmails = new ExpandedResult();
        withEmptyEmails.setEmails(new String[0]);
        search.getResults().add(withEmails);
        search.getResults().add(withoutEmails);
        search.getResults().add(withEmptyEmails);
        when(orcidSearchManager.expandedSearch(any())).thenReturn(search);

        ExpandedSearch actual = delegator.searchByQuery("John", "0", "10");

        assertNull(actual.getResults().get(0).getEmails());
        assertNull(actual.getResults().get(1).getEmails());
        assertNotNull(actual.getResults().get(2).getEmails());
        assertEquals(0, actual.getResults().get(2).getEmails().length);
    }

    @Test
    public void searchByQuery_emptyResults() throws Exception {
        ExpandedSearch actual = delegator.searchByQuery("Nobody", "0", "10");
        assertNotNull(actual);
        assertTrue(actual.getResults().isEmpty());
    }

    @Test
    public void searchByQuery_acceptsValidInternationalAndPunctuatedNames() throws Exception {
        String[] validTerms = { "José García", "O'Brien", "J. Smith", "李小龙", "Müller", "Ñandú", "Zoë", "Владимир", "محمد", "Nguyễn",
                "John Smith 3rd", "a", "1" };
        for (String term : validTerms) {
            Mockito.clearInvocations(orcidSearchManager);
            delegator.searchByQuery(term, "0", "10");
            assertEquals("Unexpected q for term: " + term, List.of(term), captureParams().get("q"));
        }
    }

    // ---------------------------------------------------------------------
    // Feature toggle
    // ---------------------------------------------------------------------

    @Test
    public void searchByQuery_uiSearchDisabled_returnsEmptyAndSkipsSearch() throws Exception {
        togglzRule.enable(Features.DISABLE_UI_SEARCH);

        ExpandedSearch actual = delegator.searchByQuery("John", "0", "10");

        assertNotNull(actual);
        assertTrue(actual.getResults().isEmpty());
        verify(orcidSearchManager, never()).expandedSearch(any());
    }

    @Test
    public void searchByQuery_uiSearchDisabled_doesNotValidateInput() throws Exception {
        togglzRule.enable(Features.DISABLE_UI_SEARCH);

        ExpandedSearch actual = delegator.searchByQuery("<script>alert(1)</script>", "-1", "abc");

        assertNotNull(actual);
        assertTrue(actual.getResults().isEmpty());
        verify(orcidSearchManager, never()).expandedSearch(any());
    }

    // ---------------------------------------------------------------------
    // Search term edge cases
    // ---------------------------------------------------------------------

    @Test
    public void searchByQuery_nullTerm_rejected() {
        assertBadRequest(null, "0", "10", "Search term is required");
    }

    @Test
    public void searchByQuery_emptyTerm_rejected() {
        assertBadRequest("", "0", "10", "Search term must be between 1 and " + MAX_TERM_LENGTH + " characters");
    }

    @Test
    public void searchByQuery_whitespaceOnlyTerm_rejected() {
        assertBadRequest("   \t\n  ", "0", "10", "Search term must be between 1 and " + MAX_TERM_LENGTH + " characters");
    }

    @Test
    public void searchByQuery_termAtMaxLength_accepted() throws Exception {
        String term = repeat('a', MAX_TERM_LENGTH);
        delegator.searchByQuery(term, "0", "10");
        assertEquals(List.of(term), captureParams().get("q"));
    }

    @Test
    public void searchByQuery_termOverMaxLength_rejected() {
        assertBadRequest(repeat('a', MAX_TERM_LENGTH + 1), "0", "10", "Search term must be between 1 and " + MAX_TERM_LENGTH + " characters");
    }

    @Test
    public void searchByQuery_lengthIsMeasuredAfterNormalization() throws Exception {
        String term = "   " + repeat('a', MAX_TERM_LENGTH) + "   ";
        delegator.searchByQuery(term, "0", "10");
        assertEquals(List.of(repeat('a', MAX_TERM_LENGTH)), captureParams().get("q"));
    }

    @Test
    public void searchByQuery_trimsAndCollapsesWhitespace() throws Exception {
        delegator.searchByQuery("  John \t\n  Smith  ", "0", "10");
        assertEquals(List.of("John Smith"), captureParams().get("q"));
    }

    @Test
    public void searchByQuery_hyphensAreEscaped() throws Exception {
        delegator.searchByQuery("Jean-Luc Picard-Smith", "0", "10");
        assertEquals(List.of("Jean\\-Luc Picard\\-Smith"), captureParams().get("q"));
    }

    @Test
    public void searchByQuery_orcidIdLikeTerm_hyphensEscaped() throws Exception {
        delegator.searchByQuery("0000-0002-1825-0097", "0", "10");
        assertEquals(List.of("0000\\-0002\\-1825\\-0097"), captureParams().get("q"));
    }

    @Test
    public void searchByQuery_consecutiveHyphens_rejected() {
        assertInvalidCharacters("Jean--Luc");
    }

    @Test
    public void searchByQuery_termStartingWithNonAlphanumeric_rejected() {
        assertInvalidCharacters("-John");
        assertInvalidCharacters("'John");
        assertInvalidCharacters(".John");
    }

    // ---------------------------------------------------------------------
    // Start edge cases
    // ---------------------------------------------------------------------

    @Test
    public void searchByQuery_startBoundaries_accepted() throws Exception {
        delegator.searchByQuery("John", "0", "10");
        assertEquals(List.of("0"), captureParams().get("start"));

        Mockito.clearInvocations(orcidSearchManager);
        delegator.searchByQuery("John", String.valueOf(MAX_START), "10");
        assertEquals(List.of(String.valueOf(MAX_START)), captureParams().get("start"));
    }

    @Test
    public void searchByQuery_nullStart_rejected() {
        assertBadRequest("John", null, "10", "Start must be a valid integer");
    }

    @Test
    public void searchByQuery_nonNumericStart_rejected() {
        assertBadRequest("John", "abc", "10", "Start must be a valid integer");
        assertBadRequest("John", "", "10", "Start must be a valid integer");
        assertBadRequest("John", "1.5", "10", "Start must be a valid integer");
        assertBadRequest("John", "0 OR 1=1", "10", "Start must be a valid integer");
        assertBadRequest("John", "99999999999", "10", "Start must be a valid integer");
    }

    @Test
    public void searchByQuery_negativeStart_rejected() {
        assertBadRequest("John", "-1", "10", "Start must be between 0 and " + MAX_START);
    }

    @Test
    public void searchByQuery_startOverMax_rejected() {
        assertBadRequest("John", String.valueOf(MAX_START + 1), "10", "Start must be between 0 and " + MAX_START);
    }

    // ---------------------------------------------------------------------
    // Rows edge cases
    // ---------------------------------------------------------------------

    @Test
    public void searchByQuery_nullRows_usesDefault() throws Exception {
        delegator.searchByQuery("John", "0", null);
        assertEquals(List.of(String.valueOf(DEFAULT_ROWS)), captureParams().get("rows"));
    }

    @Test
    public void searchByQuery_rowsAtMax_accepted() throws Exception {
        delegator.searchByQuery("John", "0", String.valueOf(MAX_ROWS));
        assertEquals(List.of(String.valueOf(MAX_ROWS)), captureParams().get("rows"));
    }

    @Test
    public void searchByQuery_rowsOverMax_capped() throws Exception {
        delegator.searchByQuery("John", "0", String.valueOf(MAX_ROWS + 1));
        assertEquals(List.of(String.valueOf(MAX_ROWS)), captureParams().get("rows"));
    }

    @Test
    public void searchByQuery_hugeRows_capped() throws Exception {
        delegator.searchByQuery("John", "0", String.valueOf(Integer.MAX_VALUE));
        assertEquals(List.of(String.valueOf(MAX_ROWS)), captureParams().get("rows"));
    }

    // ---------------------------------------------------------------------
    // Injection attempts
    // ---------------------------------------------------------------------

    @Test
    public void searchByQuery_sqlInjection_rejected() {
        String[] payloads = { "' OR '1'='1", "John' OR '1'='1", "John'; DROP TABLE profile;--", "Robert'); DROP TABLE students;--", "admin'--",
                "1 OR 1=1", "John\" OR \"\"=\"", "John' AND SLEEP(5)#", "John' UNION SELECT * FROM email;", "John/* comment */", "John;",
                "x' AND 1=(SELECT COUNT(*) FROM profile)--", "John' || 'a'='a", "John%27%20OR%201=1" };
        assertAllRejected(payloads);
    }

    @Test
    public void searchByQuery_htmlInjection_rejected() {
        String[] payloads = { "<b>John</b>", "John<br>", "<img src=x onerror=alert(1)>", "<a href=\"http://evil.com\">click</a>",
                "<iframe src=javascript:alert(1)>", "<svg onload=alert(1)>", "&lt;b&gt;John", "John&amp;", "<!-- John -->",
                "\uFF1Cscript\uFF1E", "John\u003Cscript\u003E" };
        assertAllRejected(payloads);
    }

    @Test
    public void searchByQuery_scriptInjection_rejected() {
        String[] payloads = { "<script>alert(1)</script>", "<SCRIPT>alert('xss')</SCRIPT>", "javascript:alert(1)", "John\" onmouseover=\"alert(1)",
                "John'); alert('xss');//", "${7*7}", "#{7*7}", "{{7*7}}", "<%= 7*7 %>", "John`alert(1)`", "%3Cscript%3Ealert(1)%3C/script%3E",
                "Jo\u0000hn", "data:text/html;base64,PHNjcmlwdD4=" };
        assertAllRejected(payloads);
    }

    @Test
    public void searchByQuery_solrQueryInjection_rejected() {
        String[] payloads = { "*:*", "*", "John*", "Jo?n", "family-name:Smith", "John OR family-name:*", "John~", "John~2", "John^100", "(John)",
                "[* TO *]", "{!lucene}John", "John && Smith", "John || Smith", "+John", "!John", "\"John Smith\"", "John\\", "/John.*/",
                "John:", "_query_:\"{!dismax}John\"" };
        assertAllRejected(payloads);
    }

    // ---------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    private Map<String, List<String>> captureParams() {
        ArgumentCaptor<Map<String, List<String>>> captor = ArgumentCaptor.forClass(Map.class);
        verify(orcidSearchManager).expandedSearch(captor.capture());
        return captor.getValue();
    }

    private void assertAllRejected(String[] payloads) {
        for (String payload : payloads) {
            assertInvalidCharacters(payload);
        }
        verify(orcidSearchManager, never()).expandedSearch(any());
    }

    private void assertInvalidCharacters(String term) {
        assertBadRequest(term, "0", "10", "Search term contains invalid characters");
    }

    private void assertBadRequest(String term, String start, String rows, String expectedMessage) {
        try {
            delegator.searchByQuery(term, start, rows);
            fail("Expected OrcidBadRequestException for term [" + term + "], start [" + start + "], rows [" + rows + "]");
        } catch (OrcidBadRequestException e) {
            assertEquals("Unexpected message for term [" + term + "]", expectedMessage, e.getMessage());
        } catch (Exception e) {
            fail("Unexpected exception " + e + " for term [" + term + "]");
        }
        verify(orcidSearchManager, never()).expandedSearch(any());
    }

    private static String repeat(char c, int count) {
        return String.valueOf(c).repeat(count);
    }
}
