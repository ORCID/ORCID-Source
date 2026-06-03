package org.orcid.utils.sms;

public interface SmsSender {

    String getProvider();

    SmsSendResult send(SmsMessage smsMessage);
}
