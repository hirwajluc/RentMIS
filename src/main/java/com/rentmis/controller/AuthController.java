package com.rentmis.controller;

import com.rentmis.dto.request.LoginRequest;
import com.rentmis.dto.request.RegisterRequest;
import com.rentmis.dto.response.ApiResponse;
import com.rentmis.dto.response.AuthResponse;
import com.rentmis.dto.response.UserResponse;
import com.rentmis.integration.nida.NidaService;
import com.rentmis.mapper.UserMapper;
import com.rentmis.model.enums.Role;
import com.rentmis.repository.UserRepository;
import com.rentmis.security.jwt.CustomUserDetails;
import com.rentmis.service.impl.AuthServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceImpl authService;
    private final UserMapper userMapper;
    private final NidaService nidaService;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(
            @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        String ip = getClientIp(httpRequest);
        AuthResponse auth = authService.login(request, ip);

        // Build frontend-compatible payload: { token, user: { role_name, ... } }
        Map<String, Object> user = new HashMap<>();
        user.put("id", auth.getUserId());
        user.put("email", auth.getEmail());
        user.put("full_name", auth.getFullName());
        user.put("role_name", toFrontendRole(auth.getRole()));
        // also expose modern fields
        user.put("role", auth.getRole());
        user.put("language", auth.getLanguage() != null ? auth.getLanguage().name() : "ENGLISH");

        Map<String, Object> data = new HashMap<>();
        data.put("token", auth.getAccessToken());
        data.put("accessToken", auth.getAccessToken());
        data.put("refreshToken", auth.getRefreshToken());
        data.put("tokenType", "Bearer");
        data.put("expiresIn", auth.getExpiresIn());
        data.put("user", user);

        return ResponseEntity.ok(ApiResponse.ok("Login successful", data));
    }

    private String toFrontendRole(Role role) {
        if (role == null) return "admin";
        return switch (role) {
            case TENANT   -> "tenant";
            case LANDLORD -> "house_owner";
            case AGENT    -> "agent";
            default       -> "admin";
        };
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        var user = authService.register(request);
        return ResponseEntity.status(201)
                .body(ApiResponse.ok("Registration successful", userMapper.toResponse(user)));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Refresh token required"));
        }
        return ResponseEntity.ok(ApiResponse.ok(authService.refreshToken(refreshToken)));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @AuthenticationPrincipal CustomUserDetails user) {
        authService.logout(user.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Logged out successfully", null));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> me(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return userRepository.findById(userDetails.getUserId())
                .map(u -> ResponseEntity.ok(ApiResponse.ok(userMapper.toResponse(u))))
                .orElse(ResponseEntity.ok(ApiResponse.ok(
                        userMapper.toResponse(com.rentmis.model.entity.User.builder()
                                .id(userDetails.getUserId())
                                .email(userDetails.getUsername())
                                .role(userDetails.getRole())
                                .build()))));
    }

    /**
     * Proxy to GoodLink NIDA service.
     * Public endpoint — no auth required.
     * Returns { foreName, surnames, photo } or { error: "..." }
     */
    @GetMapping("/verify-nid/{nid}")
    public ResponseEntity<ApiResponse<java.util.Map<String, Object>>> verifyNid(
            @PathVariable String nid) {
        // Strip spaces/dashes
        String cleanNid = nid.replaceAll("[^0-9]", "");
        java.util.Map<String, Object> result = nidaService.lookup(cleanNid);
        boolean hasError = result.containsKey("error");
        return ResponseEntity.ok(ApiResponse.ok(hasError ? "NID lookup failed" : "NID found", result));
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        return (ip != null && !ip.isBlank()) ? ip.split(",")[0].trim() : request.getRemoteAddr();
    }
}
