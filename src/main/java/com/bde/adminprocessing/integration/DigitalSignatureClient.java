package com.bde.adminprocessing.integration;

import com.bde.adminprocessing.domain.InputDocument;
import reactor.core.publisher.Mono;

public interface DigitalSignatureClient {

    Mono<SignatureResult> sign(InputDocument inputDocument);
}
