package org.orcid.core.utils.v3.identifiers.resolvers;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Stack;

import javax.annotation.Resource;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.ext.com.google.common.collect.Lists;
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
            HttpURLConnection con = (HttpURLConnection) new URL(metadataEndpoint + value).openConnection();
            con.addRequestProperty("Accept", "application/vnd.citationstyles.csl+json");
            con.setRequestMethod("GET");
            con.setInstanceFollowRedirects(true);
            if (con.getResponseCode() == 200) {
                Reader reader = new InputStreamReader(con.getInputStream(), "UTF-8");
                InputSource is = new InputSource(reader);
                is.setEncoding("UTF-8");

                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser saxParser = factory.newSAXParser();

                WorksHandler handler = new WorksHandler();

                saxParser.parse(is, handler);
                Work w = handler.getWork();
                return w;
            }
        } catch (IOException e) {
            return null;
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    private class WorksHandler extends DefaultHandler {
        private Work work = new Work();
        private Stack<String> elementStack = new Stack<String>();
        boolean isOnEntry = false;
        StringBuffer description = new StringBuffer();

        public void startElement(String uri, String localName, String currentElementName, Attributes attributes) throws SAXException {
            this.elementStack.push(currentElementName);
            if (currentElementName.equals("entry")) {
                isOnEntry = true;
            }

            if (isOnEntry) {
                if (currentElementName.equals("link")) {
                    int typeIndex = attributes.getIndex("title");
                    if (typeIndex >= 0) {
                        String type = attributes.getValue(typeIndex);
                        if (type.equals("doi")) {
                            String extId = attributes.getValue(attributes.getIndex("href"));
                            if (work.getExternalIdentifiers() == null) {
                                work.setWorkExternalIdentifiers(new ExternalIDs());
                            }
                            ExternalID extID = new ExternalID();
                            extID.setRelationship(Relationship.SELF);
                            extID.setType("DOI");
                            extID.setValue(normalizationService.normalise("doi", extId));
                            extID.setUrl(new Url(extId));
                            work.getWorkExternalIdentifiers().getExternalIdentifier().add(extID);
                        }
                    }
                }
            }
        }

        public void endElement(String uri, String localName, String currentElementName) throws SAXException {
            this.elementStack.pop();
            if (currentElementName.equals("entry")) {
                isOnEntry = false;
            } else if (currentElementName.equals("summary")) {
                if (this.description.length() > 0) {
                    work.setShortDescription(this.description.toString());
                }
            }
        }

        public void characters(char ch[], int start, int length) throws SAXException {
            String currentElement = this.elementStack.peek();
            String value = new String(ch, start, length).trim();

            if (isOnEntry) {
                switch (currentElement) {
                case "id":
                    if (work.getExternalIdentifiers() == null) {
                        work.setWorkExternalIdentifiers(new ExternalIDs());
                    }
                    ExternalID extID = new ExternalID();
                    extID.setRelationship(Relationship.SELF);
                    extID.setType("ARXIV");
                    extID.setValue(normalizationService.normalise("arxiv", value));
                    extID.setUrl(new Url(value));
                    work.getWorkExternalIdentifiers().getExternalIdentifier().add(extID);
                    break;
                case "title":
                    WorkTitle title = new WorkTitle();
                    title.setTitle(new Title(value));
                    work.setWorkTitle(title);
                    break;
                case "summary":
                    if (!PojoUtil.isEmpty(value)) {
                        // In case of multiline content, add a space before
                        // appending the next line content
                        if (this.description.length() > 0 && this.description.charAt(this.description.length() - 1) != ' ') {
                            this.description.append(' ');
                        }
                        this.description.append(value);
                    }
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
                    work.setJournalTitle(new Title(value));
                    break;
                }
            }
        }

        public Work getWork() {
            return work;
        }

    }

}
