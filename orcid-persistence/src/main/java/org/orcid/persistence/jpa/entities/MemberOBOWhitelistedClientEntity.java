package org.orcid.persistence.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "member_obo_whitelisted_client")
public class MemberOBOWhitelistedClientEntity extends BaseEntity<Long> {

    private static final long serialVersionUID = 1L;

    private Long id;
    
    private ClientDetailsEntity clientDetailsEntity;
    
    private ClientDetailsEntity whitelistedClientDetailsEntity;
    
    @Id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    @ManyToOne
    @JoinColumn(name = "client_details_id")
    public ClientDetailsEntity getClientDetailsEntity() {
        return clientDetailsEntity;
    }

    public void setClientDetailsEntity(ClientDetailsEntity clientDetailsEntity) {
        this.clientDetailsEntity = clientDetailsEntity;
    }

    @ManyToOne
    @JoinColumn(name = "whitelisted_client_details_id")
    public ClientDetailsEntity getWhitelistedClientDetailsEntity() {
        return whitelistedClientDetailsEntity;
    }

    public void setWhitelistedClientDetailsEntity(ClientDetailsEntity whitelistedClientDetailsEntity) {
        this.whitelistedClientDetailsEntity = whitelistedClientDetailsEntity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        MemberOBOWhitelistedClientEntity that = (MemberOBOWhitelistedClientEntity) o;

        if (!clientDetailsEntity.getId().equals(that.clientDetailsEntity.getId())) {
            return false;
        }
        
        if (!whitelistedClientDetailsEntity.getId().equals(that.whitelistedClientDetailsEntity.getId())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = 31 * clientDetailsEntity.getId().hashCode();
        result += 31 * whitelistedClientDetailsEntity.getId().hashCode();
        return result;
    }

}
