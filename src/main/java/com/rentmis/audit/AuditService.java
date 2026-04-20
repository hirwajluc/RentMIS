package com.rentmis.audit;

import com.rentmis.model.entity.AuditLog;
import com.rentmis.repository.AuditLogRepository;
import com.rentmis.security.jwt.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    @Async
    public void log(String action, String entityType, Long entityId,
                    String oldValue, String newValue, String ip, String status) {
        try {
            AuditLog audit = AuditLog.builder()
                    .action(action)
                    .entityType(entityType)
                    .entityId(entityId)
                    .oldValue(oldValue)
                    .newValue(newValue)
                    .ipAddress(ip)
                    .status(status)
                    .build();

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof CustomUserDetails user) {
                audit.setUserId(user.getUserId());
                audit.setUserEmail(user.getUsername());
            }

            auditLogRepository.save(audit);
        } catch (Exception e) {
            log.error("Failed to save audit log: {}", e.getMessage());
        }
    }

    @Async
    public void log(String action, String entityType, Long entityId, String ip) {
        log(action, entityType, entityId, null, null, ip, "SUCCESS");
    }
}
