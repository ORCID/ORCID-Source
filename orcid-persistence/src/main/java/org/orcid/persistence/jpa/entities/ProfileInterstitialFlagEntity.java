package org.orcid.persistence.jpa.entities;

import javax.persistence.*;
import java.util.Objects;

/**
 *
 * @author Andrej Romanov
 *
 */
@Entity
@Table(name = "profile_interstitial_flag")
public class ProfileInterstitialFlagEntity extends BaseEntity<Long>  {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String orcid;
    private String interstitialName;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "profile_interstitial_flag_seq")
    @SequenceGenerator(name = "profile_interstitial_flag_seq", sequenceName = "profile_interstitial_flag_seq", allocationSize = 1)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "orcid")
    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    @Column(name = "interstitial_name")
    public String getInterstitialName() {
        return interstitialName;
    }

    public void setInterstitialName(String interstitialName) {
        this.interstitialName = interstitialName;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ProfileInterstitialFlagEntity other = (ProfileInterstitialFlagEntity) obj;
        return Objects.equals(interstitialName, other.interstitialName) && Objects.equals(orcid, other.orcid);
    }

}
