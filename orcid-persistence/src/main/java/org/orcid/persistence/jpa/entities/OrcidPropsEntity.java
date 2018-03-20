package org.orcid.persistence.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Will Simpson
 */
@Entity
@Table(name = "orcid_props")
public class OrcidPropsEntity extends BaseEntity<String> {

    private static final long serialVersionUID = 1;

    private String key;
    private String value;

    @Override
    @Id
    @Column(name = "key", length = 255)
    public String getId() {
        return key;
    }

    public void setId(String key) {
        this.key = key;
    }

    @Column(name = "prop_value")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}