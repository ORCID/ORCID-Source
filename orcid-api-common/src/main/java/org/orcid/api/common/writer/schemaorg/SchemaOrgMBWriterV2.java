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
import org.orcid.jaxb.model.record.summary_v2.EducationSummary;
import org.orcid.jaxb.model.record.summary_v2.EmploymentSummary;
import org.orcid.jaxb.model.record.summary_v2.FundingGroup;
import org.orcid.jaxb.model.record.summary_v2.WorkGroup;
import org.orcid.jaxb.model.record_v2.Address;
import org.orcid.jaxb.model.record_v2.ExternalID;
import org.orcid.jaxb.model.record_v2.OtherName;
import org.orcid.jaxb.model.record_v2.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_v2.Record;
import org.orcid.jaxb.model.record_v2.ResearcherUrl;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Provider
@Produces({ JSON_LD })
@Component
public class SchemaOrgMBWriterV2 implements MessageBodyWriter<Record> {

    @Resource
    NormalizationService norm;

    private ObjectMapper objectMapper;

    public SchemaOrgMBWriterV2() {
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

            //educations
            if (r.getActivitiesSummary().getEducations() != null && r.getActivitiesSummary().getEducations().getSummaries() != null)
                for (EducationSummary e : r.getActivitiesSummary().getEducations().getSummaries()) {
                    if (e.getOrganization() != null && e.getOrganization().getDisambiguatedOrganization() != null)
                        doc.alumniOf.add(createOrg(
                                e.getOrganization().getName(), 
                                e.getDepartmentName(),
                                e.getOrganization().getDisambiguatedOrganization().getDisambiguationSource(),
                                e.getOrganization().getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier(),
                                e.getOrganization().getDisambiguatedOrganization().getExternalIdentifiers()
                                ));
                }

            //empoyments
            if (r.getActivitiesSummary().getEmployments() != null && r.getActivitiesSummary().getEmployments().getSummaries() != null)
                for (EmploymentSummary e : r.getActivitiesSummary().getEmployments().getSummaries()) {
                    if (e.getOrganization() != null && e.getOrganization().getDisambiguatedOrganization() != null)
                        doc.affiliation.add(createOrg(
                                e.getOrganization().getName(), 
                                e.getDepartmentName(),
                                e.getOrganization().getDisambiguatedOrganization().getDisambiguationSource(),
                                e.getOrganization().getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier(),
                                e.getOrganization().getDisambiguatedOrganization().getExternalIdentifiers()
                                ));
                }

            //funding
            if (r.getActivitiesSummary().getFundings() != null && r.getActivitiesSummary().getFundings().getFundingGroup() != null)
                for (FundingGroup e : r.getActivitiesSummary().getFundings().getFundingGroup()) {
                    // funding includes org ids and grant_ids
                    if (e.getFundingSummary().get(0) != null && e.getFundingSummary().get(0).getOrganization() != null
                            && e.getFundingSummary().get(0).getOrganization().getDisambiguatedOrganization() != null) {
                        SchemaOrgAffiliation a = createOrg(
                                e.getFundingSummary().get(0).getOrganization().getName(),
                                e.getFundingSummary().get(0).getTitle().getTitle().getContent(),
                                e.getFundingSummary().get(0).getOrganization().getDisambiguatedOrganization().getDisambiguationSource(),
                                e.getFundingSummary().get(0).getOrganization().getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier(),
                                e.getFundingSummary().get(0).getOrganization().getDisambiguatedOrganization().getExternalIdentifiers());
                        if (e.getIdentifiers() != null && e.getIdentifiers().getExternalIdentifier() != null)
                            for (ExternalID id : e.getIdentifiers().getExternalIdentifier()) {
                                a.identifier.add(new SchemaOrgExternalID(id.getType(), id.getValue()));
                            }
                        doc.worksAndFunding.funder.add(a);
                    }
                }

            //works
            if (r.getActivitiesSummary().getWorks() != null && r.getActivitiesSummary().getWorks().getWorkGroup() != null)
                for (WorkGroup wg : r.getActivitiesSummary().getWorks().getWorkGroup()) {
                    SchemaOrgWork sw = new SchemaOrgWork();
                    if (wg.getWorkSummary().get(0) != null && wg.getWorkSummary().get(0).getTitle() != null && wg.getWorkSummary().get(0).getTitle().getTitle() != null)
                        sw.name = wg.getWorkSummary().get(0).getTitle().getTitle().getContent();

                    if (wg.getIdentifiers() != null && wg.getIdentifiers().getExternalIdentifier() != null)
                        for (ExternalID id : wg.getIdentifiers().getExternalIdentifier()) {
                            String normed = norm.normalise(id.getType(), id.getValue());
                            if (!StringUtils.isEmpty(normed)){
                                // add all ids (inc doi non-url if available)
                                sw.identifier.add(new SchemaOrgExternalID(id.getType(), normed));
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

    private SchemaOrgAffiliation createOrg(String name, String alternateName, String mainIdSource, String mainIdValue, List<DisambiguatedOrganizationExternalIdentifier> ids) {
        SchemaOrgAffiliation a = new SchemaOrgAffiliation();
        a.name = name;
        a.alternateName = alternateName;
        if (!StringUtils.isEmpty(mainIdSource) && !StringUtils.isEmpty(mainIdValue))
            addIdToAffiliation(mainIdSource,mainIdValue,a);
        if (ids != null)
            for (DisambiguatedOrganizationExternalIdentifier i : ids) {
                addIdToAffiliation(i.getIdentifierType(),i.getIdentifier(),a);
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
