package com.bde.adminprocessing.integration;

import com.bde.adminprocessing.domain.InputDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@ConditionalOnProperty(name = "integration.signature.mock", havingValue = "false")
public class HttpDigitalSignatureClient implements DigitalSignatureClient {

    private final WebClient webClient;

    public HttpDigitalSignatureClient(
            WebClient.Builder webClientBuilder,
            @Value("${integration.signature.base-url}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    @Override
    public Mono<SignatureResult> sign(InputDocument inputDocument) {
        return webClient.post()
                .uri("/api/v1/sign")
                .bodyValue(Map.of(
                        "documentCode", inputDocument.getDocument().getDocumentCode(),
                        "contentReference", inputDocument.getDocument().getContentReference(),
                        "citizenId", inputDocument.getCitizenId()))
                .retrieve()
                .bodyToMono(SignatureResult.class);
    }
}
