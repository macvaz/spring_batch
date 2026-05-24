package com.bde.adminprocessing.integration;

import com.bde.adminprocessing.domain.InputDocument;
import com.bde.adminprocessing.domain.OutputDocument;
import reactor.core.publisher.Mono;

public interface NotificationClient {

    Mono<NotificationResult> notifyCitizen(InputDocument inputDocument, OutputDocument outputDocument);
}
