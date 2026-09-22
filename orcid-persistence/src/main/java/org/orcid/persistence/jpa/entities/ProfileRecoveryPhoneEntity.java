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
 * The number is held in E.164 form, reversibly encrypted with the same
 * mechanism as the 2FA secret, alongside the last four digits, which are all
 * that is ever displayed back to the user. The encryption has to be reversible
 * because the Registry sends a text to the stored number without the user
 * re-typing it; the encrypted column is the only place the full number exists.
 */
@Entity
@Table(name = "profile_recovery_phone")
public class ProfileRecoveryPhoneEntity extends BaseEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String orcid;

    private String encryptedPhoneNumber;

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

    @Column(name = "encrypted_phone_number", nullable = false)
    public String getEncryptedPhoneNumber() {
        return encryptedPhoneNumber;
    }

    public void setEncryptedPhoneNumber(String encryptedPhoneNumber) {
        this.encryptedPhoneNumber = encryptedPhoneNumber;
    }

    @Column(name = "last_four", length = 4)
    public String getLastFour() {
        return lastFour;
    }

    public void setLastFour(String lastFour) {
        this.lastFour = lastFour;
    }

}
