package org.orcid.api.search;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.apache.hc.core5.http.ParseException;
import org.orcid.api.publicV3.server.delegator.PublicV3ApiServiceDelegator;
import org.orcid.core.exception.OrcidBadRequestException;
import org.orcid.core.manager.v3.OrcidSearchManager;
import org.orcid.core.togglz.Features;
import org.orcid.jaxb.model.v3.release.groupid.GroupIdRecord;
import org.orcid.jaxb.model.v3.release.record.*;
import org.orcid.jaxb.model.v3.release.search.Search;
import org.orcid.jaxb.model.v3.release.search.expanded.ExpandedSearch;
import org.orcid.utils.solr.entities.SolrConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Component
public class BasicSearchServiceDelegator {

    @Resource(name = "orcidSearchManagerV3")
    private OrcidSearchManager orcidSearchManager;

    /**
     * Whitelist for basic search terms. Matches a string that:
     * <ul>
     * <li>Starts with a letter (\p{L}, any language), a combining mark (\p{M}, e.g. accents) or a digit (\p{N})</li>
     * <li>Is followed by zero or more letters, combining marks, digits, spaces, apostrophes ('), periods (.) or hyphens (-)</li>
     * </ul>
     * Anything else (quotes, colons, brackets, wildcards, semicolons, &lt;, &gt;, =, etc.) is rejected,
     * which blocks Solr query syntax, SQL fragments and HTML/script injection.
     * Examples that match: "José García", "O'Brien", "Jean-Luc", "J. Smith", "李小龙".
     * Note: consecutive hyphens ("--") are rejected separately in validateBasicSearchTerm.
     */
    private static final Pattern BASIC_SEARCH_TERM_PATTERN = Pattern.compile("^[\\p{L}\\p{M}\\p{N}][\\p{L}\\p{M}\\p{N} '.\\-]*$");

    @Value("${org.orcid.papi.basicSearch.qf:given-and-family-names^50.0 family-name^10.0 given-names^10.0 credit-name^10.0 other-names^5.0 text^1.0}")
    private String basicSearchQf;

    @Value("${org.orcid.papi.basicSearch.pf:given-and-family-names^50.0}")
    private String basicSearchPf;

    @Value("${org.orcid.papi.basicSearch.bq:current-institution-affiliation-name:[* TO *]^100.0 past-institution-affiliation-name:[* TO *]^70}")
    private String basicSearchBq;

    @Value("${org.orcid.papi.basicSearch.mm:3<70%}")
    private String basicSearchMm;

    @Value("${org.orcid.papi.basicSearch.rows:50}")
    private int basicSearchRows;

    @Value("${org.orcid.papi.basicSearch.rows.max:200}")
    private int basicSearchRowsMax;

    @Value("${org.orcid.papi.basicSearch.maxTermLength:100}")
    private int basicSearchMaxTermLength;

    public ExpandedSearch searchByQuery(String searchTerm, String start, String rows) throws ParseException {
        if(Features.DISABLE_UI_SEARCH.isActive()) {
            // If the feature is active, return an empty result set to prevent search functionality
            return new ExpandedSearch();
        }
        String term = validateBasicSearchTerm(searchTerm);
        int validStart = validateBasicSearchStart(start);
        int validRows = rows != null ? Integer.parseInt(rows) : basicSearchRows;
        if(validRows > basicSearchRowsMax) {
            validRows = basicSearchRowsMax;
        }

        Map<String, List<String>> solrParams = new HashMap<>();
        solrParams.put("q", List.of(escapeBasicSearchTerm(term)));
        solrParams.put("defType", List.of("edismax"));
        solrParams.put("qf", List.of(basicSearchQf));
        solrParams.put("pf", List.of(basicSearchPf));
        solrParams.put("bq", List.of(basicSearchBq));
        solrParams.put("mm", List.of(basicSearchMm));
        solrParams.put("start", List.of(String.valueOf(validStart)));
        solrParams.put("rows", List.of(String.valueOf(validRows)));
        solrParams.put("fl", SolrConstants.BASIC_SEARCH_ALLOWED_FIELDS);

        ExpandedSearch expandedSearch = orcidSearchManager.expandedSearch(solrParams);
        expandedSearch.getResults().forEach(r -> {
            // Remove emails from search results as they are not displayed in the UI and are not needed for the search results
            if(r.getEmails() != null && r.getEmails().length > 0) {
                r.setEmails(null);
            }
        });
        return expandedSearch;
    }

    private String validateBasicSearchTerm(String searchTerm) {
        if (searchTerm == null) {
            throw new OrcidBadRequestException("Search term is required");
        }
        String term = searchTerm.trim().replaceAll("\\s+", " ");
        if (term.isEmpty() || term.length() > basicSearchMaxTermLength) {
            throw new OrcidBadRequestException("Search term must be between 1 and " + basicSearchMaxTermLength + " characters");
        }
        // Whitelist: letters (any language), combining marks, digits, spaces, apostrophes, periods and single hyphens
        if (!BASIC_SEARCH_TERM_PATTERN.matcher(term).matches() || term.contains("--")) {
            throw new OrcidBadRequestException("Search term contains invalid characters");
        }
        return term;
    }

    private int validateBasicSearchStart(String start) {
        int value;
        try {
            value = Integer.parseInt(start);
        } catch (NumberFormatException e) {
            throw new OrcidBadRequestException("Start must be a valid integer");
        }
        if (value < 0 || value > OrcidSearchManager.MAX_SEARCH_START) {
            throw new OrcidBadRequestException("Start must be between 0 and " + OrcidSearchManager.MAX_SEARCH_START);
        }
        return value;
    }

    // Hyphens are the only allowed char with special meaning in edismax (NOT operator), so escape them
    private String escapeBasicSearchTerm(String term) {
        return term.replace("-", "\\-");
    }
}
