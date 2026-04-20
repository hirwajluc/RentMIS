package com.rentmis.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class WingRequest {
    @NotNull
    private Long propertyId;

    @NotBlank @Size(max = 100)
    private String name;

    @Size(max = 300)
    private String description;
}
