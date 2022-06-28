package org.orcid.core.manager.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.manager.SlackManager;
import org.orcid.core.utils.JsonUtils;
import org.orcid.utils.rest.RESTHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import jakarta.ws.rs.core.Response;

/**
 * 
 * @author Will Simpson
 *
 */
public class SlackManagerImpl implements SlackManager {

    @Value("${org.orcid.core.slack.webhookUrl:}")
    private String webhookUrl;

    @Value("${org.orcid.core.slack.channel}")
    private String channel;

    @Resource
    private RESTHelper httpHelper;

    private static final Logger LOGGER = LoggerFactory.getLogger(SlackManagerImpl.class);

    @Override
    public void sendSystemAlert(String message) {
        if (StringUtils.isNotBlank(webhookUrl)) {
            Map<String, String> bodyMap = new HashMap<>();
            bodyMap.put("text", message);
            bodyMap.put("channel", channel);
            send(JsonUtils.convertToJsonString(bodyMap));
        }
    }
    
    @Override
    public void sendAlert(String message, String customChannel, String from) {
        if (StringUtils.isNotBlank(webhookUrl)) {
            Map<String, String> bodyMap = new HashMap<>();
            bodyMap.put("text", message);
            bodyMap.put("channel", customChannel);
            bodyMap.put("username", from);
            send(JsonUtils.convertToJsonString(bodyMap));
        }
    }
    
    private void send(String bodyJson) {
        Response response = httpHelper.postMessage(webhookUrl, bodyJson);
        int status = response.getStatus();
        if (status != 200) {
            LOGGER.warn("Unable to send message to Slack: \n{}", bodyJson);
        }
    }

}
