package com.SE330_Q22.payment_service.client;

import com.SE330_Q22.payment_service.config.MomoProperties;
import com.SE330_Q22.payment_service.dto.momo.MomoCreateRequest;
import com.SE330_Q22.payment_service.dto.momo.MomoCreateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class MomoClient {

    private final MomoProperties momoProperties;

    private final RestClient restClient = RestClient.create();

    public MomoCreateResponse createPayment(MomoCreateRequest request) {
        return restClient.post()
                .uri(momoProperties.getEndpoint())
                .body(request)
                .retrieve()
                .body(MomoCreateResponse.class);
    }
}