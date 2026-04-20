package com.rentmis.service.impl;

import com.rentmis.audit.AuditService;
import com.rentmis.dto.request.LoginRequest;
import com.rentmis.dto.request.RegisterRequest;
import com.rentmis.dto.response.AuthResponse;
import com.rentmis.exception.RentMISException;
import com.rentmis.integration.nida.NidaService;
import com.rentmis.model.entity.User;
import com.rentmis.model.enums.Role;
import com.rentmis.repository.UserRepository;
import com.rentmis.security.jwt.CustomUserDetails;
import com.rentmis.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final AuditService auditService;
    private final NidaService nidaService;

    @Transactional
    public AuthResponse login(LoginRequest request, String ip) {
        // Check if user exists
        String email = request.resolveEmail();
        if (email.isBlank()) throw RentMISException.unauthorized("Invalid email or password");

        User user = userRepository.findByEmailAndIsActiveTrue(email)
                .orElseThrow(() -> RentMISException.unauthorized("Invalid email or password"));

        // Check if locked
        if (user.isAccountLocked()) {
            throw RentMISException.forbidden("Account temporarily locked. Try again later.");
        }

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.getPassword()));

            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

            String accessToken = jwtTokenProvider.generateToken(userDetails);
            String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

            // Update user
            user.setRefreshToken(refreshToken);
            user.setRefreshTokenExpiry(LocalDateTime.now().plusDays(7));
            user.setLastLogin(LocalDateTime.now());
            user.setFailedLoginAttempts(0);
            user.setLockedUntil(null);
            userRepository.save(user);

            auditService.log("AUTH_LOGIN", "User", user.getId(), null, null, ip, "SUCCESS");

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtTokenProvider.getExpirationTime())
                    .userId(user.getId())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .role(user.getRole())
                    .language(user.getLanguage())
                    .build();

        } catch (Exception e) {
            // Increment failed attempts
            int attempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(attempts);
            if (attempts >= 5) {
                user.setLockedUntil(LocalDateTime.now().plusMinutes(15));
                log.warn("Account locked for {} after {} failed attempts", user.getEmail(), attempts);
            }
            userRepository.save(user);
            auditService.log("AUTH_LOGIN_FAILED", "User", user.getId(), null, null, ip, "FAILURE");
            throw e;
        }
    }

    @Transactional
    public AuthResponse refreshToken(String refreshToken) {
        User user = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> RentMISException.unauthorized("Invalid refresh token"));

        if (user.getRefreshTokenExpiry() == null ||
                LocalDateTime.now().isAfter(user.getRefreshTokenExpiry())) {
            throw RentMISException.unauthorized("Refresh token expired");
        }

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String newAccessToken = jwtTokenProvider.generateToken(userDetails);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

        user.setRefreshToken(newRefreshToken);
        user.setRefreshTokenExpiry(LocalDateTime.now().plusDays(7));
        userRepository.save(user);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getExpirationTime())
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();
    }

    @Transactional
    public void logout(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            user.setRefreshToken(null);
            user.setRefreshTokenExpiry(null);
            userRepository.save(user);
        });
    }

    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw RentMISException.conflict("Email already in use");
        }
        if (request.getNationalId() != null && !request.getNationalId().isBlank()
                && userRepository.existsByNationalId(request.getNationalId())) {
            throw RentMISException.conflict("National ID already registered");
        }

        // Fetch NIDA photo for identity verification
        String nidaPhoto = null;
        if (request.getNationalId() != null && !request.getNationalId().isBlank()) {
            try {
                java.util.Map<String, Object> nidaData = nidaService.lookup(request.getNationalId());
                // IPPIS API returns the photo under "profileImage" key
                Object photo = nidaData.get("profileImage");
                if (photo instanceof String s && !s.isBlank()) {
                    nidaPhoto = s;
                }
            } catch (Exception e) {
                log.warn("NIDA photo fetch failed during registration for NID {}: {}",
                        request.getNationalId(), e.getMessage());
            }
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .nationalId(request.getNationalId())
                .role(request.getRole() != null ? request.getRole() : Role.TENANT)
                .language(request.getLanguage() != null ? request.getLanguage() : com.rentmis.model.enums.Language.ENGLISH)
                .address(request.getAddress())
                .profileImage(nidaPhoto)
                .isActive(true)
                .isVerified(nidaPhoto != null)   // auto-verify if NIDA returned a photo
                .build();

        return userRepository.save(user);
    }
}
