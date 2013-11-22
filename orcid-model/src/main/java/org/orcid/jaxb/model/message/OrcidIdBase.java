package org.orcid.jaxb.model.message;

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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "", propOrder = { "values", "uri", "path", "host" })
public class OrcidIdBase implements Serializable {
    private static final long serialVersionUID = 1L;

    protected List<String> values;

    protected String uri;

    protected String path;

    protected String host;

    public OrcidIdBase() {
        super();
    }

    public OrcidIdBase(OrcidIdBase other) {
        this.values = other.values;
        this.uri = other.uri;
        this.path = other.path;
        this.host = other.host;
    }

    public OrcidIdBase(String path) {
        this.path = path;
    }

    @XmlMixed
    public List<String> getValues() {
        if (values != null) {
            String combinedValues = StringUtils.join(values.toArray());
            if (StringUtils.isBlank(combinedValues)) {
                return null;
            }
        }
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    @XmlTransient
    public String getValue() {
        if (values != null && !values.isEmpty() && StringUtils.isNotBlank(values.get(0))) {
            return values.get(0);
        }
        return null;
    }

    public void setValue(String value) {
        if (values == null) {
            values = new ArrayList<>(1);
        } else {
            values.clear();
        }
        values.add(value);
    }

    @XmlElement
    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @XmlElement
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @XmlElement
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((host == null) ? 0 : host.hashCode());
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        result = prime * result + ((uri == null) ? 0 : uri.hashCode());
        result = prime * result + ((values == null) ? 0 : values.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        OrcidIdBase other = (OrcidIdBase) obj;
        if (host == null) {
            if (other.host != null)
                return false;
        } else if (!host.equals(other.host))
            return false;
        if (path == null) {
            if (other.path != null)
                return false;
        } else if (!path.equals(other.path))
            return false;
        if (uri == null) {
            if (other.uri != null)
                return false;
        } else if (!uri.equals(other.uri))
            return false;
        if (values == null) {
            if (other.values != null)
                return false;
        } else if (!values.equals(other.values))
            return false;
        return true;
    }

}
