package org.orcid.utils.sms;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;

@Component
public class AwsSnsSmsSender implements SmsSender {

    public static final String PROVIDER = "aws";

    @Value("${org.orcid.sms.aws.region:us-east-2}")
    private String region;

    @Value("${org.orcid.sms.aws.accessKey:}")
    private String accessKey;

    @Value("${org.orcid.sms.aws.secretKey:}")
    private String secretKey;

    private AmazonSNS amazonSNS;

    @Override
    public String getProvider() {
        return PROVIDER;
    }

    @Override
    public SmsSendResult send(SmsMessage smsMessage) {
        try {
            PublishResult result = getAmazonSNS().publish(new PublishRequest().withPhoneNumber(smsMessage.getTo()).withMessage(smsMessage.getBody()));
            return SmsSendResult.success(PROVIDER, result.getMessageId(), "SENT");
        } catch (Exception e) {
            return SmsSendResult.failure(PROVIDER, e.getClass().getSimpleName(), e.getMessage());
        }
    }

    private AmazonSNS getAmazonSNS() {
        if (amazonSNS == null) {
            AmazonSNSClientBuilder builder = AmazonSNSClientBuilder.standard().withRegion(Regions.fromName(region));
            if (StringUtils.isNotBlank(accessKey) && StringUtils.isNotBlank(secretKey)) {
                builder.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)));
            } else {
                builder.withCredentials(DefaultAWSCredentialsProviderChain.getInstance());
            }
            amazonSNS = builder.build();
        }
        return amazonSNS;
    }

    void setAmazonSNS(AmazonSNS amazonSNS) {
        this.amazonSNS = amazonSNS;
    }
}
