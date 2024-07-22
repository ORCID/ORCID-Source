package org.orcid.utils.alerting.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.orcid.utils.alerting.SlackManager;
import org.orcid.utils.jersey.JerseyClientHelper;
import org.orcid.utils.jersey.JerseyClientResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.core.MediaType;

/**
 * 
 * @author Will Simpson
 *
 */
public class SlackManagerImpl implements SlackManager {

    @Value("${org.orcid.core.slack.webhookUrl}")
    private String webhookUrl;

    @Value("${org.orcid.core.slack.channel}")
    private String channel;

    @Resource
    private JerseyClientHelper jerseyClientHelper;

    private ObjectMapper mapper = new ObjectMapper();
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SlackManagerImpl.class);

    @Override
    public void sendSystemAlert(String message) {
        if (StringUtils.isNotBlank(webhookUrl)) {
            Map<String, String> bodyMap = new HashMap<>();
            bodyMap.put("text", message);
            bodyMap.put("channel", channel);
            try {
                send(mapper.writeValueAsString(bodyMap));
            } catch (JsonProcessingException e) {
                LOGGER.error("Unable to transform map into string: " + bodyMap.toString());
                throw new RuntimeException(e);
            }
        }
    }
    
    @Override
    public void sendAlert(String message, String customChannel, String from) {
        if (StringUtils.isNotBlank(webhookUrl)) {
            sendAlert(message,customChannel,from,webhookUrl);
        }
    }
    
    @Override
    public void sendAlert(String message, String customChannel, String from, String webhookUrl) {
        if (StringUtils.isNotBlank(webhookUrl)) {
            Map<String, String> bodyMap = new HashMap<>();
            bodyMap.put("text", message);
            bodyMap.put("channel", customChannel);
            bodyMap.put("username", from);
            try {
                send(mapper.writeValueAsString(bodyMap), webhookUrl);
            } catch (JsonProcessingException e) {
                LOGGER.error("Unable to transform map into string: " + bodyMap.toString());
                throw new RuntimeException(e);
            }
        }
    }
    
    private void send(String bodyJson) {
        JerseyClientResponse<String, String> response = jerseyClientHelper.executePostRequest(webhookUrl, new MediaType(), bodyJson, String.class, String.class);
        int status = response.getStatus();
        if (status != 200) {
            LOGGER.warn("Unable to send message to Slack: \n{}", bodyJson);
        }
    }
    
    private void send(String bodyJson, String webhookUrl) {
        JerseyClientResponse<String, String> response = jerseyClientHelper.executePostRequest(webhookUrl, new MediaType(), bodyJson, String.class, String.class);
        int status = response.getStatus();
        if (status != 200) {
            LOGGER.warn("Unable to send message to Slack: \n{}", bodyJson);
        }
    }

}
