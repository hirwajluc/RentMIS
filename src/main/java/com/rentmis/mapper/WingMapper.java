package com.rentmis.mapper;

import com.rentmis.dto.response.WingResponse;
import com.rentmis.model.entity.Wing;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WingMapper {
    @Mapping(target = "propertyId", source = "property.id")
    @Mapping(target = "propertyName", source = "property.name")
    @Mapping(target = "unitCount", ignore = true)
    WingResponse toResponse(Wing wing);
}
