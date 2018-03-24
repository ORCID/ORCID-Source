package org.orcid.persistence.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "identifier_type")
public class IdentifierTypeEntity extends BaseEntity<Long>{

    private static final long serialVersionUID = 1L;
   
    private static final String DEFUALT_USE = "work";
    
    private Long id;
    private String id_name;
    private String id_validation_regex;
    private String id_resolution_prefix;
    private Boolean id_deprecated = Boolean.FALSE;
    private Boolean case_sensitive = Boolean.FALSE;
    private String primary_use = DEFUALT_USE;

    private ClientDetailsEntity sourceClient;
    
    @Column(name = "id_name")
    public String getName() {
        return id_name;
    }

    public void setName(String id_name) {
        this.id_name = id_name;
    }

    @Column(name = "id_validation_regex")
    public String getValidationRegex() {
        return id_validation_regex;
    }

    public void setValidationRegex(String id_validation_regex) {
        this.id_validation_regex = id_validation_regex;
    }

    @Column(name = "id_resolution_prefix")
    public String getResolutionPrefix() {
        return id_resolution_prefix;
    }

    public void setResolutionPrefix(String id_resolution_prefix) {
        this.id_resolution_prefix = id_resolution_prefix;
    }

    @Column(name = "id_deprecated")
    public Boolean getIsDeprecated() {
        return id_deprecated;
    }

    public void setIsDeprecated(Boolean id_deprecated) {
        this.id_deprecated = id_deprecated;
    }

    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "identifier_type_seq")
    @SequenceGenerator(name = "identifier_type_seq", sequenceName = "identifier_type_seq")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_source_id")
    public ClientDetailsEntity getSourceClient() {
        return sourceClient;
    }

    public void setSourceClient(ClientDetailsEntity sourceClient) {
        this.sourceClient = sourceClient;
    }
    
    @Column(name = "primary_use")
    public String getPrimaryUse() {
        return primary_use;
    }

    public void setPrimaryUse(String primary_use) {
        this.primary_use = primary_use;
    }
    
    @Column(name = "`case_sensitive`")
    //this is in single backslashes to make it work with HSQLDB
    public Boolean getIsCaseSensitive() {
        return case_sensitive;
    }

    public void setIsCaseSensitive(Boolean case_sensitive) {
        this.case_sensitive = case_sensitive;
    }

}
