package org.orcid.api.common.writer.schemaorg;

import static org.orcid.core.api.OrcidApiConstants.JSON_LD;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;
import org.orcid.api.common.writer.schemaorg.SchemaOrgDocument.SchemaOrgAddress;
import org.orcid.api.common.writer.schemaorg.SchemaOrgDocument.SchemaOrgAffiliation;
import org.orcid.api.common.writer.schemaorg.SchemaOrgDocument.SchemaOrgExternalID;
import org.orcid.api.common.writer.schemaorg.SchemaOrgDocument.SchemaOrgWork;
import org.orcid.core.utils.v3.identifiers.NormalizationService;
import org.orcid.jaxb.model.common_v2.DisambiguatedOrganizationExternalIdentifier;
import org.orcid.jaxb.model.v3.dev1.record.Address;
import org.orcid.jaxb.model.v3.dev1.record.ExternalID;
import org.orcid.jaxb.model.v3.dev1.record.OtherName;
import org.orcid.jaxb.model.v3.dev1.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.dev1.record.Record;
import org.orcid.jaxb.model.v3.dev1.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.dev1.record.summary.AffiliationSummary;
import org.orcid.jaxb.model.v3.dev1.record.summary.FundingGroup;
import org.orcid.jaxb.model.v3.dev1.record.summary.FundingSummary;
import org.orcid.jaxb.model.v3.dev1.record.summary.WorkGroup;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.Lists;

//redirect don't resolve directly.
@Provider
@Produces({ JSON_LD })
@Component
public class SchemaOrgMBWriterV3 implements MessageBodyWriter<Record> {

    @Resource
    NormalizationService norm;

    private ObjectMapper objectMapper;

    public SchemaOrgMBWriterV3() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED, true);
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return Record.class.isAssignableFrom(type);
    }

    @Override
    public long getSize(Record t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    /** Converts a v2Record into a SchemaOrgDocument 
     * requires lots of tedious null checking but logic is simple.
     * For works, 
     * - uses DOI for '@id' if available.  
     * - Puts all ExternalIDs into 'identifiers', normalizing if possible.
     * - Puts all ExternalID urls into 'sameAs', normalizing if possible.
     * For Orgs
     * - Uses first found LEI, GRID or Fundref for '@id'
     * - Puts other LEI, GRID or Fundref URLs into 'sameAs'
     * - Puts RINGGOLD into 'identifiers'.
     * 
     */
    @Override
    public void writeTo(Record r, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
            OutputStream entityStream) throws IOException, WebApplicationException {

        SchemaOrgDocument doc = new SchemaOrgDocument();
        doc.id = r.getOrcidIdentifier().getUri();
        doc.mainEntityOfPage = doc.id;

        // names
        if (r.getPerson().getName() != null) {
            if (r.getPerson().getName().getCreditName() != null)
                doc.name = r.getPerson().getName().getCreditName().getContent();
            if (r.getPerson().getName().getGivenNames() != null)
                doc.givenName = r.getPerson().getName().getGivenNames().getContent();
            if (r.getPerson().getName().getFamilyName() != null)
                doc.familyName = r.getPerson().getName().getFamilyName().getContent();
        }

        if (r.getPerson().getOtherNames() != null && r.getPerson().getOtherNames().getOtherNames() != null)
            for (OtherName n : r.getPerson().getOtherNames().getOtherNames()) {
                doc.alternateName.add(n.getContent());
            }

        // country
        if (r.getPerson().getAddresses() != null && r.getPerson().getAddresses().getAddress() != null)
            for (Address a : r.getPerson().getAddresses().getAddress()) {
                if (a.getCountry() != null && a.getCountry().getValue() !=null)
                    doc.address.add(new SchemaOrgAddress(a.getCountry().getValue().toString()));
            }
        
        // activities
        if (r.getActivitiesSummary() != null) {

            //education & qualification
            List<AffiliationSummary> alumniOf = Lists.newArrayList();
            if (r.getActivitiesSummary().getEducations() != null && r.getActivitiesSummary().getEducations().getSummaries() != null)
                alumniOf.addAll(r.getActivitiesSummary().getEducations().getSummaries());
            if (r.getActivitiesSummary().getQualifications() != null && r.getActivitiesSummary().getQualifications().getSummaries() != null)
                alumniOf.addAll(r.getActivitiesSummary().getQualifications().getSummaries());            
            for (AffiliationSummary e : alumniOf) {
                if (e.getOrganization() != null && e.getOrganization().getDisambiguatedOrganization() != null)
                    doc.alumniOf.add(createOrg(e));
            }

            //affiliations
            List<AffiliationSummary> affiliations = Lists.newArrayList();
            if (r.getActivitiesSummary().getEmployments() != null && r.getActivitiesSummary().getEmployments().getSummaries() != null)
                affiliations.addAll(r.getActivitiesSummary().getEmployments().getSummaries());
            if (r.getActivitiesSummary().getDistinctions() != null && r.getActivitiesSummary().getDistinctions().getSummaries() != null)
                affiliations.addAll(r.getActivitiesSummary().getDistinctions().getSummaries());
            if (r.getActivitiesSummary().getInvitedPositions() != null && r.getActivitiesSummary().getInvitedPositions().getSummaries() != null)
                affiliations.addAll(r.getActivitiesSummary().getInvitedPositions().getSummaries());
            if (r.getActivitiesSummary().getMemberships() != null && r.getActivitiesSummary().getMemberships().getSummaries() != null)
                affiliations.addAll(r.getActivitiesSummary().getMemberships().getSummaries());
            if (r.getActivitiesSummary().getServices() != null && r.getActivitiesSummary().getServices().getSummaries() != null)
                affiliations.addAll(r.getActivitiesSummary().getServices().getSummaries());                        
            for (AffiliationSummary a: affiliations){
                if (a.getOrganization() != null && a.getOrganization().getDisambiguatedOrganization() != null)
                    doc.affiliation.add(createOrg(a));
            }

            //funding
            if (r.getActivitiesSummary().getFundings() != null && r.getActivitiesSummary().getFundings().getFundingGroup() != null)
                for (FundingGroup e : r.getActivitiesSummary().getFundings().getFundingGroup()) {
                    // funding includes org ids and grant_ids
                    if (e.getFundingSummary().get(0) != null && e.getFundingSummary().get(0).getOrganization() != null
                            && e.getFundingSummary().get(0).getOrganization().getDisambiguatedOrganization() != null) {
                        doc.worksAndFunding.funder.add(createFundingOrg(e));
                    }
                }

            //work
            if (r.getActivitiesSummary().getWorks() != null && r.getActivitiesSummary().getWorks().getWorkGroup() != null)
                for (WorkGroup wg : r.getActivitiesSummary().getWorks().getWorkGroup()) {
                    SchemaOrgWork sw = new SchemaOrgWork();
                    if (wg.getWorkSummary().get(0) != null && wg.getWorkSummary().get(0).getTitle() != null && wg.getWorkSummary().get(0).getTitle().getTitle() != null)
                        sw.name = wg.getWorkSummary().get(0).getTitle().getTitle().getContent();

                    if (wg.getIdentifiers() != null && wg.getIdentifiers().getExternalIdentifier() != null)
                        for (ExternalID id : wg.getIdentifiers().getExternalIdentifier()) {
                            // add all ids (inc doi non-url if available)
                            sw.identifier.add(new SchemaOrgExternalID(id.getType(), norm.normalise(id.getType(), id.getValue())));
                            // sameAs for id URLs.
                            String url = norm.generateNormalisedURL(id.getType(), id.getValue());
                            if (StringUtils.isEmpty(url) && id.getUrl() != null)
                                url = id.getUrl().getValue();
                            //add first DOI as @id
                            if (id.getType().equals("doi") && !StringUtils.isEmpty(url) && sw.id == null)
                                sw.id = url;
                            else if (!StringUtils.isEmpty(url))
                                sw.sameAs.add(url);
                        }
                    doc.worksAndFunding.creator.add(sw);
                }
        }

        if (r.getPerson().getResearcherUrls() != null && r.getPerson().getResearcherUrls().getResearcherUrls() != null)
            for (ResearcherUrl u : r.getPerson().getResearcherUrls().getResearcherUrls()) {
                doc.url.add(u.getUrl().getValue());
            }

        if (r.getPerson().getExternalIdentifiers() != null && r.getPerson().getExternalIdentifiers().getExternalIdentifiers() != null)
            for (PersonExternalIdentifier i : r.getPerson().getExternalIdentifiers().getExternalIdentifiers()) {
                doc.identifier.add(new SchemaOrgExternalID(i.getType(), i.getValue()));
            }
        
        objectMapper.writer().writeValue(entityStream, doc);
    }

    private SchemaOrgAffiliation createFundingOrg(FundingGroup fundingGroup) {
        SchemaOrgAffiliation a = new SchemaOrgAffiliation();
        a.name = fundingGroup.getFundingSummary().get(0).getOrganization().getName();
        a.alternateName = fundingGroup.getFundingSummary().get(0).getTitle().getTitle().getContent();
        
        //add org ids
        for (FundingSummary s : fundingGroup.getFundingSummary()){
            if (s.getOrganization().getDisambiguatedOrganization() !=null){
                if (!StringUtils.isEmpty(s.getOrganization().getDisambiguatedOrganization().getDisambiguationSource()) && !StringUtils.isEmpty(s.getOrganization().getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier()))
                    addIdToAffiliation(s.getOrganization().getDisambiguatedOrganization().getDisambiguationSource(),s.getOrganization().getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier(), a);            
                if (s.getOrganization().getDisambiguatedOrganization().getExternalIdentifiers() != null)
                    for (DisambiguatedOrganizationExternalIdentifier i : s.getOrganization().getDisambiguatedOrganization().getExternalIdentifiers()) {
                        addIdToAffiliation(i.getIdentifierType(),i.getIdentifier(), a);
                }
            }
        }

       //add grant ids
       if (fundingGroup.getIdentifiers() != null && fundingGroup.getIdentifiers().getExternalIdentifier() != null)
           for (ExternalID id : fundingGroup.getIdentifiers().getExternalIdentifier())
               a.identifier.add(new SchemaOrgExternalID(id.getType(), norm.normalise(id.getType(), id.getValue())));
       
        return a;
    }

    private SchemaOrgAffiliation createOrg(AffiliationSummary e) {
        SchemaOrgAffiliation a = new SchemaOrgAffiliation();
        a.name = e.getOrganization().getName();
        a.alternateName = e.getDepartmentName();
        if (e.getOrganization().getDisambiguatedOrganization() !=null){
            if (!StringUtils.isEmpty(e.getOrganization().getDisambiguatedOrganization().getDisambiguationSource()) && !StringUtils.isEmpty(e.getOrganization().getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier())){
                addIdToAffiliation(e.getOrganization().getDisambiguatedOrganization().getDisambiguationSource(),e.getOrganization().getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier(), a);
            }   
            if (e.getOrganization().getDisambiguatedOrganization().getExternalIdentifiers() != null)
                for (DisambiguatedOrganizationExternalIdentifier i : e.getOrganization().getDisambiguatedOrganization().getExternalIdentifiers()) {
                    addIdToAffiliation(i.getIdentifierType(),i.getIdentifier(), a);
            }
        }
        return a;
    }
    
    private void addIdToAffiliation(String type, String value, SchemaOrgAffiliation a){
        if (type.equals("LEI")) {
            a.leiCode = value;
            if (a.id == null)
                a.id = "https://www.gleif.org/lei/" + value;
            else
                a.sameAs.add("https://www.gleif.org/lei/" + value);
        } else if (type.equals("FUNDREF")) {
            if (a.id == null)
                a.id = norm.generateNormalisedURL("doi", value);
            else
                a.sameAs.add(norm.generateNormalisedURL("doi", value));
        } else if (type.equals("GRID")) {
            if (a.id == null)
                a.id = value;
            else
                a.sameAs.add(value);
        } else {
            a.identifier.add(new SchemaOrgExternalID(type,value));
        }
    }

}
