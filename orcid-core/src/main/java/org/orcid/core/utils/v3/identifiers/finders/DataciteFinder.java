package org.orcid.core.utils.v3.identifiers.finders;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.utils.v3.identifiers.normalizers.DOINormalizer;
import org.orcid.jaxb.model.v3.rc1.common.TransientNonEmptyString;
import org.orcid.jaxb.model.v3.rc1.record.ExternalID;
import org.orcid.jaxb.model.v3.rc1.record.ExternalIDs;
import org.orcid.pojo.FindMyStuffResult;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class DataciteFinder implements Finder{

    @Resource
    DOINormalizer norm;
    
    private static final String serviceName = "datacite";
    private static final String metadataEndpoint = "https://api.datacite.org/works?query=";
   
    //https://api.datacite.org/works?query=0000-0003-1419-2405 
    
    @Override
    public FindMyStuffResult find(String orcid, ExternalIDs existingIDs) {
        FindMyStuffResult result = new FindMyStuffResult();
        result.setServiceName(getServiceName());
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(metadataEndpoint + orcid).openConnection();
            con.addRequestProperty("Accept", "application/json");
            con.setRequestMethod("GET");
            con.setInstanceFollowRedirects(true);
            if (con.getResponseCode() == 200) {
                ObjectMapper objectMapper = new ObjectMapper();
                DataciteSearchResult dcResult = objectMapper.readValue(con.getInputStream(), DataciteSearchResult.class);  
                for (DataciteSimpleWork w : dcResult.data){
                    if (!existingIDs.getExternalIdentifier().contains(w.getExternalID())){
                        result.getResults().add(new FindMyStuffResult.Result(w.id, "doi", w.title));
                    }
                }
            }
            
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String getServiceName() {
        return serviceName;
    }
    
    //query datacite
    //{data:[
    //  {attributes":{"doi":"","title":""}},
    //  {attributes":{"doi":"","title":""}}
    // ]}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DataciteSearchResult{
        public List<DataciteSimpleWork> data; 
        public DataciteSearchResult(){};
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DataciteSimpleWork{
        public String id;
        public String title;
        public DataciteSimpleWork(){};
        public ExternalID getExternalID(){
            ExternalID eid = new ExternalID();
            eid.setType("doi");
            eid.setValue(id);
            eid.setNormalized(new TransientNonEmptyString(id));
            return eid;
        }
    }

}
