package org.orcid.utils.solr.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

/**
 * Verifies that the relationship based identifier maps are bound to the dynamic Solr fields the
 * search queries rely on, e.g. a "grant_number-funded-by" map key must end up as a
 * "grant_number-funded-by" field on the document. This is the same binder SolrJ uses internally
 * when SolrIndexUpdater calls solrClient.addBean(...).
 */
public class OrcidSolrDocumentTest {

    @Test
    public void relationshipIdentifiersAreBoundToDynamicFields() {
        OrcidSolrDocument doc = new OrcidSolrDocument();
        doc.setOrcid("0000-0001-2345-6789");
        doc.setSelfIds(map("doi" + SolrConstants.DYNAMIC_SELF, "10.1000/self"));
        doc.setPartOfIds(map("doi" + SolrConstants.DYNAMIC_PART_OF, "10.1000/part-of"));
        doc.setVersionOfIds(map("doi" + SolrConstants.DYNAMIC_VERSION_OF, "10.1000/version-of"));
        doc.setFundedByIds(map("grant_number" + SolrConstants.DYNAMIC_FUNDED_BY, "funded-by-value"));

        SolrInputDocument solrDoc = new DocumentObjectBinder().toSolrInputDocument(doc);

        assertTrue(solrDoc.containsKey("doi-self"));
        assertTrue(solrDoc.containsKey("doi-part-of"));
        assertTrue(solrDoc.containsKey("doi-version-of"));
        assertTrue(solrDoc.containsKey("grant_number-funded-by"));

        assertTrue(solrDoc.getFieldValues("doi-version-of").contains("10.1000/version-of"));
        assertTrue(solrDoc.getFieldValues("grant_number-funded-by").contains("funded-by-value"));
    }

    @Test
    public void fundedByIdsAreIncludedInEqualsAndHashCode() {
        OrcidSolrDocument one = new OrcidSolrDocument();
        OrcidSolrDocument two = new OrcidSolrDocument();
        assertEquals(one, two);

        one.setFundedByIds(map("grant_number" + SolrConstants.DYNAMIC_FUNDED_BY, "funded-by-value"));
        assertTrue(!one.equals(two));

        two.setFundedByIds(map("grant_number" + SolrConstants.DYNAMIC_FUNDED_BY, "funded-by-value"));
        assertEquals(one, two);
        assertEquals(one.hashCode(), two.hashCode());
    }

    private Map<String, java.util.List<String>> map(String key, String value) {
        Map<String, java.util.List<String>> result = new HashMap<String, java.util.List<String>>();
        result.put(key, Arrays.asList(value));
        return result;
    }
}
