package com.bde.adminprocessing.integration;

import com.bde.adminprocessing.domain.InputDocument;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@ConditionalOnProperty(name = "integration.signature.mock", havingValue = "true", matchIfMissing = true)
public class MockDigitalSignatureClient implements DigitalSignatureClient {

    @Override
    public Mono<SignatureResult> sign(InputDocument inputDocument) {
        String reference = "SIG-" + UUID.randomUUID();
        String signedRef = inputDocument.getDocument().getContentReference() + ".signed";
        return Mono.just(new SignatureResult(reference, signedRef));
    }
}
