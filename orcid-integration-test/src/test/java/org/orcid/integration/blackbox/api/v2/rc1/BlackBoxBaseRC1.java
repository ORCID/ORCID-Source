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
package org.orcid.integration.blackbox.api.v2.rc1;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.orcid.integration.blackbox.api.BlackBoxBase;
import org.orcid.integration.blackbox.api.v12.T2OAuthAPIService;
import org.orcid.jaxb.model.record_rc1.Education;
import org.orcid.jaxb.model.record_rc1.Employment;
import org.orcid.jaxb.model.record_rc1.Funding;
import org.orcid.jaxb.model.record_rc1.PeerReview;
import org.orcid.jaxb.model.record_rc1.Work;

import com.sun.jersey.api.client.ClientResponse;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class BlackBoxBaseRC1 extends BlackBoxBase {
    @Resource(name = "t2OAuthClient")
    protected T2OAuthAPIService<ClientResponse> t2OAuthClient;
    @Resource(name = "memberV2ApiClient_rc1")
    protected MemberV2ApiClientImpl memberV2ApiClient;   

    public Object unmarshallFromPath(String path, Class<?> type) {
        try (Reader reader = new InputStreamReader(getClass().getResourceAsStream(path))) {
            Object obj = unmarshall(reader, type);
            Object result = null;
            if (Education.class.equals(type)) {
                result = (Education) obj;
            } else if (Employment.class.equals(type)) {
                result = (Employment) obj;
            } else if (Funding.class.equals(type)) {
                result = (Funding) obj;
            } else if (Work.class.equals(type)) {
                result = (Work) obj;
            } else if (PeerReview.class.equals(type)) {
                result = (PeerReview) obj;
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
}
