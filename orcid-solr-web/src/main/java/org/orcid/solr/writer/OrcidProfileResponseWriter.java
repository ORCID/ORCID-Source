package org.orcid.solr.writer;

import java.io.IOException;
import java.io.Writer;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.solr.common.SolrException;
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
        if (iterator.hasNext()) {
            int docId = iterator.nextDoc();
            Document doc = request.getSearcher().doc(docId);
            IndexableField field = doc.getField("public-profile-message");
            writer.append(field.stringValue());
        } else
            throw new SolrException(SolrException.ErrorCode.NOT_FOUND, "No record found for reponse writer");
    }

    @Override
    public String getContentType(SolrQueryRequest request, SolrQueryResponse response) {
        return MediaType.APPLICATION_XML_VALUE;
    }

    @Override
    public void init(@SuppressWarnings("rawtypes") NamedList args) {
    }

}
