package com.rentmis.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LinkageRequest {

    @NotNull
    private Long propertyId;

    /** Optional — specific unit the lead is interested in. */
    private Long unitId;

    @NotBlank @Size(max = 200)
    private String tenantLeadName;

    @NotBlank @Size(max = 30)
    @Pattern(regexp = "^[+\\d][\\d\\s\\-]{6,28}$", message = "Invalid phone number")
    private String tenantLeadPhone;

    @Size(max = 200)
    private String tenantLeadEmail;

    @Size(max = 1000)
    private String notes;
}
