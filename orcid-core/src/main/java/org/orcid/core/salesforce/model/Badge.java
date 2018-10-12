package org.orcid.core.salesforce.model;

import java.io.Serializable;

/**
 * 
 * @author Will Simpson
 *
 */
public class Badge implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String publicDescription;
    private Float index;
    private String indexAndName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPublicDescription() {
        return publicDescription;
    }

    public void setPublicDescription(String publicDescription) {
        this.publicDescription = publicDescription;
    }

    public Float getIndex() {
        return index;
    }

    public void setIndex(Float index) {
        this.index = index;
    }

    public String getIndexAndName() {
        return indexAndName;
    }

    public void setIndexAndName(String indexAndName) {
        this.indexAndName = indexAndName;
    }

    @Override
    public String toString() {
        return "Badge [id=" + id + ", name=" + name + ", publicDescription=" + publicDescription + ", index=" + index + ", indexAndName=" + indexAndName + "]";
    }

}
