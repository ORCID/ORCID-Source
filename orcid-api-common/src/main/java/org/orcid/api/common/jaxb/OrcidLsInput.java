package org.orcid.api.common.jaxb;

import java.io.InputStream;
import java.io.Reader;

import org.w3c.dom.ls.LSInput;

/**
 * 
 * @author Will Simpson
 *
 */
public class OrcidLsInput implements LSInput {

    private String publicId;
    private String systemId;
    private String baseURI;
    private Reader characterStream;
    private InputStream byteStream;
    private String stringData;
    private String encoding;
    private boolean certifiedText;

    OrcidLsInput(String systemId, String publicId, String baseURI) {
        this.systemId = systemId;
        this.publicId = publicId;
        this.baseURI = baseURI;
    }

    @Override
    public String getPublicId() {
        return publicId;
    }

    @Override
    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    @Override
    public String getSystemId() {
        return systemId;
    }

    @Override
    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    @Override
    public String getBaseURI() {
        return baseURI;
    }

    @Override
    public void setBaseURI(String baseURI) {
        this.baseURI = baseURI;
    }

    @Override
    public Reader getCharacterStream() {
        return characterStream;
    }

    @Override
    public void setCharacterStream(Reader characterStream) {
        this.characterStream = characterStream;
    }

    @Override
    public InputStream getByteStream() {
        return byteStream;
    }

    @Override
    public void setByteStream(InputStream byteStream) {
        this.byteStream = byteStream;
    }

    @Override
    public String getStringData() {
        return stringData;
    }

    @Override
    public void setStringData(String stringData) {
        this.stringData = stringData;
    }

    @Override
    public String getEncoding() {
        return encoding;
    }

    @Override
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    @Override
    public boolean getCertifiedText() {
        return certifiedText;
    }

    @Override
    public void setCertifiedText(boolean certifiedText) {
        this.certifiedText = certifiedText;
    }

}
