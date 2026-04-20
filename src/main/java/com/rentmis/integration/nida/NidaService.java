package com.rentmis.integration.nida;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Proxies National ID lookups to the GoodLink NIDA service.
 * Returns foreName, surnames, and photo so the UI can auto-populate fields.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NidaService {

    private final RestTemplate restTemplate;

    @Value("${nida.api-url:https://selfservice.ippis.rw/api/profile/user-identity}")
    private String apiUrl;

    /**
     * Look up a National ID number.
     * @param nid 16-digit National ID (spaces/dashes stripped by caller)
     * @return map with keys: firstName, lastName (or error message)
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> lookup(String nid) {
        try {
            String url = apiUrl + "/" + nid;
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return (Map<String, Object>) response.getBody();
            }
            return Map.of("error", "NID not found");
        } catch (Exception e) {
            log.warn("NIDA lookup failed for NID {}: {}", nid, e.getMessage());
            return Map.of("error", "NID service unavailable");
        }
    }
}
