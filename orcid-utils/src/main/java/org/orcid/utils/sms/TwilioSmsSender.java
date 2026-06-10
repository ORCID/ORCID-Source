package org.orcid.utils.sms;

import jakarta.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

@Component
public class TwilioSmsSender implements SmsSender {

    public static final String PROVIDER = "twilio";

    @Value("${org.orcid.sms.twilio.accountSid:}")
    private String accountSid;

    @Value("${org.orcid.sms.twilio.authToken:}")
    private String authToken;

    @Value("${org.orcid.sms.twilio.fromNumber:}")
    private String fromNumber;

    @Value("${org.orcid.sms.twilio.messagingServiceSid:}")
    private String messagingServiceSid;

    @Override
    public String getProvider() {
        return PROVIDER;
    }

    @PostConstruct
    public void initTwilioClient() {
        if (StringUtils.isNotBlank(accountSid) && StringUtils.isNotBlank(authToken)) {
            Twilio.init(accountSid, authToken);
        }
    }

    @Override
    public SmsSendResult send(SmsMessage smsMessage) {
        if (StringUtils.isBlank(accountSid) || StringUtils.isBlank(authToken)) {
            return SmsSendResult.failure(PROVIDER, "TWILIO_NOT_CONFIGURED", "Twilio account SID and auth token are required");
        }

        try {
            Message message;
            if (StringUtils.isNotBlank(messagingServiceSid)) {
                message = Message.creator(new PhoneNumber(smsMessage.getTo()), messagingServiceSid, smsMessage.getBody()).create();
            } else {
                if (StringUtils.isBlank(fromNumber)) {
                    return SmsSendResult.failure(PROVIDER, "TWILIO_SENDER_NOT_CONFIGURED",
                            "Twilio sender number is required; set org.orcid.sms.twilio.fromNumber");
                }
                message = Message.creator(new PhoneNumber(smsMessage.getTo()), new PhoneNumber(fromNumber), smsMessage.getBody()).create();
            }
            return SmsSendResult.success(PROVIDER, message.getSid(), message.getStatus() == null ? "SENT" : message.getStatus().toString());
        } catch (Exception e) {
            return SmsSendResult.failure(PROVIDER, e.getClass().getSimpleName(), e.getMessage());
        }
    }
}
