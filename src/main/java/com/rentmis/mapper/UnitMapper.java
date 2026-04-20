package com.rentmis.mapper;

import com.rentmis.dto.response.UnitResponse;
import com.rentmis.model.entity.Unit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface UnitMapper {
    @Mapping(target = "propertyId", source = "property.id")
    @Mapping(target = "propertyName", source = "property.name")
    @Mapping(target = "wingId", source = "wing.id")
    @Mapping(target = "wingName", source = "wing.name")
    UnitResponse toResponse(Unit unit);
}
