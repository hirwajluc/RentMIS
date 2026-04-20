package com.rentmis.mapper;

import com.rentmis.dto.response.ContractResponse;
import com.rentmis.model.entity.Contract;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class, UnitMapper.class})
public interface ContractMapper {
    ContractResponse toResponse(Contract contract);
}
