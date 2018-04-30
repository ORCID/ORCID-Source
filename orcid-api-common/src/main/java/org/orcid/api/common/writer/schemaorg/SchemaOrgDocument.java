package org.orcid.api.common.writer.schemaorg;

import java.util.List;
import java.util.Set;

import org.apache.jena.ext.com.google.common.collect.Lists;
import org.apache.jena.ext.com.google.common.collect.Sets;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({ "context", "type", "id", "mainEntityOfPage", "name", "givenName", "familyName", "alternateName", "address", "alumniOf", "affiliation",
        "worksAndFunding", "url", "identifier" })
public class SchemaOrgDocument {

    @JsonProperty("@context")
    public String context = "http://schema.org";
    @JsonProperty("@type")
    public String type = "Person";
    @JsonProperty("@id")
    public String id;
    public List<SchemaOrgExternalID> identifier = Lists.newArrayList();
    public String mainEntityOfPage;// same as ID;
    public String name;
    public String givenName;
    public String familyName;
    public List<String> alternateName = Lists.newArrayList();
    public List<SchemaOrgAddress> address = Lists.newArrayList();
    public Set<SchemaOrgAffiliation> alumniOf = Sets.newLinkedHashSet();
    public Set<SchemaOrgAffiliation> affiliation = Sets.newLinkedHashSet(); // non-education
    public List<String> url = Lists.newArrayList(); // webpages
    @JsonProperty("@reverse")
    public SchemaOrgReverse worksAndFunding = new SchemaOrgReverse();

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((address == null) ? 0 : address.hashCode());
        result = prime * result + ((affiliation == null) ? 0 : affiliation.hashCode());
        result = prime * result + ((alternateName == null) ? 0 : alternateName.hashCode());
        result = prime * result + ((alumniOf == null) ? 0 : alumniOf.hashCode());
        result = prime * result + ((context == null) ? 0 : context.hashCode());
        result = prime * result + ((familyName == null) ? 0 : familyName.hashCode());
        result = prime * result + ((givenName == null) ? 0 : givenName.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
        result = prime * result + ((mainEntityOfPage == null) ? 0 : mainEntityOfPage.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        result = prime * result + ((worksAndFunding == null) ? 0 : worksAndFunding.hashCode());
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
        SchemaOrgDocument other = (SchemaOrgDocument) obj;
        if (address == null) {
            if (other.address != null)
                return false;
        } else if (!address.equals(other.address))
            return false;
        if (affiliation == null) {
            if (other.affiliation != null)
                return false;
        } else if (!affiliation.equals(other.affiliation))
            return false;
        if (alternateName == null) {
            if (other.alternateName != null)
                return false;
        } else if (!alternateName.equals(other.alternateName))
            return false;
        if (alumniOf == null) {
            if (other.alumniOf != null)
                return false;
        } else if (!alumniOf.equals(other.alumniOf))
            return false;
        if (context == null) {
            if (other.context != null)
                return false;
        } else if (!context.equals(other.context))
            return false;
        if (familyName == null) {
            if (other.familyName != null)
                return false;
        } else if (!familyName.equals(other.familyName))
            return false;
        if (givenName == null) {
            if (other.givenName != null)
                return false;
        } else if (!givenName.equals(other.givenName))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (identifier == null) {
            if (other.identifier != null)
                return false;
        } else if (!identifier.equals(other.identifier))
            return false;
        if (mainEntityOfPage == null) {
            if (other.mainEntityOfPage != null)
                return false;
        } else if (!mainEntityOfPage.equals(other.mainEntityOfPage))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        if (url == null) {
            if (other.url != null)
                return false;
        } else if (!url.equals(other.url))
            return false;
        if (worksAndFunding == null) {
            if (other.worksAndFunding != null)
                return false;
        } else if (!worksAndFunding.equals(other.worksAndFunding))
            return false;
        return true;
    }

    /** Address contains a simple country code
     * 
     * @author tom
     *
     */
    public static class SchemaOrgAddress {
        @JsonProperty("@type")
        public String type = "PostalAddress";
        public String addressCountry;

        public SchemaOrgAddress() {
        }

        public SchemaOrgAddress(String countryCode) {
            this.addressCountry = countryCode;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((addressCountry == null) ? 0 : addressCountry.hashCode());
            result = prime * result + ((type == null) ? 0 : type.hashCode());
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
            SchemaOrgAddress other = (SchemaOrgAddress) obj;
            if (addressCountry == null) {
                if (other.addressCountry != null)
                    return false;
            } else if (!addressCountry.equals(other.addressCountry))
                return false;
            if (type == null) {
                if (other.type != null)
                    return false;
            } else if (!type.equals(other.type))
                return false;
            return true;
        }
        
    }

    /** Container for reverse properties (created, funded)
     * 
     * @author tom
     *
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonPropertyOrder({ "funder", "creator" })
    public static class SchemaOrgReverse {
        public List<SchemaOrgWork> creator = Lists.newArrayList();
        public List<SchemaOrgAffiliation> funder = Lists.newArrayList();
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((creator == null) ? 0 : creator.hashCode());
            result = prime * result + ((funder == null) ? 0 : funder.hashCode());
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
            SchemaOrgReverse other = (SchemaOrgReverse) obj;
            if (creator == null) {
                if (other.creator != null)
                    return false;
            } else if (!creator.equals(other.creator))
                return false;
            if (funder == null) {
                if (other.funder != null)
                    return false;
            } else if (!funder.equals(other.funder))
                return false;
            return true;
        }
        
    }

    /** A Work.  Three different places to put identifiers:
     * - '@id' a doi URL if available
     * - 'sameAs' other URL identifiers
     * - 'identifiers' non-url identifiers (including non-url forms of sameAs/DOI
     * 
     * @author tom
     *
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonPropertyOrder({ "type", "id", "name", "identifier" })
    public static class SchemaOrgWork {
        @JsonProperty("@type")
        public String type = "CreativeWork"; // or dataset etc...
        @JsonProperty("@id")
        public String id;// the doi if available
        public String name;
        public Set<SchemaOrgExternalID> identifier;
        public Set<String> sameAs; // for id urls

        public SchemaOrgWork() {
            identifier = Sets.newLinkedHashSet();
            sameAs = Sets.newLinkedHashSet();
        }

        public SchemaOrgWork(String id, String name, Set<String> sameAs, SchemaOrgExternalID... identifier) {
            super();
            this.id = id;
            this.name = name;
            this.identifier = Sets.newLinkedHashSet(Lists.newArrayList(identifier));
            this.sameAs = sameAs;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((id == null) ? 0 : id.hashCode());
            result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            result = prime * result + ((sameAs == null) ? 0 : sameAs.hashCode());
            result = prime * result + ((type == null) ? 0 : type.hashCode());
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
            SchemaOrgWork other = (SchemaOrgWork) obj;
            if (id == null) {
                if (other.id != null)
                    return false;
            } else if (!id.equals(other.id))
                return false;
            if (identifier == null) {
                if (other.identifier != null)
                    return false;
            } else if (!identifier.equals(other.identifier))
                return false;
            if (name == null) {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
            if (sameAs == null) {
                if (other.sameAs != null)
                    return false;
            } else if (!sameAs.equals(other.sameAs))
                return false;
            if (type == null) {
                if (other.type != null)
                    return false;
            } else if (!type.equals(other.type))
                return false;
            return true;
        }
        
    }

    /** An affiliation (link to an org)
     * - '@id' a lei/grid/fundref URL if available
     * - 'sameAs' other URL identifiers
     * - 'identifiers' non-url identifiers (ringgold)
     * 
     * @author tom
     *
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonPropertyOrder({ "type", "id", "leiCode", "name", "alternateName", "identifier" })
    public static class SchemaOrgAffiliation {
        @JsonProperty("@type")
        public final String type = "Organization";
        @JsonProperty("@id")
        public String id;// the grid/lei/fundref if available
        public String name; // the org
        public String alternateName; // the specific grant/job/degree
        public Set<SchemaOrgExternalID> identifier = Sets.newLinkedHashSet();
        public String leiCode;
        public Set<String> sameAs = Sets.newLinkedHashSet(); // id are urls

        public SchemaOrgAffiliation() {
        }

        public SchemaOrgAffiliation(String resolvableID, String name, String alternateName, String leiCode, SchemaOrgExternalID... otherIdentifiers) {
            this.id = resolvableID;
            this.leiCode = leiCode;
            this.name = name;
            this.alternateName = alternateName;
            for (SchemaOrgExternalID id : otherIdentifiers) {
                if (id.value.startsWith("http"))
                    sameAs.add(id.value);
                else
                    this.identifier.add(id);
            }
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((alternateName == null) ? 0 : alternateName.hashCode());
            result = prime * result + ((id == null) ? 0 : id.hashCode());
            result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
            result = prime * result + ((leiCode == null) ? 0 : leiCode.hashCode());
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            result = prime * result + ((sameAs == null) ? 0 : sameAs.hashCode());
            result = prime * result + ((type == null) ? 0 : type.hashCode());
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
            SchemaOrgAffiliation other = (SchemaOrgAffiliation) obj;
            if (alternateName == null) {
                if (other.alternateName != null)
                    return false;
            } else if (!alternateName.equals(other.alternateName))
                return false;
            if (id == null) {
                if (other.id != null)
                    return false;
            } else if (!id.equals(other.id))
                return false;
            if (identifier == null) {
                if (other.identifier != null)
                    return false;
            } else if (!identifier.equals(other.identifier))
                return false;
            if (leiCode == null) {
                if (other.leiCode != null)
                    return false;
            } else if (!leiCode.equals(other.leiCode))
                return false;
            if (name == null) {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
            if (sameAs == null) {
                if (other.sameAs != null)
                    return false;
            } else if (!sameAs.equals(other.sameAs))
                return false;
            if (type == null) {
                if (other.type != null)
                    return false;
            } else if (!type.equals(other.type))
                return false;
            return true;
        }

    }

    /** An ID (non-url)
     * 
     * @author tom
     *
     */
    @JsonPropertyOrder({ "type", "propertyID", "value" })
    public static class SchemaOrgExternalID {
        @JsonProperty("@type")
        public final String type = "PropertyValue";
        public String propertyID;
        public String value;

        public SchemaOrgExternalID() {
        }

        public SchemaOrgExternalID(String propertyID, String value) {
            this.propertyID = propertyID;
            this.value = value;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((propertyID == null) ? 0 : propertyID.hashCode());
            result = prime * result + ((type == null) ? 0 : type.hashCode());
            result = prime * result + ((value == null) ? 0 : value.hashCode());
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
            SchemaOrgExternalID other = (SchemaOrgExternalID) obj;
            if (propertyID == null) {
                if (other.propertyID != null)
                    return false;
            } else if (!propertyID.equals(other.propertyID))
                return false;
            if (type == null) {
                if (other.type != null)
                    return false;
            } else if (!type.equals(other.type))
                return false;
            if (value == null) {
                if (other.value != null)
                    return false;
            } else if (!value.equals(other.value))
                return false;
            return true;
        }
    }
}
