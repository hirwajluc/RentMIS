package com.rentmis.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WingResponse {
    private Long id;
    private String name;
    private String description;
    private Long propertyId;
    private String propertyName;
    private Boolean isActive;
    private int unitCount;
    private LocalDateTime createdAt;
}
