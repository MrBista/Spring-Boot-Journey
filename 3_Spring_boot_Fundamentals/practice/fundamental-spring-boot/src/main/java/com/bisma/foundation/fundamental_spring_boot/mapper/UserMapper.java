package com.bisma.foundation.fundamental_spring_boot.mapper;

import com.bisma.foundation.fundamental_spring_boot.dto.UserReqDto;
import com.bisma.foundation.fundamental_spring_boot.dto.UserResponseDto;
import com.bisma.foundation.fundamental_spring_boot.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    UserResponseDto toResponseDTO(User user);

    @Mapping(target = "id", ignore = true)
    User toEntity(UserReqDto userReqDto);
}
