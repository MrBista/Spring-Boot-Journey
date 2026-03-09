package com.bisma.foundation.practice_materi_4_7.maper;

import com.bisma.foundation.practice_materi_4_7.dto.UserReqDTO;
import com.bisma.foundation.practice_materi_4_7.dto.UserResponseDTO;
import com.bisma.foundation.practice_materi_4_7.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDTO toResponse(User user);


    User toEntity(UserReqDTO user);

    List<UserResponseDTO> toResponseList(List<User> users);
}
