package org.orcid.api.member.server.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class JSONInputErrorHandler implements ErrorHandler {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(JSONInputErrorHandler.class);

    @Override
    public void warning(SAXParseException exception) throws SAXException {
        LOGGER.warn("Invalid JSON", exception);
    }

    @Override
    public void error(SAXParseException exception) throws SAXException {
        LOGGER.error("Invalid JSON", exception);
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
        LOGGER.error("Invalid JSON", exception);
    }

}
