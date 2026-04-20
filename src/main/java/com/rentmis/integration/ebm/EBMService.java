package com.rentmis.integration.ebm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rentmis.model.entity.Invoice;
import com.rentmis.model.entity.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class EBMService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${ebm.base-url:https://backend-qa.inkomane.rw}")
    private String baseUrl;

    @Value("${ebm.client-id:bacf8e00b-54bb-4c04-a395-4e728dda2c35}")
    private String clientId;

    @Value("${ebm.client-secret:K9W8+RZqx1g5CFfPyIbzZnF8rjKiZ5xE1Lt+MH1Ot38=}")
    private String clientSecret;

    @Value("${ebm.company-id:cdfbb33e-1255-49fc-9af1-11d92c7686c3}")
    private String companyId;

    @Value("${ebm.branch-id:e5166abc-dae3-4dfc-9c5e-ad22b16c918a}")
    private String branchId;

    private static final BigDecimal VAT_RATE = new BigDecimal("0.18");
    private static final DateTimeFormatter ISO_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneOffset.UTC);

    // Simple in-memory token cache (starts expired so first call always fetches fresh)
    private final AtomicReference<String>  cachedToken      = new AtomicReference<>();
    private volatile long                  tokenExpiresAt   = 0; // 0 = expired

    // ─── Public API ────────────────────────────────────────────────────────────

    public EBMInvoiceResponse submitInvoice(Payment payment, Invoice invoice) {
        String step = "connecting to EBM server";
        try {
            step = "authenticating with EBM server";
            String token = getToken();

            step = "retrieving items from EBM server";
            String itemId = getItemId(token);

            step = "registering sale on EBM server";
            String saleId = createSale(token, itemId, payment);

            step = "generating EBM receipt";
            return generateReceipt(token, saleId, payment, invoice);

        } catch (Exception e) {
            String userMessage = "EBM generation failed while " + step + ". Please try again later.";
            log.error("[EBM] {} — invoice {}: {}", userMessage, invoice.getInvoiceNumber(), e.getMessage(), e);
            return buildErrorResponse(userMessage);
        }
    }

    public BigDecimal calculateVAT(BigDecimal amount) {
        return amount.multiply(VAT_RATE)
                .divide(BigDecimal.ONE.add(VAT_RATE), 2, RoundingMode.HALF_UP);
    }

    // ─── Token ─────────────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private String getToken() {
        if (cachedToken.get() != null && System.currentTimeMillis() < tokenExpiresAt) {
            return cachedToken.get();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Client-Id", clientId);
        headers.set("Client-Secret", clientSecret);

        ResponseEntity<Map> res = restTemplate.exchange(
                baseUrl + "/api/traceability/better-integration/token",
                HttpMethod.POST, new HttpEntity<>(Map.of(), headers), Map.class);

        Map<?, ?> body = res.getBody();
        String token = null;
        if (body != null) {
            for (String key : List.of("token", "access_token", "accessToken")) {
                if (body.get(key) instanceof String s) { token = s; break; }
            }
        }
        if (token == null) throw new RuntimeException("EBM token response missing token field");

        cachedToken.set(token);
        tokenExpiresAt = System.currentTimeMillis() + 50 * 60 * 1000L; // 50 minutes
        return token;
    }

    // ─── Item ID ───────────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private String getItemId(String token) {
        ResponseEntity<Map> res = restTemplate.exchange(
                baseUrl + "/api/traceability/rent-integration/company/" + companyId,
                HttpMethod.GET, new HttpEntity<>(bearerHeaders(token)), Map.class);

        Object data = res.getBody() != null ? res.getBody().get("data") : null;
        if (data instanceof List<?> list && !list.isEmpty()) {
            Object first = list.get(0);
            if (first instanceof Map<?,?> m) return (String) m.get("id");
        }
        throw new RuntimeException("EBM: could not find item ID for company " + companyId);
    }

    // ─── Partner lookup (customer / supplier by TIN) ──────────────────────────

    /** Returns the EBM partner UUID for a given TIN, or null if not found. */
    @SuppressWarnings("unchecked")
    private String findPartnerIdByTin(String token, String partnerType, String tin) {
        if (tin == null || tin.isBlank()) return null;
        try {
            String url = baseUrl + "/api/traceability/rent-integration/get-suppliers-or-customers/by-company/"
                    + companyId + "?partnerType=" + partnerType;
            ResponseEntity<Map> res = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(bearerHeaders(token)), Map.class);
            Object raw = res.getBody() != null ? res.getBody().get("data") : null;
            List<?> list = raw instanceof List<?> l ? l : null;
            if (list != null) {
                for (Object item : list) {
                    if (item instanceof Map<?,?> m) {
                        String bTin = m.get("businessTin") instanceof String s ? s : "";
                        if (tin.equalsIgnoreCase(bTin) && m.get("id") instanceof String id) return id;
                    }
                }
            }
        } catch (Exception e) {
            log.warn("[EBM] Partner lookup failed ({} / {}): {}", partnerType, tin, e.getMessage());
        }
        return null;
    }

    // ─── Create Sale ───────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private String createSale(String token, String itemId, Payment payment) {
        String tName   = payment.getTenant().getFirstName() + " " + payment.getTenant().getLastName();
        String tEmail  = payment.getTenant().getEmail();
        String tPhone  = payment.getTenant().getPhone() != null ? payment.getTenant().getPhone() : "";
        String tNid    = payment.getTenant().getNationalId() != null ? payment.getTenant().getNationalId() : "";

        // Landlord (supplier) details
        String lName   = payment.getUnit().getProperty() != null
                && payment.getUnit().getProperty().getLandlord() != null
                ? payment.getUnit().getProperty().getLandlord().getFirstName()
                + " " + payment.getUnit().getProperty().getLandlord().getLastName()
                : "RentMIS Landlord";
        String lEmail  = payment.getUnit().getProperty() != null
                && payment.getUnit().getProperty().getLandlord() != null
                ? payment.getUnit().getProperty().getLandlord().getEmail() : "";
        String lNid    = payment.getUnit().getProperty() != null
                && payment.getUnit().getProperty().getLandlord() != null
                ? (payment.getUnit().getProperty().getLandlord().getNationalId() != null
                   ? payment.getUnit().getProperty().getLandlord().getNationalId() : "") : "";
        String lPhone  = payment.getUnit().getProperty() != null
                && payment.getUnit().getProperty().getLandlord() != null
                ? (payment.getUnit().getProperty().getLandlord().getPhone() != null
                   ? payment.getUnit().getProperty().getLandlord().getPhone() : "") : "";

        // Look up registered partner IDs in EBM (required for verification code to be returned)
        String customerId  = findPartnerIdByTin(token, "CUSTOMER",  tNid);
        String supplierId  = findPartnerIdByTin(token, "SUPPLIER",  lNid);
        log.info("[EBM] Partner lookup — customer: {}, supplier: {}", customerId, supplierId);

        int months = 1;
        BigDecimal unitPrice = payment.getTotalAmount()
                .divide(BigDecimal.valueOf(months), 2, RoundingMode.HALF_UP);

        String saleDate = ISO_FMT.format(Instant.now());

        Map<String, Object> sale = new HashMap<>();
        sale.put("companyId",                companyId);
        sale.put("itemId",                   itemId);
        sale.put("quantity",                 months);
        sale.put("unitPrice",                unitPrice);
        sale.put("saleDate",                 saleDate);
        sale.put("customerId",               customerId);
        sale.put("customerBusinessTin",      tNid);
        sale.put("customerBusinessName",     tName);
        sale.put("customerEmail",            tEmail);
        sale.put("customerAddress",          "");
        sale.put("customerContactPersonName",tName);
        sale.put("customerPhoneNumber",      tPhone);

        Map<String, Object> purchase = new HashMap<>();
        purchase.put("supplierId",               supplierId);
        purchase.put("supplierBusinessTin",      lNid);
        purchase.put("supplierBusinessName",     lName);
        purchase.put("supplierEmail",            lEmail);
        purchase.put("supplierAddress",          "");
        purchase.put("supplierContactPersonName",lName);
        purchase.put("supplierPartnerType",      "SUPPLIER");
        purchase.put("supplierPhoneNumber",      lPhone);

        Map<String, Object> body = Map.of("sale", sale, "purchase", purchase);

        ResponseEntity<Map> res = restTemplate.exchange(
                baseUrl + "/api/traceability/rent-integration/sales",
                HttpMethod.POST, new HttpEntity<>(body, jsonBearerHeaders(token)), Map.class);

        Object data = res.getBody() != null ? res.getBody().get("data") : null;
        if (data instanceof Map<?,?> m && m.get("id") instanceof String id) return id;

        // Fallback: find most recent sale without receipts
        return findLatestSaleId(token);
    }

    @SuppressWarnings("unchecked")
    private String findLatestSaleId(String token) {
        ResponseEntity<Map> res = restTemplate.exchange(
                baseUrl + "/api/traceability/rent-integration/sales/by-company/" + companyId + "?size=1",
                HttpMethod.GET, new HttpEntity<>(bearerHeaders(token)), Map.class);
        Object data = res.getBody() != null ? res.getBody().get("data") : null;
        if (data instanceof List<?> list && !list.isEmpty()) {
            Object first = list.get(0);
            if (first instanceof Map<?,?> m) return (String) m.get("id");
        }
        throw new RuntimeException("EBM: could not resolve sale ID");
    }

    // ─── Generate EBM Receipt ──────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private EBMInvoiceResponse generateReceipt(String token, String saleId, Payment payment, Invoice invoice) {
        String paymentMethod = resolvePaymentMethod(payment.getPaymentMethod());
        String phone = payment.getTenant().getPhone() != null ? payment.getTenant().getPhone() : "";

        Map<String, Object> body = new HashMap<>();
        body.put("receiptType",          "S");
        body.put("branchId",             branchId);
        body.put("paymentMethod",        paymentMethod);
        body.put("customerOption",       "PHONE");
        body.put("customerPhoneNumber",  phone);
        body.put("refundReason",         "01");
        body.put("saleType",             "N");
        body.put("purchaseCode",         "000000");

        ResponseEntity<Map> res = restTemplate.exchange(
                baseUrl + "/api/traceability/rent-integration/sales/generate-ebm-receipt/" + saleId,
                HttpMethod.POST, new HttpEntity<>(body, jsonBearerHeaders(token)), Map.class);

        Map<?, ?> respBody = res.getBody();
        if (respBody == null) throw new RuntimeException("EBM receipt: empty response");

        log.info("[EBM] generate-receipt raw response: {}", safeJson(respBody));

        // Response structure (v3): { "success": true, "data": { "ebmVerificationCode": "...",
        //   "response": { "resultCd": "000", "data": { "rcptNo": 47, "intrlData": "...", "rcptSign": "..." } } } }
        // Note: on QA env ebmVerificationCode may be null — fall back to intrlData (internal receipt code)
        String verCode = null;
        String rcptNo  = null;
        try {
            Object dataObj = respBody.get("data");
            if (dataObj instanceof Map<?,?> data) {
                // Primary: ebmVerificationCode
                if (data.get("ebmVerificationCode") instanceof String s && !s.isBlank()) verCode = s;

                // Inner response data: rcptNo + intrlData (fallback verCode)
                if (data.get("response") instanceof Map<?,?> inner &&
                    inner.get("data") instanceof Map<?,?> rcptData) {
                    if (rcptData.get("rcptNo") != null) rcptNo = rcptData.get("rcptNo").toString();
                    // Use intrlData as verification code when ebmVerificationCode is null
                    if (verCode == null && rcptData.get("intrlData") instanceof String s && !s.isBlank()) {
                        verCode = s;
                        log.info("[EBM] Using intrlData as verification code: {}", verCode);
                    }
                }

                // Other fallback field names on data level
                if (verCode == null) {
                    for (String key : List.of("verificationCode", "code", "vc", "receipt_code", "ebmCode")) {
                        if (data.get(key) instanceof String s && !s.isBlank()) { verCode = s; break; }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("[EBM] Error parsing receipt response: {}", e.getMessage());
        }

        if (verCode == null) {
            log.warn("[EBM] Receipt response missing verification code. Full response: {}", safeJson(respBody));
            throw new RuntimeException("EBM receipt generated but verification code not found in response");
        }

        String ebmNumber = rcptNo != null ? "EBM-" + rcptNo : "EBM-" + System.currentTimeMillis();
        String verUrl = "https://qa.inkomane.rw/ebm/" + verCode;

        return EBMInvoiceResponse.builder()
                .ebmInvoiceNumber(ebmNumber)
                .ebmVerificationCode(verCode)
                .qrCode(verCode)
                .verificationUrl(verUrl)
                .status("SUCCESS")
                .rawResponse(safeJson(respBody))
                .build();
    }

    // ─── Helpers ───────────────────────────────────────────────────────────────

    private String resolvePaymentMethod(String method) {
        if (method == null) return "06";
        return switch (method.toUpperCase()) {
            case "CASH"        -> "01";
            case "CARD"        -> "02";
            case "BANK"        -> "04";
            case "MOMO","GLSPAY" -> "05";
            default            -> "06";
        };
    }

    private HttpHeaders bearerHeaders(String token) {
        HttpHeaders h = new HttpHeaders();
        h.set("Authorization", "Bearer " + token);
        return h;
    }

    private HttpHeaders jsonBearerHeaders(String token) {
        HttpHeaders h = bearerHeaders(token);
        h.setContentType(MediaType.APPLICATION_JSON);
        return h;
    }

    private String safeJson(Object obj) {
        try { return objectMapper.writeValueAsString(obj); } catch (Exception e) { return "{}"; }
    }

    private EBMInvoiceResponse buildErrorResponse(String userMessage) {
        return EBMInvoiceResponse.builder()
                .status("FAILED")
                .errorMessage(userMessage)
                .rawResponse(userMessage)
                .build();
    }
}
