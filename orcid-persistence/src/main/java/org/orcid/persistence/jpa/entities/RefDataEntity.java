package org.orcid.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Table(name = "reference_data")
@Entity
public class RefDataEntity extends BaseEntity<Integer> {

    /**
     * 
     */
    private static final long serialVersionUID = -6639759571886764413L;
    private Integer id;
    private String refDataEntityKey;
    private String refDataEntityValue;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "reference_data_seq")
    @SequenceGenerator(name = "reference_data_seq", sequenceName = "reference_data_seq", allocationSize = 1)
    @Column(name = "id")
    public Integer getId() {
        return id;
    }

    @Column(name = "ref_data_key", length = 255)
    public String getRefDataEntityKey() {
        return refDataEntityKey;
    }

    @Column(name = "ref_data_value", length = 255)
    public String getRefDataEntityValue() {
        return refDataEntityValue;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setRefDataEntityKey(String refDataEntityKey) {
        this.refDataEntityKey = refDataEntityKey;
    }

    public void setRefDataEntityValue(String refDataEntityValue) {
        this.refDataEntityValue = refDataEntityValue;
    }

}
