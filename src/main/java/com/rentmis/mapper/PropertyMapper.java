package com.rentmis.mapper;

import com.rentmis.dto.response.PropertyResponse;
import com.rentmis.model.entity.Property;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface PropertyMapper {
    @Mapping(target = "occupiedUnits", ignore = true)
    @Mapping(target = "availableUnits", ignore = true)
    @Mapping(target = "floors", ignore = true)
    PropertyResponse toResponse(Property property);
}
