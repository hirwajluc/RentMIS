package com.rentmis.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${rate-limit.enabled:true}")
    private boolean rateLimitEnabled;

    @Value("${rate-limit.capacity:100}")
    private long capacity;

    @Value("${rate-limit.refill-tokens:100}")
    private long refillTokens;

    @Value("${rate-limit.refill-duration-seconds:60}")
    private long refillDurationSeconds;

    @Value("${rate-limit.auth-capacity:10}")
    private long authCapacity;

    @Value("${rate-limit.auth-refill-tokens:10}")
    private long authRefillTokens;

    private Bucket createBucket(boolean isAuth) {
        long cap = isAuth ? authCapacity : capacity;
        long tokens = isAuth ? authRefillTokens : refillTokens;
        Bandwidth limit = Bandwidth.builder()
                .capacity(cap)
                .refillGreedy(tokens, Duration.ofSeconds(refillDurationSeconds))
                .build();
        return Bucket.builder().addLimit(limit).build();
    }

    private String getClientKey(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        if (!rateLimitEnabled) {
            filterChain.doFilter(request, response);
            return;
        }

        boolean isAuth = request.getServletPath().startsWith("/api/auth/");
        String key = getClientKey(request) + (isAuth ? ":auth" : "");
        Bucket bucket = buckets.computeIfAbsent(key, k -> createBucket(isAuth));

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            log.warn("Rate limit exceeded for IP: {}", getClientKey(request));
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getWriter(),
                    Map.of("error", "Too many requests. Please try again later.",
                           "status", 429));
        }
    }
}
