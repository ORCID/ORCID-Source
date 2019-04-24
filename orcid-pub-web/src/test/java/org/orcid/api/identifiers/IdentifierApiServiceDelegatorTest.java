package org.orcid.api.identifiers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.pojo.IdentifierType;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-t1-web-context.xml", "classpath:orcid-t1-security-context.xml" })
public class IdentifierApiServiceDelegatorTest {

    @Resource
    IdentifierApiServiceImpl service;

    @Before
    public void init(){
        // setup security context
        ArrayList<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();
        roles.add(new SimpleGrantedAuthority("ROLE_ANONYMOUS"));
        Authentication auth = new AnonymousAuthenticationToken("anonymous", "anonymous", roles);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
    
    @Test
    public void testviewIdentifierTypes() {
        assertEquals(service.viewIdentifierTypes(null).getStatus(), 200);
        Response r = service.viewIdentifierTypes(null);
        @SuppressWarnings("unchecked")
        GenericEntity<List<IdentifierType>> types = (GenericEntity<List<IdentifierType>>) r.getEntity();
        Boolean found = false;
        for (IdentifierType t : types.getEntity()){
            if (t.getName().equals("doi")){
                assertEquals("doi: Digital object identifier",t.getDescription());
                found = true;
            }
        }
        assertTrue("no description for DOI found",found);
    }
    
    @Test
    public void testviewIdentifierTypesWithLocale() {
        assertEquals(service.viewIdentifierTypes("es").getStatus(), 200);
        Response r = service.viewIdentifierTypes("es");
        @SuppressWarnings("unchecked")
        GenericEntity<List<IdentifierType>> types = (GenericEntity<List<IdentifierType>>) r.getEntity();
        Boolean found = false;
        for (IdentifierType t : types.getEntity()){
            if (t.getName().equals("doi")){
                assertEquals("doi: Identificador de objeto digital",t.getDescription());
                found = true;
            }
        }
        assertTrue("no description for DOI found",found);
    }

    
}
