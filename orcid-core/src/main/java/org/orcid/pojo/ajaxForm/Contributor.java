package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.orcid.jaxb.model.v3.rc2.common.ContributorEmail;
import org.orcid.jaxb.model.v3.rc2.common.ContributorOrcid;
import org.orcid.jaxb.model.v3.rc2.common.CreditName;
import org.orcid.jaxb.model.v3.rc2.record.FundingContributor;
import org.orcid.jaxb.model.v3.rc2.record.FundingContributorAttributes;
import org.orcid.jaxb.model.v3.rc2.record.FundingContributorRole;

public class Contributor implements ErrorsInterface, Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();

    private Text contributorSequence;

    private Text email;

    private Text orcid;

    private Text uri;

    private Text creditName;

    private Text contributorRole;

    public static Contributor valueOf(org.orcid.jaxb.model.common_v2.Contributor contributor) {
        Contributor c = new Contributor();
        if (contributor != null) {
            if (contributor.getContributorAttributes() != null) {
                contributor.getContributorAttributes();
                if (contributor.getContributorAttributes().getContributorRole() != null)
                    c.setContributorRole(Text.valueOf(contributor.getContributorAttributes().getContributorRole().value()));
                if (contributor.getContributorAttributes().getContributorSequence() != null)
                    c.setContributorSequence(Text.valueOf(contributor.getContributorAttributes().getContributorSequence().value()));
            }
            if (contributor.getContributorOrcid() != null) {
                c.setOrcid(Text.valueOf(contributor.getContributorOrcid().getPath()));
                c.setUri(Text.valueOf(contributor.getContributorOrcid().getUri()));
            }
            //Set default values that must be overwritten by the controller
            if (contributor.getCreditName() != null) {
                c.setCreditName(Text.valueOf(contributor.getCreditName().getContent()));
            }
        }
        return c;
    }
    
    public static Contributor valueOf(org.orcid.jaxb.model.v3.rc2.common.Contributor contributor) {
        Contributor c = new Contributor();
        if (contributor != null) {
            if (contributor.getContributorAttributes() != null) {
                contributor.getContributorAttributes();
                if (contributor.getContributorAttributes().getContributorRole() != null)
                    c.setContributorRole(Text.valueOf(contributor.getContributorAttributes().getContributorRole().value()));
                if (contributor.getContributorAttributes().getContributorSequence() != null)
                    c.setContributorSequence(Text.valueOf(contributor.getContributorAttributes().getContributorSequence().value()));
            }
            if (contributor.getContributorOrcid() != null) {
                c.setOrcid(Text.valueOf(contributor.getContributorOrcid().getPath()));
                c.setUri(Text.valueOf(contributor.getContributorOrcid().getUri()));
            }
            //Set default values that must be overwritten by the controller
            if (contributor.getCreditName() != null) {
                c.setCreditName(Text.valueOf(contributor.getCreditName().getContent()));
            }
        }
        return c;
    }
    
    public static Contributor valueOf(FundingContributor contributor) {
        Contributor c = new Contributor();
        if (contributor != null) {
            if (contributor.getContributorAttributes() != null) {
                contributor.getContributorAttributes();
                if (contributor.getContributorAttributes().getContributorRole() != null)
                    c.setContributorRole(Text.valueOf(contributor.getContributorAttributes().getContributorRole().value()));
            }
            if (contributor.getContributorOrcid() != null) {
                c.setOrcid(Text.valueOf(contributor.getContributorOrcid().getPath()));
                c.setUri(Text.valueOf(contributor.getContributorOrcid().getUri()));
            }
            if (contributor.getCreditName() != null) {
                c.setCreditName(Text.valueOf(contributor.getCreditName().getContent()));
            }
        }
        return c;
    }   
    
    @Deprecated
    public static Contributor valueOf(org.orcid.jaxb.model.message.FundingContributor contributor) {
        Contributor c = new Contributor();
        if (contributor != null) {
            if (contributor.getContributorAttributes() != null) {
                contributor.getContributorAttributes();
                if (contributor.getContributorAttributes().getContributorRole() != null)
                    c.setContributorRole(Text.valueOf(contributor.getContributorAttributes().getContributorRole().value()));
            }
            if (contributor.getContributorEmail() != null)
                c.setEmail(Text.valueOf(contributor.getContributorEmail().getValue()));
            if (contributor.getContributorOrcid() != null) {
                c.setOrcid(Text.valueOf(contributor.getContributorOrcid().getPath()));
                c.setUri(Text.valueOf(contributor.getContributorOrcid().getUri()));
            }
            if (contributor.getCreditName() != null) {
                c.setCreditName(Text.valueOf(contributor.getCreditName().getContent()));
            }
        }
        return c;
    }  
                    
    public FundingContributor toFundingContributor() {
        FundingContributor c = new FundingContributor();
        if (this.getContributorRole() != null || this.getContributorSequence() != null) {
            FundingContributorAttributes ca = new FundingContributorAttributes();
            if (!PojoUtil.isEmpty(this.getContributorRole()))
                ca.setContributorRole(FundingContributorRole.fromValue(this.getContributorRole().getValue()));
            c.setContributorAttributes(ca);
        }
        if (this.getEmail() != null)
            c.setContributorEmail(new ContributorEmail(this.getEmail().getValue()));
        if (this.getOrcid() != null) {
            ContributorOrcid contributorOrcid = new ContributorOrcid(this.getOrcid().getValue());
            if (this.getUri() != null) {
                String uriString = this.getUri().getValue();
                if (StringUtils.isNotBlank(uriString)) {
                    try {
                        URI uri = new URI(uriString);
                        contributorOrcid.setHost(uri.getHost());
                    } catch (URISyntaxException e) {
                        throw new RuntimeException("Problem parsing contributor orcid uri", e);
                    }
                }
            }
            contributorOrcid.setUri(this.getUri().getValue());

            c.setContributorOrcid(contributorOrcid);
        }
        if (this.getCreditName() != null) {
            CreditName cn = new CreditName(this.getCreditName().getValue());
            c.setCreditName(cn);
        }
        return c;
    }
    
    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public Text getContributorSequence() {
        return contributorSequence;
    }

    public void setContributorSequence(Text contributorSequence) {
        this.contributorSequence = contributorSequence;
    }

    public Text getContributorRole() {
        return contributorRole;
    }

    public void setContributorRole(Text contributorRole) {
        this.contributorRole = contributorRole;
    }

    public Text getEmail() {
        return email;
    }

    public void setEmail(Text email) {
        this.email = email;
    }

    public Text getOrcid() {
        return orcid;
    }

    public void setOrcid(Text orcid) {
        this.orcid = orcid;
    }

    public Text getUri() {
        return uri;
    }

    public void setUri(Text uri) {
        this.uri = uri;
    }

    public Text getCreditName() {
        return creditName;
    }

    public void setCreditName(Text creditName) {
        this.creditName = creditName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((contributorRole == null) ? 0 : contributorRole.hashCode());
        result = prime * result + ((contributorSequence == null) ? 0 : contributorSequence.hashCode());
        result = prime * result + ((creditName == null) ? 0 : creditName.hashCode());
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        result = prime * result + ((errors == null) ? 0 : errors.hashCode());
        result = prime * result + ((orcid == null) ? 0 : orcid.hashCode());
        result = prime * result + ((uri == null) ? 0 : uri.hashCode());
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
        Contributor other = (Contributor) obj;
        if (contributorRole == null) {
            if (other.contributorRole != null)
                return false;
        } else if (!contributorRole.equals(other.contributorRole))
            return false;
        if (contributorSequence == null) {
            if (other.contributorSequence != null)
                return false;
        } else if (!contributorSequence.equals(other.contributorSequence))
            return false;
        if (creditName == null) {
            if (other.creditName != null)
                return false;
        } else if (!creditName.equals(other.creditName))
            return false;
        if (email == null) {
            if (other.email != null)
                return false;
        } else if (!email.equals(other.email))
            return false;
        if (errors == null) {
            if (other.errors != null)
                return false;
        } else if (!errors.equals(other.errors))
            return false;
        if (orcid == null) {
            if (other.orcid != null)
                return false;
        } else if (!orcid.equals(other.orcid))
            return false;
        if (uri == null) {
            if (other.uri != null)
                return false;
        } else if (!uri.equals(other.uri))
            return false;
        return true;
    }    
}
