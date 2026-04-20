package com.rentmis.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    // Accepts either "email" or "username" field (frontend sends "username")
    private String email;
    private String username;

    @NotBlank
    private String password;

    /** Returns whichever of email/username was provided */
    public String resolveEmail() {
        if (email != null && !email.isBlank()) return email.trim();
        if (username != null && !username.isBlank()) return username.trim();
        return "";
    }
}
