package org.orcid.core.utils.v3.identifiers.resolvers;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Stack;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.ext.com.google.common.collect.Lists;
import org.orcid.core.exception.UnexpectedResponseCodeException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.IdentifierTypeManager;
import org.orcid.core.utils.v3.identifiers.PIDNormalizationService;
import org.orcid.core.utils.v3.identifiers.PIDResolverCache;
import org.orcid.jaxb.model.v3.rc1.common.Day;
import org.orcid.jaxb.model.v3.rc1.common.Month;
import org.orcid.jaxb.model.v3.rc1.common.PublicationDate;
import org.orcid.jaxb.model.v3.rc1.common.Title;
import org.orcid.jaxb.model.v3.rc1.common.Url;
import org.orcid.jaxb.model.v3.rc1.common.Year;
import org.orcid.jaxb.model.v3.rc1.record.ExternalID;
import org.orcid.jaxb.model.v3.rc1.record.ExternalIDs;
import org.orcid.jaxb.model.v3.rc1.record.Relationship;
import org.orcid.jaxb.model.v3.rc1.record.Work;
import org.orcid.jaxb.model.v3.rc1.record.WorkTitle;
import org.orcid.pojo.PIDResolutionResult;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.stereotype.Component;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

@Component
public class ArXivResolver implements LinkResolver, MetadataResolver {

    @Resource
    PIDNormalizationService normalizationService;

    @Resource
    PIDResolverCache cache;

    @Resource
    private IdentifierTypeManager identifierTypeManager;

    @Resource
    protected LocaleManager localeManager;

    private String metadataEndpoint = "https://export.arxiv.org/api/query?id_list=";

    List<String> types = Lists.newArrayList("arxiv");

    @Override
    public List<String> canHandle() {
        return types;
    }

    /**
     * Checks for a http 200 normalizing the value and creating a URL using the
     * resolution prefix
     * 
     */
    @Override
    public PIDResolutionResult resolve(String apiTypeName, String value) {
        if (StringUtils.isEmpty(value) || StringUtils.isEmpty(normalizationService.normalise(apiTypeName, value)))
            return PIDResolutionResult.NOT_ATTEMPTED;

        String normUrl = normalizationService.generateNormalisedURL(apiTypeName, value);
        if (!StringUtils.isEmpty(normUrl)) {
            if (cache.isHttp200(normUrl)) {
                return new PIDResolutionResult(true, true, true, normUrl);
            } else {
                return new PIDResolutionResult(false, true, true, null);
            }
        }

        return new PIDResolutionResult(false, false, true, null);// unreachable?
    }

    @Override
    public Work resolveMetadata(String apiTypeName, String value) {
        PIDResolutionResult rr = this.resolve(apiTypeName, value);
        if (!rr.isResolved())
            return null;

        try {
            InputStream inputStream = cache.get(metadataEndpoint + value, MediaType.APPLICATION_ATOM_XML);
            InputSource is = new InputSource(new InputStreamReader(inputStream, StandardCharsets.UTF_8.name()));
            is.setEncoding(StandardCharsets.UTF_8.name());

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            WorksHandler handler = new WorksHandler();

            saxParser.parse(is, handler);
            Work w = handler.getWork();
            return w;
        } catch (UnexpectedResponseCodeException e) {
            // TODO: For future projects, we might want to retry when
            // e.getReceivedCode() tell us that we can retry later, like 503 or
            // 504
        } catch (IOException | ParserConfigurationException | SAXException e) {
            return null;
        }

        return null;
    }

    private class WorksHandler extends DefaultHandler {
        private Work work = new Work();
        private Stack<String> elementStack = new Stack<String>();
        boolean isOnEntry = false;
        StringBuffer title = new StringBuffer();
        StringBuffer description = new StringBuffer();
        StringBuffer journalTitle = new StringBuffer();

        public void startElement(String uri, String localName, String currentElementName, Attributes attributes) throws SAXException {
            this.elementStack.push(currentElementName);
            if (currentElementName.equals("entry")) {
                isOnEntry = true;
            }
        }

        public void endElement(String uri, String localName, String currentElementName) throws SAXException {
            this.elementStack.pop();
            if (currentElementName.equals("entry")) {
                isOnEntry = false;
            } else if (currentElementName.equals("title")) {
                if (this.title.length() > 0) {
                    WorkTitle workTitle = new WorkTitle();
                    workTitle.setTitle(new Title(title.toString()));
                    work.setWorkTitle(workTitle);
                }
            } else if (currentElementName.equals("summary")) {
                if (this.description.length() > 0) {
                    work.setShortDescription(this.description.toString());
                }
            } else if (currentElementName.equals("arxiv:journal_ref")) {
                if (this.journalTitle.length() > 0) {
                    work.setJournalTitle(new Title(this.journalTitle.toString()));
                }
            }
        }

        public void characters(char ch[], int start, int length) throws SAXException {
            String currentElement = this.elementStack.peek();            
            String value = new String(ch, start, length).trim();

            if (isOnEntry && !PojoUtil.isEmpty(value)) {
                switch (currentElement) {
                case "id":
                    if (work.getExternalIdentifiers() == null) {
                        work.setWorkExternalIdentifiers(new ExternalIDs());
                    }
                    ExternalID arxiv = new ExternalID();
                    arxiv.setRelationship(Relationship.SELF);
                    arxiv.setType("ARXIV");
                    arxiv.setValue(normalizationService.normalise("arxiv", value));
                    arxiv.setUrl(new Url(value));
                    work.getWorkExternalIdentifiers().getExternalIdentifier().add(arxiv);
                    break;
                case "title":
                    // In case of multiline content, add a space before
                    // appending the next line content
                    if (this.title.length() > 0 && this.title.charAt(this.title.length() - 1) != ' ') {
                        this.title.append(' ');
                    }
                    this.title.append(value);
                    break;
                case "summary":
                    // In case of multiline content, add a space before
                    // appending the next line content
                    if (this.description.length() > 0 && this.description.charAt(this.description.length() - 1) != ' ') {
                        this.description.append(' ');
                    }
                    this.description.append(value);
                    break;
                case "published":
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    Date date = null;
                    try {
                        date = dateFormat.parse(value);
                        Calendar c = Calendar.getInstance();
                        c.setTime(date);
                        PublicationDate publicationDate = new PublicationDate();
                        work.setPublicationDate(publicationDate);
                        publicationDate.setDay(new Day(c.get(Calendar.DAY_OF_MONTH)));
                        // January = 0
                        publicationDate.setMonth(new Month(c.get(Calendar.MONTH) + 1));
                        publicationDate.setYear(new Year(c.get(Calendar.YEAR)));
                    } catch (ParseException e) {

                    }
                    break;
                case "arxiv:journal_ref":
                    // In case of multiline content, add a space before
                    // appending the next line content
                    if (this.journalTitle.length() > 0 && this.journalTitle.charAt(this.journalTitle.length() - 1) != ' ') {
                        this.journalTitle.append(' ');
                    }
                    this.journalTitle.append(value);
                    break;
                case "arxiv:doi":
                    ExternalID doi = new ExternalID();
                    doi.setRelationship(Relationship.SELF);
                    doi.setType("DOI");
                    doi.setValue(normalizationService.normalise("doi", value));
                    doi.setUrl(new Url(normalizationService.generateNormalisedURL("doi", value)));
                    work.getWorkExternalIdentifiers().getExternalIdentifier().add(doi);
                    break;
                }
            }
        }

        public Work getWork() {
            return work;
        }

    }

}
