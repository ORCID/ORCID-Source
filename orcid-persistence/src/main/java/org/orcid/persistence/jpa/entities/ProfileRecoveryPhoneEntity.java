package org.orcid.persistence.jpa.entities;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/**
 * The 2FA recovery phone number for a record.
 *
 * The plain number is never persisted: only a salted one way hash of the E.164
 * form plus the last four digits, which are all that is ever displayed back to
 * the user. To act on the number the user has to supply it again so it can be
 * hashed and compared.
 */
@Entity
@Table(name = "profile_recovery_phone")
public class ProfileRecoveryPhoneEntity extends BaseEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String orcid;

    private String hashedPhoneNumber;

    private String lastFour;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "profile_recovery_phone_seq")
    @SequenceGenerator(name = "profile_recovery_phone_seq", sequenceName = "profile_recovery_phone_seq", allocationSize = 1)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "orcid", length = 19)
    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    @Column(name = "hashed_phone_number")
    public String getHashedPhoneNumber() {
        return hashedPhoneNumber;
    }

    public void setHashedPhoneNumber(String hashedPhoneNumber) {
        this.hashedPhoneNumber = hashedPhoneNumber;
    }

    @Column(name = "last_four", length = 4)
    public String getLastFour() {
        return lastFour;
    }

    public void setLastFour(String lastFour) {
        this.lastFour = lastFour;
    }

}
