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
package org.orcid.solr.writer;

import java.io.IOException;
import java.io.Writer;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.QueryResponseWriter;
import org.apache.solr.response.ResultContext;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.search.DocIterator;
import org.springframework.http.MediaType;

/**
 * 
 * @author Will Simpson
 *
 */
public class OrcidProfileResponseWriter implements QueryResponseWriter {

    @Override
    public void write(Writer writer, SolrQueryRequest request, SolrQueryResponse response) throws IOException {
        ResultContext resultContext = (ResultContext) response.getValues().get("response");
        DocIterator iterator = resultContext.docs.iterator();
        int docId = iterator.nextDoc();
        Document doc = request.getSearcher().doc(docId);
        IndexableField field = doc.getField("public-profile-message");
        writer.append(field.stringValue());
    }

    @Override
    public String getContentType(SolrQueryRequest request, SolrQueryResponse response) {
        return MediaType.APPLICATION_XML_VALUE;
    }

    @Override
    public void init(@SuppressWarnings("rawtypes") NamedList args) {
    }

}
