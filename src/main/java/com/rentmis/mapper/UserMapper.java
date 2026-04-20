package com.rentmis.mapper;

import com.rentmis.dto.response.UserResponse;
import com.rentmis.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toResponse(User user);
}
