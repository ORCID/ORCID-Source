package org.orcid.core.utils;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.orcid.utils.solr.entities.SolrConstants;

public class SolrQueryBuilderTest {

    @Test
    public void testQueryStringPopulation() {

        SolrQueryBuilder solrQueryBuilder = new SolrQueryBuilder("query = ");
        solrQueryBuilder.appendFieldValuePair("abc", "xyz");
        assertEquals("query = abc:xyz", solrQueryBuilder.retrieveQuery());

        // check that blank fields don't corrupt the query
        solrQueryBuilder.appendORCondition("field1", "");
        assertEquals("query = abc:xyz", solrQueryBuilder.retrieveQuery());
        solrQueryBuilder.appendORCondition("field1", "field1");
        assertEquals("query = abc:xyz OR field1:field1", solrQueryBuilder.retrieveQuery());

        solrQueryBuilder.openNestedANDOperation();
        // check that blank fields don't get populated the query - can't second
        // guess the user though so we're left with open brackets
        solrQueryBuilder.appendFieldValuePair("", "1234");
        assertEquals("query = abc:xyz OR field1:field1 AND (", solrQueryBuilder.retrieveQuery());

        solrQueryBuilder.appendFieldValuePair("orcid", "1234");
        solrQueryBuilder.appendORCondition("familyName", "Simpson");
        solrQueryBuilder.closeNestedANDOperation();

        assertEquals("query = abc:xyz OR field1:field1 AND (orcid:1234 OR familyName:Simpson)", solrQueryBuilder.retrieveQuery());

    }

    @Test
    public void testNotQuery() {
        SolrQueryBuilder solrQueryBuilder = new SolrQueryBuilder();
        solrQueryBuilder.appendFieldValuePair(SolrConstants.GIVEN_NAMES, "Will");
        List<String> orcidsToExclude = new ArrayList<String>();
        orcidsToExclude.add("1877-5816-0747-5659");
        orcidsToExclude.add("6181-9093-3346-6284");
        solrQueryBuilder.appendNOTCondition(SolrConstants.ORCID, orcidsToExclude);
        assertEquals("given-names:Will -orcid:(1877\\-5816\\-0747\\-5659 6181\\-9093\\-3346\\-6284)", solrQueryBuilder.retrieveQuery());
    }
}
