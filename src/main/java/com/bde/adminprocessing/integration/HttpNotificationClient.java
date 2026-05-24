package com.bde.adminprocessing.integration;

import com.bde.adminprocessing.domain.InputDocument;
import com.bde.adminprocessing.domain.OutputDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@ConditionalOnProperty(name = "integration.notification.mock", havingValue = "false")
public class HttpNotificationClient implements NotificationClient {

    private final WebClient webClient;

    public HttpNotificationClient(
            WebClient.Builder webClientBuilder,
            @Value("${integration.notification.base-url}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    @Override
    public Mono<NotificationResult> notifyCitizen(InputDocument inputDocument, OutputDocument outputDocument) {
        return webClient.post()
                .uri("/api/v1/notifications")
                .bodyValue(Map.of(
                        "citizenId", inputDocument.getCitizenId(),
                        "outputDocumentCode", outputDocument.getDocument().getDocumentCode(),
                        "signatureReference", outputDocument.getSignatureReference()))
                .retrieve()
                .bodyToMono(NotificationResult.class);
    }
}
