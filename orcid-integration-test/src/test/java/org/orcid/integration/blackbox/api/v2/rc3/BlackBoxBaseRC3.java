/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.integration.blackbox.api.v2.rc3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.orcid.integration.blackbox.api.BlackBoxBase;
import org.orcid.integration.blackbox.api.v12.T2OAuthAPIService;
import org.orcid.jaxb.model.common_rc3.Url;
import org.orcid.jaxb.model.record_rc3.Address;
import org.orcid.jaxb.model.record_rc3.Education;
import org.orcid.jaxb.model.record_rc3.Employment;
import org.orcid.jaxb.model.record_rc3.Funding;
import org.orcid.jaxb.model.record_rc3.Keyword;
import org.orcid.jaxb.model.record_rc3.OtherName;
import org.orcid.jaxb.model.record_rc3.PeerReview;
import org.orcid.jaxb.model.record_rc3.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_rc3.PersonalDetails;
import org.orcid.jaxb.model.record_rc3.Relationship;
import org.orcid.jaxb.model.record_rc3.ResearcherUrl;
import org.orcid.jaxb.model.record_rc3.Work;

import com.sun.jersey.api.client.ClientResponse;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class BlackBoxBaseRC3 extends BlackBoxBase {
    @Resource(name = "t2OAuthClient")
    protected T2OAuthAPIService<ClientResponse> t2OAuthClient;
    @Resource(name = "memberV2ApiClient_rc3")
    protected MemberV2ApiClientImpl memberV2ApiClient;    
    
    protected List<Long> newOtherNames = new ArrayList<Long>();
    
    public Object unmarshallFromPath(String path, Class<?> type) {
        try (Reader reader = new InputStreamReader(getClass().getResourceAsStream(path))) {
            Object obj = unmarshall(reader, type);
            Object result = null;
            if (Address.class.equals(type)) {
                result = (Address) obj;
            } else if (Education.class.equals(type)) {
                result = (Education) obj;
            } else if (Employment.class.equals(type)) {
                result = (Employment) obj;
            } else if (Funding.class.equals(type)) {
                result = (Funding) obj;               
            } else if(Keyword.class.equals(type)) {
                result = (Keyword) obj;
            } else if (Work.class.equals(type)) {
                result = (Work) obj;
            } else if (PeerReview.class.equals(type)) {
                result = (PeerReview) obj;
            } else if(ResearcherUrl.class.equals(type)) {
                result = (ResearcherUrl) obj;
            } else if(PersonalDetails.class.equals(type)) {
                result = (PersonalDetails) obj;
            } else if(OtherName.class.equals(type)) {
                result = (OtherName) obj;
            } else if(PersonExternalIdentifier.class.equals(type)) {
                result = (PersonExternalIdentifier) obj;
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Error reading notification from classpath", e);
        }
    }

    public Object unmarshall(Reader reader, Class<?> type) {
        try {
            JAXBContext context = JAXBContext.newInstance(type);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return unmarshaller.unmarshal(reader);
        } catch (JAXBException e) {
            throw new RuntimeException("Unable to unmarshall orcid message" + e);
        }
    }   
    
    @SuppressWarnings({ "deprecation", "rawtypes" })
    public Long createOtherName(String value, String userOrcid, String accessToken) {
        OtherName otherName = new OtherName();
        otherName.setContent(value);
        ClientResponse response = memberV2ApiClient.createOtherName(userOrcid, otherName, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.CREATED.getStatusCode(), response.getStatus());
        Map map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List resultWithPutCode = (List) map.get("Location");
        String location = resultWithPutCode.get(0).toString();
        Long putCode = Long.valueOf(location.substring(location.lastIndexOf('/') + 1));               
        return putCode;
    }   
    
    /**
     * EXTERNAL IDENTIFIERS
     * 
     * External identifiers can't be added through the UI
     * */
    public Long createExternalIdentifier(String value, String userOrcid, String accessToken) {
        PersonExternalIdentifier e = new PersonExternalIdentifier();
        e.setValue(value);
        e.setType("test");
        e.setUrl(new Url("http://test.orcid.org"));
        e.setRelationship(Relationship.SELF);
        ClientResponse response = memberV2ApiClient.createExternalIdentifier(userOrcid, e, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.CREATED.getStatusCode(), response.getStatus());
        return getPutCodeFromResponse(response);                       
    }   
}
