package org.orcid.listener.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.orcid.utils.rest.RESTHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.core.Response;

@Component
public class SystemAlertsUtil {
    
    Logger LOG = LoggerFactory.getLogger(SystemAlertsUtil.class);
    
    static ObjectMapper mapper = new ObjectMapper();

    @Value("${org.orcid.core.slack.webhookUrl:}")
    private String webhookUrl;
    
    @Resource
    private RESTHelper httpHelper;
    
    public void sendSystemAlert(String message) {
        if (StringUtils.isNotBlank(webhookUrl)) {
            Map<String, String> bodyMap = new HashMap<>();
            bodyMap.put("text", message);
            String bodyJson = null;
            try {
                bodyJson = mapper.writeValueAsString(bodyMap);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Response response = httpHelper.postMessage(webhookUrl, bodyJson);
            int status = response.getStatus();
            if (status != 200) {
                LOG.warn("Unable to send message to Slack, status={}, error={}, message={}", new Object[] { status, response.readEntity(String.class), message });
            }
        }
    }
}
