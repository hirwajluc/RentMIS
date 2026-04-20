package com.rentmis.integration.glspay;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rentmis.model.entity.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GLSPayService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${glspay.base-url:http://86.48.7.218:3030}")
    private String baseUrl;

    @Value("${glspay.api-key:GLS-qiZdaJTLCy6oXl4irHETYiJLYJSNTdwH}")
    private String apiKey;

    /**
     * Initiates a GLSPay checkout session.
     * Amount is sent as a plain RWF string (no cents conversion).
     */
    public GLSPayCheckoutResponse initiatePayment(Payment payment, String callbackUrl, String returnUrl) {
        try {
            String externalId = payment.getReferenceNumber() + "-" + System.currentTimeMillis();
            String description = String.format("Rent for %s %s %d-%d",
                    payment.getUnit().getUnitNumber(),
                    payment.getUnit().getProperty() != null ? payment.getUnit().getProperty().getName() : "",
                    payment.getPaymentPeriodMonth(),
                    payment.getPaymentPeriodYear());

            Map<String, Object> body = new HashMap<>();
            body.put("amount", String.format("%.0f", payment.getTotalAmount()));
            body.put("currency", "RWF");
            body.put("externalId", externalId);
            body.put("description", description);
            body.put("provider", "mtnmomo");
            body.put("returnUrl",   returnUrl + "?pay=" + payment.getReferenceNumber());
            body.put("cancelUrl",   returnUrl + "?pay=" + payment.getReferenceNumber() + "&status=cancelled");
            body.put("webhookUrl",  callbackUrl);
            body.put("callbackUrl", callbackUrl);
            body.put("metadata", Map.of(
                    "payment_id", String.valueOf(payment.getId()),
                    "payment_no", payment.getReferenceNumber()
            ));

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, buildHeaders());
            ResponseEntity<Map> response = restTemplate.exchange(
                    baseUrl + "/api/v1/checkout/sessions",
                    HttpMethod.POST, request, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<?, ?> data = (Map<?, ?>) response.getBody().get("data");
                if (data != null) {
                    String sessionId = (String) data.get("sessionId");
                    String checkoutUrl = (String) data.get("checkoutUrl");
                    return GLSPayCheckoutResponse.builder()
                            .transactionId(sessionId)
                            .checkoutUrl(checkoutUrl)
                            .status("PENDING")
                            .build();
                }
            }
            log.warn("GLSPay unexpected response for {}", payment.getReferenceNumber());
            return buildFallbackCheckout(payment);

        } catch (Exception e) {
            log.error("GLSPay initiation failed for {}: {}", payment.getReferenceNumber(), e.getMessage());
            return buildFallbackCheckout(payment);
        }
    }

    /**
     * Polls GLSPay for session status.
     * Returns SUCCESSFUL / FAILED / CANCELLED / PENDING.
     */
    public GLSPayStatusResponse verifyPayment(String sessionId) {
        try {
            HttpEntity<Void> request = new HttpEntity<>(buildHeaders());
            ResponseEntity<Map> response = restTemplate.exchange(
                    baseUrl + "/api/v1/checkout/" + sessionId + "/status",
                    HttpMethod.GET, request, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<?, ?> data = (Map<?, ?>) response.getBody().get("data");
                if (data != null) {
                    String status = (String) data.get("status");
                    // Normalise to our internal statuses
                    String mapped = switch (status != null ? status.toUpperCase() : "") {
                        case "SUCCESSFUL" -> "COMPLETED";
                        case "FAILED"     -> "FAILED";
                        case "CANCELLED"  -> "CANCELLED";
                        default           -> "PENDING";
                    };
                    return GLSPayStatusResponse.builder()
                            .transactionId(sessionId)
                            .status(mapped)
                            .build();
                }
            }
        } catch (HttpClientErrorException e) {
            // 410 Gone = session expired
            if (e.getStatusCode() == HttpStatus.GONE || e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return GLSPayStatusResponse.builder()
                        .transactionId(sessionId)
                        .status("SESSION_EXPIRED")
                        .build();
            }
            log.error("GLSPay status check error for {}: {}", sessionId, e.getMessage());
        } catch (Exception e) {
            log.error("GLSPay status check error for {}: {}", sessionId, e.getMessage());
        }
        return GLSPayStatusResponse.builder().transactionId(sessionId).status("PENDING").build();
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-API-Key", apiKey);
        return headers;
    }

    private GLSPayCheckoutResponse buildFallbackCheckout(Payment payment) {
        // Fallback so UI can show something even if API is unreachable
        String mockSession = "MOCK-" + payment.getReferenceNumber();
        return GLSPayCheckoutResponse.builder()
                .transactionId(mockSession)
                .checkoutUrl(baseUrl + "/checkout/" + mockSession)
                .status("PENDING")
                .build();
    }
}
