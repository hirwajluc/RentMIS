package com.rentmis.dto.response;

import com.rentmis.model.enums.Language;
import com.rentmis.model.enums.Role;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String nationalId;
    private Role role;
    private Language language;
    private Boolean isActive;
    private Boolean isVerified;
    private String address;
    private String profileImage;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;

    // Contract creation flags — populated only by getAvailableTenants()
    private Boolean flagged;
    private List<String> flagReasons;
}
