/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.api.common.jaxb;

import java.io.File;
import java.io.OutputStream;
import java.io.Writer;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.validation.Schema;

import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

/**
 * 
 * @author Will Simpson
 *
 */
public class OrcidMarshallerWrapper implements Marshaller {

    private Marshaller marshaller;

    public OrcidMarshallerWrapper(Marshaller marshaller) {
        this.marshaller = marshaller;
    }

    @Override
    public void marshal(Object jaxbElement, Result result) throws JAXBException {
        marshaller.marshal(jaxbElement, result);
    }

    @Override
    public void marshal(Object jaxbElement, OutputStream os) throws JAXBException {
        marshaller.marshal(jaxbElement, new FilterInvalidXmlCharsOutputStreamWriter(os));
    }

    @Override
    public void marshal(Object jaxbElement, File output) throws JAXBException {
        marshaller.marshal(jaxbElement, output);
    }

    @Override
    public void marshal(Object jaxbElement, Writer writer) throws JAXBException {
        marshaller.marshal(jaxbElement, writer);
    }

    @Override
    public void marshal(Object jaxbElement, ContentHandler handler) throws JAXBException {
        marshaller.marshal(jaxbElement, handler);
    }

    @Override
    public void marshal(Object jaxbElement, Node node) throws JAXBException {
        marshaller.marshal(jaxbElement, node);
    }

    @Override
    public void marshal(Object jaxbElement, XMLStreamWriter writer) throws JAXBException {
        marshaller.marshal(jaxbElement, writer);
    }

    @Override
    public void marshal(Object jaxbElement, XMLEventWriter writer) throws JAXBException {
        marshaller.marshal(jaxbElement, writer);
    }

    @Override
    public Node getNode(Object contentTree) throws JAXBException {
        return marshaller.getNode(contentTree);
    }

    @Override
    public void setProperty(String name, Object value) throws PropertyException {
        marshaller.setProperty(name, value);
    }

    public Object getProperty(String name) throws PropertyException {
        return marshaller.getProperty(name);
    }

    @Override
    public void setEventHandler(ValidationEventHandler handler) throws JAXBException {
        marshaller.setEventHandler(handler);
    }

    @Override
    public ValidationEventHandler getEventHandler() throws JAXBException {
        return marshaller.getEventHandler();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void setAdapter(XmlAdapter adapter) {
        marshaller.setAdapter(adapter);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public <A extends XmlAdapter> void setAdapter(Class<A> type, A adapter) {
        marshaller.setAdapter(type, adapter);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public <A extends XmlAdapter> A getAdapter(Class<A> type) {
        return marshaller.getAdapter(type);
    }

    @Override
    public void setAttachmentMarshaller(AttachmentMarshaller am) {
        marshaller.setAttachmentMarshaller(am);
    }

    @Override
    public AttachmentMarshaller getAttachmentMarshaller() {
        return marshaller.getAttachmentMarshaller();
    }

    @Override
    public void setSchema(Schema schema) {
        marshaller.setSchema(schema);
    }

    @Override
    public Schema getSchema() {
        return marshaller.getSchema();
    }

    @Override
    public void setListener(Listener listener) {
        marshaller.setListener(listener);
    }

    @Override
    public Listener getListener() {
        return marshaller.getListener();
    }
    
}
