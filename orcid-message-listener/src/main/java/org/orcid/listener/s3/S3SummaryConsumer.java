package org.orcid.listener.s3;

import java.util.function.Consumer;

import javax.annotation.Resource;

import org.orcid.utils.listener.LastModifiedMessage;
import org.orcid.utils.listener.RetryMessage;
import org.springframework.stereotype.Component;

@Component
public class S3SummaryConsumer implements Consumer<LastModifiedMessage> {

    @Resource
    S3MessageProcessor proc;

    @Override
    public void accept(LastModifiedMessage message) {
        proc.update20Summary(message);
    }

    public void accept(RetryMessage message) {
        proc.update20Summary(message);
    }
}
