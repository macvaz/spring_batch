package com.bde.adminprocessing.integration;

import com.bde.adminprocessing.domain.InputDocument;
import com.bde.adminprocessing.domain.OutputDocument;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@ConditionalOnProperty(name = "integration.notification.mock", havingValue = "true", matchIfMissing = true)
public class MockNotificationClient implements NotificationClient {

    @Override
    public Mono<NotificationResult> notifyCitizen(InputDocument inputDocument, OutputDocument outputDocument) {
        String reference = "NOTIF-" + UUID.randomUUID();
        return Mono.just(new NotificationResult(reference, "ELECTRONIC_REGISTER"));
    }
}
