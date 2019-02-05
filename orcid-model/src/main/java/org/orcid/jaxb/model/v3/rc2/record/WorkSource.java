
package org.orcid.jaxb.model.v3.rc2.record;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.orcid.jaxb.model.v3.rc2.common.OrcidIdBase;

import io.swagger.annotations.ApiModel;

import java.lang.Deprecated;

@Deprecated 
@XmlRootElement(name = "source")
@ApiModel(value = "V3_0_rc2")
public class WorkSource extends OrcidIdBase implements Serializable {

    // This field indicates that the source is null on database
    // So -1 will be the same as a null value on the source
    public static String NULL_SOURCE_PROFILE = "NOT_DEFINED";

    private static final long serialVersionUID = 1L;

    private String sourceName;

    public WorkSource() {
        super();
    }

    public WorkSource(OrcidIdBase other) {
        super(other);
    }

    public WorkSource(String path) {
        super(path);
    }

    public WorkSource(String path, String sourceName) {
        setPath(path);
        setSourceName(sourceName);
    }

    @XmlTransient
    public String getSourceName() {
        return this.sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

}
