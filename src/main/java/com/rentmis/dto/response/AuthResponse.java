package com.rentmis.dto.response;

import com.rentmis.model.enums.Language;
import com.rentmis.model.enums.Role;
import lombok.Builder;
import lombok.Data;

@Data @Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;
    private Long userId;
    private String email;
    private String fullName;
    private Role role;
    private Language language;
}
